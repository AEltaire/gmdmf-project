package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ApprovalDTO;
import net.zjitc.result.Result;
import net.zjitc.service.ApprovalService;
import net.zjitc.vo.ApprovalVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "审批相关接口")
@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController extends BaseController {

    private final ApprovalService approvalService;

    // 教师端推送审批
    @Operation(summary = "推送审批")
    @PostMapping
    private Result<ApprovalVO> save(@RequestBody ApprovalDTO approvalDTO) {

        boolean flag = approvalService.saveApproval(approvalDTO);
        return toPostAjax(flag, "推送审批成功");
    }

    // 管理员端处理审批
    @Operation(summary = "审批处理")
    @PutMapping
    private Result<ApprovalVO> update(@RequestBody ApprovalDTO approvalDTO) {

        System.out.println(approvalDTO);
        boolean flag = approvalService.updateApproval(approvalDTO);
        return toPutAjax(flag, "审批处理成功");
    }

    // 查询全部审批
    @Operation(summary = "查询所有审批")
    @GetMapping
    private Result<List<ApprovalVO>> findAll() {

        List<ApprovalVO> approvalVOS = approvalService.findAllApproval();
        return toGetAjax(CollUtil.isEmpty(approvalVOS), "查询成功", approvalVOS);
    }
}
