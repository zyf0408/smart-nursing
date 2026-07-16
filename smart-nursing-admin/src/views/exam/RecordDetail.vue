<template>
  <div class="app-container">
    <div class="card-box">
      <div class="edit-header">
        <el-page-header @back="goBack">
          <template #content>
            <span class="page-title">成绩详情</span>
          </template>
        </el-page-header>
      </div>

      <!-- 基本信息 -->
      <el-descriptions :column="3" border class="info-box">
        <el-descriptions-item label="考生姓名">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="考试名称">{{ detail.examTitle }}</el-descriptions-item>
        <el-descriptions-item label="得分">
          <span :style="{ color: detail.score >= detail.passScore ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
            {{ detail.score }} / {{ detail.totalScore }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="及格分">{{ detail.passScore }}</el-descriptions-item>
        <el-descriptions-item label="考试结果">
          <el-tag :type="detail.score >= detail.passScore ? 'success' : 'danger'">
            {{ detail.score >= detail.passScore ? '通过' : '不通过' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用时">{{ detail.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.submitTime }}</el-descriptions-item>
      </el-descriptions>

      <!-- 答题详情 -->
      <el-divider content-position="left">答题详情</el-divider>
      <div v-loading="loading">
        <div v-for="(item, index) in detail.answers" :key="index" class="question-item">
          <div class="question-header">
            <span class="question-index">{{ index + 1 }}.</span>
            <el-tag size="small" class="question-type">{{ getTypeText(item.type) }}</el-tag>
            <span class="question-score">（{{ item.score }}分）</span>
            <el-tag
              :type="item.isCorrect ? 'success' : 'danger'"
              size="small"
              class="question-result"
            >
              {{ item.isCorrect ? '正确' : '错误' }}
            </el-tag>
          </div>
          <div class="question-content">{{ item.content }}</div>
          <div class="question-answer">
            <div class="answer-row">
              <span class="answer-label">考生答案：</span>
              <span :class="['answer-value', item.isCorrect ? 'correct' : 'wrong']">
                {{ item.userAnswer || '未作答' }}
              </span>
            </div>
            <div class="answer-row" v-if="!item.isCorrect">
              <span class="answer-label">正确答案：</span>
              <span class="answer-value correct">{{ item.correctAnswer }}</span>
            </div>
            <div class="answer-row" v-if="item.analysis">
              <span class="answer-label">解析：</span>
              <span class="answer-value">{{ item.analysis }}</span>
            </div>
          </div>
        </div>
        <el-empty v-if="!detail.answers || detail.answers.length === 0" description="暂无答题数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { recordDetail } from '@/api/examRecord'

const route = useRoute()
const router = useRouter()
const loading = ref(false)

const detail = reactive({
  username: '',
  examTitle: '',
  score: 0,
  totalScore: 0,
  passScore: 0,
  duration: 0,
  submitTime: '',
  answers: []
})

const getTypeText = (type) => {
  const map = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '简答题' }
  return map[type] || '未知'
}

const loadDetail = () => {
  if (!route.query.id) return
  loading.value = true
  recordDetail(route.query.id)
    .then((res) => {
      Object.assign(detail, res)
    })
    .catch((err) => {
      console.error('获取成绩详情失败', err)
    })
    .finally(() => {
      loading.value = false
    })
}

const goBack = () => {
  router.push('/exam/record')
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.edit-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;

  .page-title {
    font-size: 16px;
    font-weight: bold;
  }
}

.info-box {
  margin-bottom: 20px;
}

.question-item {
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;

  .question-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;

    .question-index {
      font-weight: bold;
      font-size: 15px;
    }

    .question-score {
      color: #909399;
      font-size: 13px;
    }
  }

  .question-content {
    margin-bottom: 12px;
    font-size: 14px;
    line-height: 1.6;
    color: #303133;
  }

  .question-answer {
    .answer-row {
      margin-bottom: 6px;
      font-size: 13px;
    }

    .answer-label {
      color: #909399;
    }

    .answer-value {
      color: #606266;

      &.correct {
        color: #67c23a;
        font-weight: 500;
      }

      &.wrong {
        color: #f56c6c;
        font-weight: 500;
      }
    }
  }
}
</style>
