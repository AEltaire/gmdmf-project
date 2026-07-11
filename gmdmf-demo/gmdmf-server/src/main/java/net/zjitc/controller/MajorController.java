package net.zjitc.controller;


import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.MajorDTO;
import net.zjitc.result.Result;
import net.zjitc.service.MajorService;
import net.zjitc.vo.MajorVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "专业相关接口")
@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController extends BaseController {

    private final MajorService majorService;

    @Operation(summary = "添加专业")
    @PostMapping("/admin")
    private Result<MajorVO> saveOrUpdate(@RequestBody List<MajorDTO> majorDTO) {

        boolean flag = majorService.saveOrUpdateMajor(majorDTO);
        return toPostAjax(flag, "保存专业成功");
    }

    @Operation(summary = "删除专业")
    @PostMapping("/admin/delete")
    private Result<MajorVO> delete(@RequestBody List<MajorDTO> majorDTO) {

        boolean flag = majorService.deleteMajor(majorDTO);
        return toDeleteAjax(flag, "删除专业成功");
    }

    @Operation(summary = "查询所有专业")
    @GetMapping
    private Result<List<MajorVO>> findAll() {

        List<MajorVO> majorVOS = majorService.findAll();
        return toGetAjax(CollUtil.isEmpty(majorVOS), "查询专业成功", majorVOS);
    }

    @Operation(summary = "查询所有专业(admin)")
    @GetMapping("/admin")
    private Result<List<MajorVO>> adminFindAll() {

        List<MajorVO> majorVOS = majorService.adminFindAll();
        return toGetAjax(CollUtil.isEmpty(majorVOS), "查询专业成功", majorVOS);
    }
}
