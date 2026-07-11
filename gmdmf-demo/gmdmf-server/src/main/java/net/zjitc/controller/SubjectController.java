package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.result.Result;
import net.zjitc.dto.SubjectDTO;
import net.zjitc.service.SubjectService;
import net.zjitc.vo.SubjectVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "题目相关接口")
@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController extends BaseController {

    private final SubjectService subjectService;

    @Operation(summary = "添加题目")
    @PostMapping
    private Result<SubjectVO> save(@RequestBody SubjectDTO subjectDTO) {

        System.out.println(subjectDTO);
        boolean flag = subjectService.saveSubject(subjectDTO);
        return toPostAjax(flag, "题目添加成功");
    }

    @Operation(summary = "删除题目")
    @DeleteMapping("{id}")
    private Result<SubjectVO> delete(@PathVariable("id") Long id) {

        boolean flag = subjectService.deleteSubject(id);
        return toDeleteAjax(flag, "题目删除成功");
    }


    @Operation(summary = "修改题目")
    @PutMapping
    private Result<SubjectVO> update(@RequestBody SubjectDTO subjectDTO) {

        boolean flag = subjectService.updateSubject(subjectDTO);
        return toPutAjax(flag, "题目修改成功");
    }

    @Operation(summary = "查询所有")
    @GetMapping("/warehouseId/{id}")
    private Result<Map<String, List<SubjectVO>>> findByWarehouseId(@PathVariable("id") Long warehouseId) {

        Map<String, List<SubjectVO>> subjectVOS = subjectService.findByWarehouseId(warehouseId);
        return toGetAjax(CollUtil.isEmpty(subjectVOS), "题目查询成功", subjectVOS);
    }
}
