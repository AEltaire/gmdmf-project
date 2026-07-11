package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.OpinionDTO;
import net.zjitc.result.Result;
import net.zjitc.service.OpinionService;
import net.zjitc.vo.OpinionVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "意见反馈相关接口")
@RestController
@RequestMapping("/opinion")
@RequiredArgsConstructor
public class OpinionController extends BaseController {

    private final OpinionService opinionService;

    @Operation(summary = "反馈提交接口")
    @PostMapping
    private Result<OpinionVO> save(@RequestBody OpinionDTO opinionDTO) {

        boolean flag = opinionService.save(opinionDTO);
        return toPostAjax(flag, "反馈成功");
    }

    @Operation(summary = "删除恶意反馈")
    @DeleteMapping("{id}")
    private Result<OpinionVO> deleteOpinion(@PathVariable("id") Long id) {

        boolean flag = opinionService.deleteOpinion(id);
        return toDeleteAjax(flag, "删除意见成功");
    }

    @Operation(summary = "查询全部")
    @GetMapping("/findAll")
    private Result<List<OpinionVO>> findAll() {

        List<OpinionVO> opinionVOS = opinionService.findAll();
        return toGetAjax(CollUtil.isEmpty(opinionVOS), "意见查询成功", opinionVOS);
    }
}
