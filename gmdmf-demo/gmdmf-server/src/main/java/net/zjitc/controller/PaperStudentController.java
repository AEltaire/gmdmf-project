package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperStudentDTO;
import net.zjitc.entity.PaperStudent;
import net.zjitc.result.Result;
import net.zjitc.service.PaperStudentService;
import net.zjitc.vo.PaperStudentVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "题集学生相关接口")
@RestController
@RequestMapping("/paperStudent")
@RequiredArgsConstructor
public class PaperStudentController extends BaseController {

    private final PaperStudentService paperStudentService;

    @Operation(summary = "提交答卷")
    @PostMapping
    private Result<PaperStudent> saveOrUpdate(@RequestBody List<PaperStudentDTO> paperStudentDTOS) {

        boolean flag = paperStudentService.saveOrUpdatePaperStudent(paperStudentDTOS);
        return toPostAjax(flag, "提交试卷成功");
    }

    @Operation(summary = "根据提交题集id查询")
    @GetMapping("{id}")
    private Result<Map<String, List<PaperStudentVO>>> findByPaperApprovalId(@PathVariable("id") Long paperApprovalId) {

        Map<String, List<PaperStudentVO>> paperStudentVOS = paperStudentService.findByPaperApprovalId(paperApprovalId);
        return toGetAjax(CollUtil.isEmpty(paperStudentVOS), "查询试卷成功", paperStudentVOS);
    }

    @Operation(summary = "根据题集id和学生id查询试卷")
    @GetMapping
    private Result<Map<String, List<PaperStudentVO>>> findByPaperIdAndStudentId(Long paperId, Long studentId) {

        Map<String, List<PaperStudentVO>> paperStudentVOS = paperStudentService.findByPaperIdAndStudentId(paperId, studentId);
        return toGetAjax(CollUtil.isEmpty(paperStudentVOS), "查询试卷成功", paperStudentVOS);
    }

    @Operation(summary = "根据课程id和学生id查询试卷")
    @GetMapping("/studentId")
    private Result<List<Object>> findByStudentId(Long studentId, Long courseId) {

        List<Object> paperStudentVOS = paperStudentService.findByStudentId(studentId, courseId);
        return toGetAjax(CollUtil.isEmpty(paperStudentVOS), "查询试卷成功", paperStudentVOS);
    }

    @Operation(summary = "批改答卷")
    @PutMapping
    private Result<PaperStudent> approval(@RequestBody List<PaperStudentDTO> paperStudentDTOS) {

        boolean flag = paperStudentService.approvalPaperStudent(paperStudentDTOS);
        return toPutAjax(flag, "批改试卷成功");
    }
}
