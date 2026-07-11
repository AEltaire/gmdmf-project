package net.zjitc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.result.Result;
import net.zjitc.service.AIChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI相关接口")
@RestController
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService AIChatService;

    @Operation(summary = "聊天接口")
    @GetMapping("/chat")
    public Result<String> chat(String input) {

        String chat = AIChatService.chat(input);
        return Result.success(Code.GET_OK, "操作成功", chat);
    }
}
