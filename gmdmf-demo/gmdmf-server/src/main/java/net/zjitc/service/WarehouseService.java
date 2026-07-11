package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.WarehouseDTO;
import net.zjitc.entity.Warehouse;
import net.zjitc.query.WarehouseQuery;
import net.zjitc.page.PageBean;
import net.zjitc.vo.WarehouseVO;

import java.util.List;

public interface WarehouseService extends IService<Warehouse> {

    boolean saveOrUpdateWarehouse(WarehouseDTO warehouseDTO);

    boolean deleteWarehouse(Long id);

    // 根据 id 查询
    WarehouseVO findById(Long id);

    // 根据题库名查询
    List<WarehouseVO> findByName(String warehouseName);

    // 根据教师 id 查询全部
    List<WarehouseVO> findByTeacherId();

    PageBean<WarehouseVO> findByPage(WarehouseQuery warehouseQuery);
}
