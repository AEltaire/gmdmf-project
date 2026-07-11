package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.result.Result;
import net.zjitc.dto.DepartmentDTO;
import net.zjitc.service.DepartmentService;
import net.zjitc.vo.DepartmentVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "院系相关接口")
@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController extends BaseController {

    private final DepartmentService departmentService;

    @Operation(summary = "添加院系")
    @PostMapping("/admin")
    private Result<DepartmentVO> save(@RequestBody DepartmentDTO departmentDTO) {

        System.out.println(departmentDTO);
        boolean flag = departmentService.saveDepartment(departmentDTO);
        return  toPostAjax(flag, "添加院系成功");
    }

    @Operation(summary = "修改院系")
    @PutMapping("/admin")
    private Result<DepartmentVO> update(@RequestBody List<DepartmentDTO> departmentDTOS) {

        System.out.println(departmentDTOS);
        boolean flag = departmentService.updateDepartment(departmentDTOS);
        return toPutAjax(flag, "修改院系成功");
    }

    @Operation(summary = "删除院系")
    @PostMapping("/admin/deleteList")
    private Result<DepartmentVO> delete(@RequestBody List<Long> ids) {

        boolean flag = departmentService.deleteDepartment(ids);
        return toPostAjax(flag, "删除院系成功");
    }

    // 管理端
    @Operation(summary = "查询所有院系 [管理]")
    @GetMapping("/admin")
    private Result<List<DepartmentVO>> adminFindAll() {

        List<DepartmentVO> departmentVOS = departmentService.adminFindAllDepartment();
        return toGetAjax(CollUtil.isEmpty(departmentVOS), "查询院系成功", departmentVOS);
    }

    @Operation(summary = "查询学校院系")
    @GetMapping("/schoolId")
    private Result<List<DepartmentVO>> findBySchoolId(Long schoolId) {

        List<DepartmentVO> departmentVOS = departmentService.findBySchoolId(schoolId);
        return toGetAjax(CollUtil.isEmpty(departmentVOS), "查询院系成功", departmentVOS);
    }

    @Operation(summary = "查询学校院系")
    @GetMapping("/schoolName")
    private Result<List<DepartmentVO>> findBySchoolName(String schoolName) {

        List<DepartmentVO> departmentVOS = departmentService.findBySchoolName(schoolName);
        return toGetAjax(CollUtil.isEmpty(departmentVOS), "查询院系成功", departmentVOS);
    }
}
