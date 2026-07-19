package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.exception.BusinessException;
import com.smart.nursing.dao.ExamMapper;
import com.smart.nursing.dao.ExamQuestionMapper;
import com.smart.nursing.dao.ExamRecordMapper;
import com.smart.nursing.dao.QuestionMapper;
import com.smart.nursing.dao.UserMapper;
import com.smart.nursing.dto.ExamRecordDto;
import com.smart.nursing.entity.*;
import com.smart.nursing.service.AiScoringService;
import com.smart.nursing.service.IExamRecordService;
import com.smart.nursing.vo.AiScoreResult;
import com.smart.nursing.vo.ExamResultVo;
import com.smart.nursing.vo.QuestionScoreResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 考试记录 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecordEntity> implements IExamRecordService {

    private static final String SUBMIT_LOCK_PREFIX = "ExamSubmit::";
    private static final long SUBMIT_LOCK_TTL_SECONDS = 60;

    private final ExamMapper examMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AiScoringService aiScoringService;

    @Override
    public IPage<ExamRecordEntity> listRecordByCondition(ExamRecordDto dto) {
        Page<ExamRecordEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return baseMapper.selectRecordPage(page, dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamResultVo submitExam(Long examId, Long userId, String answers) {
        // 1. Redis 分布式锁防重复交卷
        String lockKey = SUBMIT_LOCK_PREFIX + examId + "::" + userId;
        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", SUBMIT_LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new BusinessException(GlobalErrorCodeConstants.EXAM_ALREADY_SUBMITTED);
        }

        try {
            // 2. 查考试信息
            ExamEntity exam = examMapper.selectById(examId);
            if (exam == null) {
                throw new BusinessException(GlobalErrorCodeConstants.EXAM_NOT_AVAILABLE);
            }
            // 3. 查已考次数
            long attemptedCount = this.count(new LambdaQueryWrapper<ExamRecordEntity>()
                    .eq(ExamRecordEntity::getExamId, examId)
                    .eq(ExamRecordEntity::getUserId, userId));
            // 4. 判断 max_attempts
            if (exam.getMaxAttempts() != null && attemptedCount >= exam.getMaxAttempts()) {
                throw new BusinessException(GlobalErrorCodeConstants.EXAM_MAX_ATTEMPTS);
            }

            // 5. 查考试关联的试题
            List<ExamQuestionEntity> examQuestions = examQuestionMapper.selectList(
                    new LambdaQueryWrapper<ExamQuestionEntity>()
                            .eq(ExamQuestionEntity::getExamId, examId)
                            .orderByAsc(ExamQuestionEntity::getSortOrder));
            List<Long> questionIds = examQuestions.stream()
                    .map(ExamQuestionEntity::getQuestionId)
                    .collect(Collectors.toList());
            List<QuestionEntity> questionList = questionMapper.selectBatchIds(questionIds);

            // 6. 解析用户答案 JSON: { "questionId": "userAnswer", ... }
            Map<String, String> userAnswerMap = parseAnswers(answers);

            // 7. 自动判分（客观题精确判分，解答题AI评分）
            int totalScore = 0;
            List<Map<String, Object>> answerDetails = new ArrayList<>();
            for (QuestionEntity question : questionList) {
                String userAnswer = userAnswerMap.get(String.valueOf(question.getQuestionId()));
                QuestionScoreResult scoreResult = scoreQuestion(question, userAnswer);
                totalScore += scoreResult.getEarnedScore();

                Map<String, Object> detail = new HashMap<>();
                detail.put("questionId", question.getQuestionId());
                detail.put("questionType", question.getQuestionType());
                detail.put("content", question.getContent());
                detail.put("correctAnswer", question.getAnswer());
                detail.put("userAnswer", userAnswer);
                detail.put("isCorrect", scoreResult.isCorrect());
                detail.put("score", scoreResult.getEarnedScore());
                detail.put("fullScore", question.getScore());
                detail.put("feedback", scoreResult.getFeedback());
                detail.put("needManualReview", scoreResult.isNeedManualReview());
                answerDetails.add(detail);
            }

            // 8. 及格判定
            boolean passed = exam.getPassScore() != null && totalScore >= exam.getPassScore();

            // 9. 查找最近一条"进行中"的考试记录
            ExamRecordEntity record = this.getOne(new LambdaQueryWrapper<ExamRecordEntity>()
                    .eq(ExamRecordEntity::getExamId, examId)
                    .eq(ExamRecordEntity::getUserId, userId)
                    .eq(ExamRecordEntity::getStatus, 1)
                    .orderByDesc(ExamRecordEntity::getStartTime)
                    .last("LIMIT 1"));

            if (record == null) {
                // 没有进行中的记录，创建一条新的
                record = new ExamRecordEntity();
                record.setExamId(examId);
                record.setUserId(userId);
                record.setAttemptCount((int) attemptedCount + 1);
                record.setStartTime(LocalDateTime.now());
            }

            // 10. 保存记录 (status=2 已提交)
            record.setStatus(2);
            record.setAnswers(answers);
            record.setScore(totalScore);
            record.setIsPass(passed ? 1 : 0);
            record.setSubmitTime(LocalDateTime.now());
            this.saveOrUpdate(record);

            // 11. 组装返回结果
            return buildExamResultVo(record, exam, answerDetails);
        } finally {
            // 释放锁
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    public ExamResultVo getExamResult(Long recordId) {
        ExamRecordEntity record = this.getById(recordId);
        if (record == null) {
            return null;
        }
        ExamEntity exam = examMapper.selectById(record.getExamId());
        List<Map<String, Object>> answerDetails = new ArrayList<>();
        // 重新判分以生成答题详情
        if (record.getAnswers() != null) {
            List<ExamQuestionEntity> examQuestions = examQuestionMapper.selectList(
                    new LambdaQueryWrapper<ExamQuestionEntity>()
                            .eq(ExamQuestionEntity::getExamId, record.getExamId())
                            .orderByAsc(ExamQuestionEntity::getSortOrder));
            List<Long> questionIds = examQuestions.stream()
                    .map(ExamQuestionEntity::getQuestionId)
                    .collect(Collectors.toList());
            List<QuestionEntity> questionList = questionMapper.selectBatchIds(questionIds);
            Map<String, String> userAnswerMap = parseAnswers(record.getAnswers());
            for (QuestionEntity question : questionList) {
                String userAnswer = userAnswerMap.get(String.valueOf(question.getQuestionId()));
                QuestionScoreResult scoreResult = scoreQuestion(question, userAnswer);
                Map<String, Object> detail = new HashMap<>();
                detail.put("questionId", question.getQuestionId());
                detail.put("questionType", question.getQuestionType());
                detail.put("content", question.getContent());
                detail.put("correctAnswer", question.getAnswer());
                detail.put("userAnswer", userAnswer);
                detail.put("isCorrect", scoreResult.isCorrect());
                detail.put("score", scoreResult.getEarnedScore());
                detail.put("fullScore", question.getScore());
                detail.put("feedback", scoreResult.getFeedback());
                detail.put("needManualReview", scoreResult.isNeedManualReview());
                answerDetails.add(detail);
            }
        }
        return buildExamResultVo(record, exam, answerDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startExam(Long examId, Long userId) {
        // 1. 查考试信息
        ExamEntity exam = examMapper.selectById(examId);
        if (exam == null || exam.getStatus() == null || exam.getStatus() != 1) {
            throw new BusinessException(GlobalErrorCodeConstants.EXAM_NOT_AVAILABLE);
        }
        // 2. 校验考试时间窗口
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new BusinessException(GlobalErrorCodeConstants.EXAM_NOT_IN_TIME_RANGE);
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new BusinessException(GlobalErrorCodeConstants.EXAM_NOT_IN_TIME_RANGE);
        }
        // 3. 查已考次数（仅统计已交卷的记录，未完成的进行中记录不计入次数）
        long completedCount = this.count(new LambdaQueryWrapper<ExamRecordEntity>()
                .eq(ExamRecordEntity::getExamId, examId)
                .eq(ExamRecordEntity::getUserId, userId)
                .eq(ExamRecordEntity::getStatus, 2));
        // 4. 判断 max_attempts
        if (exam.getMaxAttempts() != null && completedCount >= exam.getMaxAttempts()) {
            throw new BusinessException(GlobalErrorCodeConstants.EXAM_MAX_ATTEMPTS);
        }
        // 5. 清理该用户此考试下未完成的进行中记录（上次中断/放弃的考试）
        this.remove(new LambdaQueryWrapper<ExamRecordEntity>()
                .eq(ExamRecordEntity::getExamId, examId)
                .eq(ExamRecordEntity::getUserId, userId)
                .eq(ExamRecordEntity::getStatus, 1));
        // 6. 计算新 attemptCount：取已有最大 attempt_count + 1
        ExamRecordEntity maxAttemptRecord = this.getOne(new LambdaQueryWrapper<ExamRecordEntity>()
                .eq(ExamRecordEntity::getExamId, examId)
                .eq(ExamRecordEntity::getUserId, userId)
                .orderByDesc(ExamRecordEntity::getAttemptCount)
                .last("LIMIT 1"));
        int nextAttemptNo = (maxAttemptRecord != null && maxAttemptRecord.getAttemptCount() != null)
                ? maxAttemptRecord.getAttemptCount() + 1 : 1;
        // 7. 创建考试记录 (status=1 进行中)
        ExamRecordEntity record = new ExamRecordEntity();
        record.setExamId(examId);
        record.setUserId(userId);
        record.setAttemptCount(nextAttemptNo);
        record.setStatus(1);
        record.setStartTime(LocalDateTime.now());
        this.save(record);
    }

    // ==================== 私有方法 ====================

    /**
     * 查询学员的考试记录（同一考试取最高分作为最终成绩）
     */
    @Override
    public List<ExamRecordEntity> listMyExamRecords(Long userId) {
        // 查询该用户所有已交卷的记录（status=2）
        List<ExamRecordEntity> allRecords = this.list(new LambdaQueryWrapper<ExamRecordEntity>()
                .eq(ExamRecordEntity::getUserId, userId)
                .eq(ExamRecordEntity::getStatus, 2)
                .orderByDesc(ExamRecordEntity::getSubmitTime));
        // 按 examId 分组，每组取最高分
        Map<Long, ExamRecordEntity> bestScoreMap = new LinkedHashMap<>();
        for (ExamRecordEntity record : allRecords) {
            ExamRecordEntity existing = bestScoreMap.get(record.getExamId());
            if (existing == null || (record.getScore() != null && (existing.getScore() == null
                    || record.getScore() > existing.getScore()))) {
                bestScoreMap.put(record.getExamId(), record);
            }
        }
        return new ArrayList<>(bestScoreMap.values());
    }

    /**
     * 解析答案 JSON
     */
    private Map<String, String> parseAnswers(String answers) {
        if (answers == null || answers.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(answers, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.error("解析答题内容失败: {}", answers, e);
            return new HashMap<>();
        }
    }

    /**
     * 题目判分（单选/多选/判断精确匹配，解答题AI评分）
     *
     * @param question   试题
     * @param userAnswer 用户答案
     * @return 判分结果
     */
    private QuestionScoreResult scoreQuestion(QuestionEntity question, String userAnswer) {
        QuestionScoreResult result = new QuestionScoreResult();
        int fullScore = question.getScore() != null ? question.getScore() : 0;
        result.setEarnedScore(0);
        result.setCorrect(false);

        Integer questionType = question.getQuestionType();
        if (questionType == null) {
            return result;
        }

        switch (questionType) {
            case 1: // 单选
            case 3: // 判断
                boolean correctExact = checkExactAnswer(question.getAnswer(), userAnswer);
                result.setCorrect(correctExact);
                result.setEarnedScore(correctExact ? fullScore : 0);
                break;
            case 2: // 多选：排序后比较
                boolean correctMulti = checkMultiAnswer(question.getAnswer(), userAnswer);
                result.setCorrect(correctMulti);
                result.setEarnedScore(correctMulti ? fullScore : 0);
                break;
            case 4: // 解答题：AI评分
                AiScoreResult aiResult = aiScoringService.scoreAnswer(
                        question.getContent(), question.getReferenceAnswer(), userAnswer, fullScore);
                result.setEarnedScore(aiResult.getScore());
                result.setCorrect(aiResult.getScore() >= fullScore);
                result.setFeedback(aiResult.getFeedback());
                result.setNeedManualReview(aiResult.isNeedManualReview());
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 精确匹配（单选/判断）
     */
    private boolean checkExactAnswer(String correctAnswer, String userAnswer) {
        if (userAnswer == null || userAnswer.isEmpty() || correctAnswer == null || correctAnswer.isEmpty()) {
            return false;
        }
        return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }

    /**
     * 多选匹配（排序后比较）
     */
    private boolean checkMultiAnswer(String correctAnswer, String userAnswer) {
        if (userAnswer == null || userAnswer.isEmpty() || correctAnswer == null || correctAnswer.isEmpty()) {
            return false;
        }
        return sortAnswer(userAnswer).equalsIgnoreCase(sortAnswer(correctAnswer));
    }

    /**
     * 对多选答案排序（按字母顺序）
     */
    private String sortAnswer(String answer) {
        char[] chars = answer.trim().toCharArray();
        Arrays.sort(chars);
        return new String(chars).toUpperCase();
    }

    /**
     * 构建考试结果 VO
     */
    private ExamResultVo buildExamResultVo(ExamRecordEntity record, ExamEntity exam, List<Map<String, Object>> answerDetails) {
        ExamResultVo vo = new ExamResultVo();
        vo.setRecordId(record.getRecordId());
        vo.setExamId(record.getExamId());
        vo.setExamName(exam != null ? exam.getExamName() : null);
        vo.setUserId(record.getUserId());
        // 查用户名
        if (record.getUserId() != null) {
            UserEntity user = userMapper.selectById(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
        }
        vo.setScore(record.getScore());
        vo.setIsPass(record.getIsPass());
        vo.setSubmitTime(record.getSubmitTime());
        vo.setAnswerDetails(answerDetails);
        return vo;
    }
}
