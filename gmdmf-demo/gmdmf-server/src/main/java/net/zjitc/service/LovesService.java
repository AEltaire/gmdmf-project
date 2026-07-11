package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.Loves;
import net.zjitc.vo.CourseVO;

import java.util.List;


public interface LovesService extends IService<Loves> {

    boolean saveLoves(Loves loves);

    boolean deleteLoves(Long id);

    List<CourseVO> findAll();
}
