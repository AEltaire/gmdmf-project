package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.entity.Loves;
import net.zjitc.result.Result;
import net.zjitc.service.LovesService;
import net.zjitc.vo.CourseVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "喜爱课程相关接口")
@RestController
@RequestMapping("/loves")
@RequiredArgsConstructor
public class LovesController extends BaseController {

    private final LovesService lovesService;

    @Operation(summary = "加入喜欢")
    @PostMapping
    private Result<Loves> save(@RequestBody @Validated Loves loves) {

        boolean flag = lovesService.saveLoves(loves);
        return toPostAjax(flag, "喜欢课程成功");
    }

    // 退出课程
    @Operation(summary = "取消喜欢")
    @DeleteMapping("{id}")
    private Result<Loves> delete(@PathVariable("id") Long id) {

        boolean flag = lovesService.deleteLoves(id);
        return toDeleteAjax(flag, "取消喜欢");
    }

    // 查询课程列表
    @Operation(summary = "查询喜欢")
    @GetMapping
    private Result<List<CourseVO>> findAll() {

        List<CourseVO> courseVOS = lovesService.findAll();
        return toGetAjax(CollUtil.isEmpty(courseVOS), "查询课程成功", courseVOS);
    }
}
