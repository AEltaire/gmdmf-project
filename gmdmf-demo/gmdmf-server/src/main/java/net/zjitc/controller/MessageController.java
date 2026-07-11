package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.entity.Message;
import net.zjitc.result.Result;
import net.zjitc.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "消息相关接口")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController extends BaseController{

    private final MessageService messageService;

    @Operation(summary = "发送消息")
    @GetMapping("/send")
    private Result<List<Message>> send(Long manageId, String content) {

        boolean flag = messageService.send(manageId, content);
        return toGetAjax(flag, "发送信息成功");
    }

    @Operation(summary = "查询消息")
    @GetMapping("{id}")
    private Result<List<Message>> findByManageId(@PathVariable("id") Long manageId) {

        List<Message> manageVOS = messageService.findByManageId(manageId);
        return toGetAjax(CollUtil.isEmpty(manageVOS), "获取信息成功", manageVOS);
    }
}
