package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.WarehouseDTO;
import net.zjitc.entity.Teacher;
import net.zjitc.entity.Warehouse;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.WarehouseMapper;
import net.zjitc.query.WarehouseQuery;
import net.zjitc.page.PageBean;
import net.zjitc.service.WarehouseService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.WarehouseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public boolean saveOrUpdateWarehouse(WarehouseDTO warehouseDTO) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();
        Warehouse warehouse = BeanUtil.copyProperties(warehouseDTO, Warehouse.class);
        warehouse.setTeacherId(ThreadLocalUtil.getId()).setSchool(ThreadLocalUtil.getSchool()).setCreateTime(LocalDateTime.now());
        return saveOrUpdate(warehouse);
    }

    @Override
    public boolean deleteWarehouse(Long id) {
        try {
            return removeById(id);
        } catch (Exception e) {
            throw new BusinessException(Code.DELETE_ERR, "该题库下存在关联数据，无法删除");
        }
    }

    @Override
    public WarehouseVO findById(Long id) {

        CheckEmptyOrRuleUtil.isEmptyId(id);
        Warehouse warehouse = getById(id);
        return convertor(warehouse);
    }

    @Override
    public List<WarehouseVO> findByName(String warehouseName) {

        List<Warehouse> warehouses = lambdaQuery().like(StrUtil.isNotBlank(warehouseName), Warehouse::getName, warehouseName).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(warehouses)) return Collections.emptyList();
        return warehouses.stream().map(this::convertor).toList();
    }

    @Override
    public List<WarehouseVO> findByTeacherId() {

        List<Warehouse> warehouses = lambdaQuery().eq(Warehouse::getTeacherId, ThreadLocalUtil.getId()).list();
        return warehouses.stream().map(this::convertor).toList();
    }

    @Override
    public PageBean<WarehouseVO> findByPage(WarehouseQuery warehouseQuery) {

        if (warehouseQuery.getPageSize() == null || warehouseQuery.getPageSize() == 0) warehouseQuery.setPageSize(10);
        if (warehouseQuery.getCurrentPage() == null || warehouseQuery.getCurrentPage() == 0)
            warehouseQuery.setCurrentPage(1);

        Page<Warehouse> page = lambdaQuery()
                .eq(StrUtil.isNotBlank(warehouseQuery.getName()), Warehouse::getName, warehouseQuery.getName())
                .eq(StrUtil.isNotBlank(warehouseQuery.getSchool()), Warehouse::getSchool, warehouseQuery.getSchool())
                .eq(Warehouse::getTeacherId, ThreadLocalUtil.getId())
                .page(new Page<>(warehouseQuery.getCurrentPage(), warehouseQuery.getPageSize()));

        if (CheckEmptyOrRuleUtil.isEmptyPage(page))
            return new PageBean<WarehouseVO>().setTotal(0L).setPageNum(0L).setList(Collections.emptyList());

        return PageBean.of(page, this::convertor);
    }

    private WarehouseVO convertor(Warehouse warehouse) {

        Long teacherId = warehouse.getTeacherId();
        CheckEmptyOrRuleUtil.isEmptyId(teacherId);
        Teacher teacher = Db.lambdaQuery(Teacher.class).eq(Teacher::getId, teacherId).select(Teacher::getName).one();
        WarehouseVO warehouseVO = BeanUtil.copyProperties(warehouse, WarehouseVO.class);

        return warehouseVO.setTeacherName(teacher.getName());
    }
}
