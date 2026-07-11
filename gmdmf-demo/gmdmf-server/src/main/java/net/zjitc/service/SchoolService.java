package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.SchoolDTO;
import net.zjitc.entity.School;
import net.zjitc.vo.SchoolVO;

import java.util.List;

public interface SchoolService extends IService<School> {

    boolean saveOrUpdateSchool(SchoolDTO schoolDTO);

    boolean deleteSchool(Long id);

    List<SchoolVO> findAll();

    List<SchoolVO> findAllSchoolAndDepartment();
}
