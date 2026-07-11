package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.CourseDTO;
import net.zjitc.query.CourseQuery;
import net.zjitc.page.PageBean;
import net.zjitc.result.Result;
import net.zjitc.service.CourseService;

import net.zjitc.vo.CourseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "课程相关接口")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController extends BaseController {

    private final CourseService courseService;

    // 教师端
    @Operation(summary = "发布课程")
    @PostMapping
    private Result<String> save(@Parameter(description = "课程数据") @RequestBody CourseDTO courseDTO) {

        Long courseId = courseService.saveCourse(courseDTO);
        return toPostAjax(courseId == null, "保存课程信息成功", String.valueOf(courseId));
    }

    // 教师端
    @Operation(summary = "上线审批课程")
    @PostMapping("/approval/{id}")
    private Result<PageBean<CourseVO>> approvalCourse(@PathVariable("id") Long courseId) {

        boolean flag = courseService.approvalCourse(courseId);
        return toPostAjax(flag, "提交审批成功");
    }

    // 教师端
    @Operation(summary = "删除课程")
    @DeleteMapping("{id}")
    private Result<CourseVO> delete(@PathVariable("id") Long id) {

        boolean flag = courseService.deleteCourse(id);
        return toDeleteAjax(flag, "删除课程成功");
    }

    // 管理端
    @Operation(summary = "删除课程")
    @PostMapping("/deleteList")
    private Result<CourseVO> deleteList(@RequestBody List<Long> ids) {

        boolean flag = courseService.deleteCourseList(ids);
        return toPostAjax(flag, "删除课程成功");
    }

    // 教师端
    @Operation(summary = "修改课程")
    @PutMapping
    private Result<CourseVO> update(@Parameter(description = "课程数据") @RequestBody CourseDTO courseDTO) throws Exception {

        boolean flag = courseService.updateCourse(courseDTO);
        return toPutAjax(flag, "修改课程成功");
    }

    // 管理端
    @Operation(summary = "修改课程")
    @PutMapping("/updateList")
    private Result<CourseVO> updateList(@Parameter(description = "课程数据") @RequestBody List<CourseDTO> courseDTO) throws Exception {

        boolean flag = courseService.updateCourseList(courseDTO);
        return toPutAjax(flag, "修改课程成功");
    }

    // 教师端
    @Operation(summary = "根据id查询课程")
    @GetMapping("{id}")
    private Result<CourseVO> findById(@Parameter(description = "id") @PathVariable("id") Long id) {

        CourseVO courseVO = courseService.findById(id);
        return toGetAjax(courseVO == null, "查询课程成功", courseVO);
    }

    // 学生端
    @Operation(summary = "根据学生id查询课程")
    @GetMapping("/studentId")
    private Result<List<CourseVO>> findByStudentId() {

        List<CourseVO> courseVOS = courseService.findByStudentId();
        return toGetAjax(CollUtil.isEmpty(courseVOS), "查询课程成功", courseVOS);
    }

    // 教师端
    @Operation(summary = "根据教师id查询课程")
    @PostMapping("/teacherId")
    private Result<PageBean<CourseVO>> findByTeacherId(@RequestBody CourseQuery courseQuery) {

        PageBean<CourseVO> pageBean = courseService.findByTeacherId(courseQuery);
        return toPostAjax(pageBean == null, "查询课程成功", pageBean);
    }

    // 管理端
    @Operation(summary = "查询所有课程 [管理]")
    @GetMapping("/admin")
    private Result<List<CourseVO>> findAll() {

        List<CourseVO> courseVOS = courseService.findAll();
        return toGetAjax(CollUtil.isEmpty(courseVOS), "查询课程成功", courseVOS);
    }

    // 管理端
    @Operation(summary = "查询所有审批课程 [管理]")
    @GetMapping("/approval")
    private Result<List<CourseVO>> findAllApproval() {

        List<CourseVO> courseVOS = courseService.findAllApproval();
        return toGetAjax(CollUtil.isEmpty(courseVOS), "查询课程成功", courseVOS);
    }

    // 首页
    @Operation(summary = "分页查询课程")
    @GetMapping("/findBy")
    private Result<List<CourseVO>> findBy(CourseQuery courseQuery) {

        System.out.println(courseQuery);
        List<CourseVO> pageBean = courseService.findBy(courseQuery);
        return toGetAjax(CollUtil.isEmpty(pageBean), "查询课程成功", pageBean);
    }
}
