package net.zjitc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.result.Result;
import net.zjitc.service.ResourceService;
import net.zjitc.vo.ResourceVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "资源相关接口")
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "根据课程id查询资源文件")
    @GetMapping("{id}")
    private Result<Map<String, List<ResourceVO>>> findByCourseId(@PathVariable("id") Long courseId) {

        Map<String, List<ResourceVO>> resourceMap = resourceService.findByCourseId(courseId);
        return Result.success(Code.GET_OK, "查询资源文件列表成功", resourceMap);
    }
}
