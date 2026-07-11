package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperDTO;
import net.zjitc.query.PaperQuery;
import net.zjitc.result.Result;
import net.zjitc.service.PaperService;
import net.zjitc.vo.PaperVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "试题集相关接口")
@RestController
@RequestMapping("/paper")
@RequiredArgsConstructor
public class PaperController extends BaseController {

    private final PaperService paperService;

    @Operation(summary = "创建试题集")
    @PostMapping
    private Result<PaperVO> save(@RequestBody PaperDTO paperDTO) {

        boolean flag = paperService.savePaper(paperDTO);
        return toPostAjax(flag, "添加试题集成功");
    }

    @Operation(summary = "删除试题集")
    @DeleteMapping("{id}")
    private Result<PaperVO> delete(@PathVariable("id") Long id) {

        boolean flag = paperService.deletePaper(id);
        return toDeleteAjax(flag, "删除试题集成功");
    }

    @Operation(summary = "修改试题集")
    @PutMapping
    private Result<PaperVO> update(@RequestBody PaperDTO paperDTO) {

        boolean flag = paperService.updatePaper(paperDTO);
        return toPutAjax(flag, "修改试题集成功");
    }

    @Operation(summary = "根据id查询试题集")
    @GetMapping("{id}")
    private Result<PaperVO> findById(@PathVariable("id") Long id) {

        PaperVO paperVO = paperService.findById(id);
        return toGetAjax(paperVO == null, "查询试题集成功", paperVO);
    }

    @Operation(summary = "分页查询试题集")
    @PostMapping("/paperQuery")
    private Result<List<PaperVO>> findByPage(@RequestBody PaperQuery paperQuery) {

        List<PaperVO> pageBean = paperService.findByQuery(paperQuery);
        return toGetAjax(CollUtil.isEmpty(pageBean), "查询试题集成功", pageBean);
    }
}
