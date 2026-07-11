package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.WarehouseDTO;
import net.zjitc.query.WarehouseQuery;
import net.zjitc.page.PageBean;
import net.zjitc.result.Result;
import net.zjitc.service.WarehouseService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.vo.WarehouseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "题库相关接口")
@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController extends BaseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "保存题库")
    @PostMapping
    private Result<WarehouseVO> saveOrUpdate(@RequestBody WarehouseDTO warehouseDTO) {

        boolean flag = warehouseService.saveOrUpdateWarehouse(warehouseDTO);
        return toPostAjax(flag, "保存题库成功");
    }

    @Operation(summary = "删除题库")
    @DeleteMapping("{id}")
    private Result<WarehouseVO> delete(@PathVariable("id") Long id) {

        boolean flag = warehouseService.deleteWarehouse(id);
        return toDeleteAjax(flag, "删除题库成功");
    }

    @Operation(summary = "根据教师id查询题库")
    @GetMapping
    private Result<List<WarehouseVO>> findByTeacherId() {

        List<WarehouseVO> warehouseVOS = warehouseService.findByTeacherId();
        return toGetAjax(CollUtil.isEmpty(warehouseVOS), "查询题库成功", warehouseVOS);
    }

    @Operation(summary = "分页查询所有题库")
    @PostMapping("/page")
    private Result<PageBean<WarehouseVO>> findByPage(@RequestBody WarehouseQuery warehouseQuery) {

        PageBean<WarehouseVO> pageBean = warehouseService.findByPage(warehouseQuery);
        return toGetAjax(pageBean.getTotal() == 0, "查询题库成功", pageBean);
    }
}
