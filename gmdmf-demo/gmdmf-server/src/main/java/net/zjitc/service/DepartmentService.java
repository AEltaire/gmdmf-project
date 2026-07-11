package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.DepartmentDTO;
import net.zjitc.entity.Department;
import net.zjitc.vo.DepartmentVO;

import java.util.List;

public interface DepartmentService extends IService<Department> {

    boolean deleteDepartment(List<Long> ids);

    List<DepartmentVO> findBySchoolId(Long schoolId);

    List<DepartmentVO> adminFindAllDepartment();

    boolean saveDepartment(DepartmentDTO departmentDTO);

    boolean updateDepartment(List<DepartmentDTO> departmentDTOS);

    List<DepartmentVO> findBySchoolName(String schoolName);
}
