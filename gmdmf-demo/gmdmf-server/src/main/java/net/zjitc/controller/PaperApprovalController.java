package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperApprovalDTO;
import net.zjitc.result.Result;
import net.zjitc.service.PaperApprovalService;
import net.zjitc.vo.PaperApprovalVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "试卷批改相关接口")
@RestController
@RequestMapping("/paperApproval")
@RequiredArgsConstructor
public class PaperApprovalController extends BaseController {

    private final PaperApprovalService paperApprovalService;

    // 学生端
    @Operation(summary = "学生提交试卷")
    @PostMapping("/student")
    private Result<PaperApprovalVO> save(@RequestBody PaperApprovalDTO paperApprovalDTO) {

        boolean flag = paperApprovalService.saveOrUpdatePaperApproval(paperApprovalDTO);
        return toPostAjax(flag, "作业提交成功");
    }

    // 教师端
    @Operation(summary = "创建学生试卷")
    @GetMapping("/studentPaper")
    private Result<List<PaperApprovalVO>> saveStudentPaper(Long courseId, Long paperId) {

        boolean flag = paperApprovalService.saveStudentPaper(courseId, paperId);
        return toGetAjax(flag, "学生试卷创建成功");
    }

    // 教师端
    @Operation(summary = "教师批改试卷")
    @PutMapping("{id}")
    private Result<PaperApprovalVO> approval(@PathVariable("id") Long paperApprovalId) {

        boolean flag = paperApprovalService.approvalPaper(paperApprovalId);
        return toPutAjax(flag, "批改成功");
    }

    @Operation(summary = "根据课程id和题集类型查询提交")
    @GetMapping("/courseId/{id}")
    private Result<Map<Long, List<PaperApprovalVO>>> findByCourseId(@PathVariable("id") Long courseId, String type) {

        Map<Long, List<PaperApprovalVO>> workStudentVO = paperApprovalService.findByCourseId(courseId, type);
        return toGetAjax(CollUtil.isEmpty(workStudentVO), "查询成功", workStudentVO);
    }

    @Operation(summary = "根据试卷id查询作业")
    @GetMapping("/paperId/{id}")
    private Result<PaperApprovalVO> findByPaperId(@PathVariable("id") Long paperId) {

        PaperApprovalVO paperApprovalVO = paperApprovalService.findByPaperId(paperId);
        return toGetAjax(paperApprovalVO == null, "查询成功", paperApprovalVO);
    }

    // 学生端: 学生查看试卷
    @Operation(summary = "根据学生id查询作业")
    @GetMapping("/studentId")
    private Result<List<PaperApprovalVO>> findByStudentId() {

        List<PaperApprovalVO> paperApprovalVOS = paperApprovalService.findByStudentId();
        return toGetAjax(CollUtil.isEmpty(paperApprovalVOS), "查询成功", paperApprovalVOS);
    }

    // 教师端：查询所有试卷
    @Operation(summary = "查询未批改")
    @GetMapping("/teacherId")
    private Result<List<PaperApprovalVO>> findByTeacherId(@RequestParam(required = false) String type) {

        List<PaperApprovalVO> paperApprovalVO = paperApprovalService.findByTeacherId(type);
        return toGetAjax(CollUtil.isEmpty(paperApprovalVO), "查询成功", paperApprovalVO);
    }


}
