package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.result.Result;
import net.zjitc.dto.SchoolDTO;
import net.zjitc.service.SchoolService;
import net.zjitc.vo.SchoolVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学校相关接口")
@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController extends BaseController {

    private final SchoolService schoolService;

    @Operation(summary = "保存学校")
    @PostMapping("/admin")
    private Result<SchoolVO> saveOrUpdate(@RequestBody SchoolDTO schoolDTO) {

        boolean flag = schoolService.saveOrUpdateSchool(schoolDTO);
        return toPostAjax(flag, "保存学校成功");
    }

    @Operation(summary = "删除学校")
    @DeleteMapping("/admin/{id}")
    private Result<SchoolVO> delete(@PathVariable("id") Long id) {

        boolean flag = schoolService.deleteSchool(id);
        return toDeleteAjax(flag, "删除学校成功");
    }

    @Operation(summary = "查询所有学校")
    @GetMapping
    private Result<List<SchoolVO>> findAll() {

        List<SchoolVO> schoolVOS = schoolService.findAll();
        return toGetAjax(CollUtil.isEmpty(schoolVOS), "查询学校成功", schoolVOS);
    }

    // 管理端使用
    @Operation(summary = "查询所有学校")
    @GetMapping("/SchoolAndDepartment")
    private Result<List<SchoolVO>> findAllSchoolAndDepartment() {

        List<SchoolVO> schoolVOS = schoolService.findAllSchoolAndDepartment();
        return toGetAjax(CollUtil.isEmpty(schoolVOS), "查询学校成功", schoolVOS);
    }
}
