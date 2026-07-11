package net.zjitc.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.EvaluateDTO;
import net.zjitc.entity.Evaluate;
import net.zjitc.result.Result;
import net.zjitc.service.EvaluateService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "课程评价相关接口")
@RestController
@RequestMapping("/evaluate")
@RequiredArgsConstructor
public class EvaluateController extends BaseController{

    private final EvaluateService evaluateService;

    @Operation(summary = "添加评价")
    @PostMapping
    private Result<Evaluate> save(@RequestBody @Validated EvaluateDTO evaluateDTO) {

        boolean flag = evaluateService.saveEvaluate(evaluateDTO);
        return toPostAjax(flag, "课程评价成功");
    }

    @Operation(summary = "添加评价")
    @PutMapping
    private Result<Evaluate> update(@RequestBody @Validated EvaluateDTO evaluateDTO) {

        boolean flag = evaluateService.updateEvaluate(evaluateDTO);
        return toPostAjax(flag, "修改评价成功");
    }

    @Operation(summary = "查询评价")
    @GetMapping("{id}")
    private Result<Evaluate> findBySidAndCid(@PathVariable("id") Long cid) {
        Evaluate evaluate = evaluateService.findBySidAndCid(cid);
        return toGetAjax(evaluate == null, "查询评价成功", evaluate);
    }
}
