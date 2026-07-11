package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.Tourist;
import net.zjitc.vo.CourseVO;

import java.util.List;

public interface TouristService extends IService<Tourist> {

    boolean saveTourist(Tourist tourist);

    boolean deleteTourist(Long id);

    List<CourseVO> findAll();
}
