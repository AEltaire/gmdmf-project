package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperSubjectDTO;
import net.zjitc.result.Result;
import net.zjitc.service.PaperSubjectService;
import net.zjitc.vo.SubjectVO;
import net.zjitc.vo.PaperSubjectVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "试卷题目相关接口")
@RestController
@RequestMapping("/paperSubject")
@RequiredArgsConstructor
public class PaperSubjectController extends BaseController {

    private final PaperSubjectService paperSubjectService;

    // 教师端
    @Operation(summary = "添加试卷题目")
    @PostMapping
    private Result<PaperSubjectVO> save(@RequestBody List<PaperSubjectDTO> paperSubjectDTOList) {

        boolean flag = paperSubjectService.savePaperSubject(paperSubjectDTOList);
        return toPostAjax(flag, "作业题目添加成功");
    }

    // 教师端
    @Operation(summary = "删除作业卷")
    @DeleteMapping("{id}")
    private Result<PaperSubjectVO> delete(@PathVariable("id") Long paperId) {

        boolean flag = paperSubjectService.deleteByPaperId(paperId);
        return toDeleteAjax(flag, "作业卷删除成功");
    }

    @Operation(summary = "修改试卷题目")
    @PutMapping("{id}")
    private Result<PaperSubjectVO> update(@PathVariable("id") Long paperId, @RequestBody List<PaperSubjectDTO> paperSubjectDTOList) {

        boolean flag = paperSubjectService.updatePaperSubject(paperId, paperSubjectDTOList);
        return toPutAjax(flag, "作业题目修改成功");
    }

    @Operation(summary = "查询试卷题目")
    @GetMapping("{id}")
    private Result<Map<String, List<SubjectVO>>> findAll(@PathVariable("id") Long paperId) {

        Map<String, List<SubjectVO>> testVOS = paperSubjectService.findAll(paperId);
        return toGetAjax(CollUtil.isEmpty(testVOS), "查询作业题目成功", testVOS);
    }
}
