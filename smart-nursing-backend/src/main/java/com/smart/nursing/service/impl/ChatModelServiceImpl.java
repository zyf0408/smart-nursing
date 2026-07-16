package com.smart.nursing.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smart.nursing.service.ChatModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 流式对话 Service 实现类
 * <p>
 * 基于 Redis 保存对话历史，使用 SpringAI 的流式接口返回逐字输出。
 * 使用 Jackson ObjectMapper（注册 JavaTimeModule）替代 FastJSON 序列化历史对话。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatModelServiceImpl implements ChatModelService {

    /** Redis 历史对话 key 前缀 */
    private static final String CHAT_HISTORY_KEY_PREFIX = "AI_CHAT::";

    /** 历史对话过期时间（小时） */
    private static final long CHAT_HISTORY_TTL_HOURS = 24;

    /** 历史对话最大条数，超过则触发滑动窗口截断 */
    private static final int MAX_HISTORY_SIZE = 20;

    /** 滑动窗口截断后保留的最近消息条数 */
    private static final int SLIDING_WINDOW_KEEP = 10;

    /** 系统提示词：护理助手角色设定 */
    private static final String SYSTEM_PROMPT = "你是一个专业的老年护理培训助手，名叫'智慧护理助手'。"
            + "你的职责是回答护理相关问题，提供学习建议，辅助护理培训工作。请用专业、简洁、友好的语气回答问题。";

    private final OpenAiChatModel openAiChatModel;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 专用于对话历史序列化的 ObjectMapper（替代 FastJSON）
     * 注册 JavaTimeModule 以处理 LocalDateTime 等日期字段
     */
    private final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册 JavaTimeModule 处理 LocalDateTime 等日期字段
        mapper.registerModule(new JavaTimeModule());
        // 日期序列化为 ISO 字符串而非时间戳
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    @Override
    public Flux<String> streamChat(String sessionId, String message) {
        String redisKey = CHAT_HISTORY_KEY_PREFIX + sessionId;

        // 1. 从 Redis 获取历史对话
        List<ChatHistoryMessage> history = getHistory(redisKey);

        // 2. 滑动窗口截断：超过 20 条只保留最近 10 条
        if (history.size() > MAX_HISTORY_SIZE) {
            history = new ArrayList<>(history.subList(history.size() - SLIDING_WINDOW_KEEP, history.size()));
        }
        // history 在此之后不再修改，使用 final 引用供 lambda 捕获
        final List<ChatHistoryMessage> finalHistory = history;

        // 3. 构建消息列表：SystemMessage + 历史 + 当前 UserMessage
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));
        for (ChatHistoryMessage h : history) {
            if ("user".equals(h.getRole())) {
                messages.add(new UserMessage(h.getContent()));
            } else if ("assistant".equals(h.getRole())) {
                messages.add(new AssistantMessage(h.getContent()));
            }
        }
        messages.add(new UserMessage(message));

        // 4. 调用流式接口
        Prompt prompt = new Prompt(messages);
        StringBuilder fullReply = new StringBuilder();

        return openAiChatModel.stream(prompt)
                // 5. 每收到一块内容，提取文本并通过 Flux 发送
                .map(this::extractText)
                .doOnNext(fullReply::append)
                // 6. 流结束后，将完整回复加入历史并保存到 Redis
                .doOnComplete(() -> {
                    List<ChatHistoryMessage> newHistory = new ArrayList<>(finalHistory);
                    newHistory.add(new ChatHistoryMessage("user", message, LocalDateTime.now()));
                    newHistory.add(new ChatHistoryMessage("assistant", fullReply.toString(), LocalDateTime.now()));
                    saveHistory(redisKey, newHistory);
                })
                .onErrorResume(e -> {
                    log.error("[AI 流式对话] 调用大模型失败, sessionId={}", sessionId, e);
                    return Flux.just("AI 服务暂时不可用，请稍后重试");
                });
    }

    /**
     * 从 ChatResponse 中提取文本内容
     */
    private String extractText(ChatResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            return "";
        }
        String text = response.getResult().getOutput().getText();
        return text != null ? text : "";
    }

    /**
     * 从 Redis 读取并反序列化对话历史
     */
    private List<ChatHistoryMessage> getHistory(String key) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<ChatHistoryMessage> list = objectMapper.readValue(json, new TypeReference<List<ChatHistoryMessage>>() {});
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            log.error("[AI 流式对话] 反序列化对话历史失败, key={}", key, e);
            return new ArrayList<>();
        }
    }

    /**
     * 序列化并保存对话历史到 Redis（TTL 24h）
     */
    private void saveHistory(String key, List<ChatHistoryMessage> history) {
        try {
            String json = objectMapper.writeValueAsString(history);
            stringRedisTemplate.opsForValue().set(key, json, CHAT_HISTORY_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("[AI 流式对话] 保存对话历史失败, key={}", key, e);
        }
    }

    /**
     * 对话历史消息（用于 Redis 序列化存储）
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ChatHistoryMessage {

        /** 角色：user / assistant */
        @JsonProperty("role")
        private String role;

        /** 消息内容 */
        @JsonProperty("content")
        private String content;

        /** 时间戳 */
        @JsonProperty("timestamp")
        private LocalDateTime timestamp;
    }
}
