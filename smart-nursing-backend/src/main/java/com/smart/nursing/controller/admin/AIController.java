package com.smart.nursing.controller.admin;

import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.service.ChatModelService;
import com.smart.nursing.service.ImageGenerationService;
import com.smart.nursing.service.LanguageModelService;
import com.smart.nursing.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * AI 智能 Controller
 * <p>
 * 提供 AI 同步问答、流式问答、试题生成、个性化推荐、文生图等接口。
 */
@Tag(name = "AI 智能", description = "AI 问答、试题生成、个性化推荐、文生图")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final LanguageModelService languageModelService;
    private final ChatModelService chatModelService;
    private final ImageGenerationService imageGenerationService;
    private final RecommendService recommendService;

    @Operation(summary = "同步问答")
    @PostMapping("/chat/sync")
    public CommonResult<String> chatSync(@RequestParam String message) {
        String reply = languageModelService.generateText(message);
        return CommonResult.success(reply);
    }

    @Operation(summary = "流式问答")
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String sessionId,
                                   @RequestParam String message) {
        return chatModelService.streamChat(sessionId, message);
    }

    @Operation(summary = "AI 生成试题")
    @PostMapping("/question/generate")
    public CommonResult<String> generateQuestion(@RequestParam Long examId,
                                                 @RequestParam String questionType) {
        String prompt = String.format(
                "你是一名老年护理培训试题专家。请为考试ID为%d的考试生成%s类型的护理培训试题，"
                        + "要求包含题干、选项（A/B/C/D）和正确答案，并以JSON数组格式返回。"
                        + "题型说明：1-单选，2-多选，3-判断。", examId, questionType);
        String result = languageModelService.generateText(prompt);
        return CommonResult.success(result);
    }

    @Operation(summary = "个性化推荐")
    @GetMapping("/recommend/{userId}")
    public CommonResult<List<Map<String, Object>>> recommend(@PathVariable Long userId) {
        List<Map<String, Object>> list = recommendService.recommend(userId);
        return CommonResult.success(list);
    }

    @Operation(summary = "文生图")
    @PostMapping("/image/generate")
    public CommonResult<String> generateImage(@RequestParam String prompt) {
        String result = imageGenerationService.generateImage(prompt);
        return CommonResult.success(result);
    }
}
