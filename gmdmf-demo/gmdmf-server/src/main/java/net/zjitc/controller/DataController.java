package net.zjitc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.result.Result;
import net.zjitc.service.DataService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "数据相关接口")
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @Operation(summary = "统计数量")
    @GetMapping("/count")
    private Result<Object> getCountData() {
        return Result.success(Code.GET_OK, "统计数量", dataService.getCountData());
    }

    @Operation(summary = "统计学校用户")
    @GetMapping("/school")
    private Result<Object> getSchoolData() {
        return Result.success(Code.GET_OK, "统计新用户", dataService.getSchoolData());
    }

    @Operation(summary = "提交分析")
    @GetMapping("/score")
    private Result<Object> getScoreData(Long studentId, Long courseId) {
        return Result.success(Code.GET_OK, "提交分析", dataService.getScoreData(studentId, courseId));
    }

    @Operation(summary = "试卷得分")
    @GetMapping("/paper")
    private Result<Object> getPaperData(Long studentId, Long courseId) {
        return Result.success(Code.GET_OK, "提交分析", dataService.getPaperData(studentId, courseId));
    }

    @Operation(summary = "试卷得分")
    @GetMapping("/ai")
    private Result<Object> getAIData(Long studentId, Long courseId) {
        Object AIData = dataService.getAIData(studentId, courseId);
        return Result.success(Code.GET_OK, "提交分析", AIData);
    }
}
