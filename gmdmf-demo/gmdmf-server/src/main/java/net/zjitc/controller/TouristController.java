package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.entity.Tourist;
import net.zjitc.result.Result;
import net.zjitc.service.TouristService;
import net.zjitc.vo.CourseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "游客相关接口")
@RestController
@RequestMapping("/tourist")
@RequiredArgsConstructor
public class TouristController extends BaseController{

    private final TouristService touristService;

    // 加入课程
    @Operation(summary = "加入课程")
    @PostMapping
    private Result<List<Tourist>> save(@RequestBody Tourist tourist) {

        boolean flag = touristService.saveTourist(tourist);
        return toPostAjax(flag, "加入课程成功");
    }

    // 退出课程
    @Operation(summary = "退出课程")
    @DeleteMapping("{id}")
    private Result<List<Tourist>> save(@PathVariable("id") Long id) {

        boolean flag = touristService.deleteTourist(id);
        return toDeleteAjax(flag, "退出课程成功");
    }

    // 查询课程列表
    @Operation(summary = "查询课程列表")
    @GetMapping
    private Result<List<CourseVO>> findAll() {

        List<CourseVO> courseVOS = touristService.findAll();
        return toGetAjax(CollUtil.isEmpty(courseVOS), "查询课程成功", courseVOS);
    }
}
