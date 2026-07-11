package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.DepartmentDTO;
import net.zjitc.entity.Department;
import net.zjitc.entity.Major;
import net.zjitc.entity.School;
import net.zjitc.entity.Team;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.DepartmentMapper;
import net.zjitc.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.DepartmentVO;
import net.zjitc.vo.SchoolVO;
import net.zjitc.vo.TeamVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public boolean saveDepartment(DepartmentDTO departmentDTO) {

        CheckEmptyOrRuleUtil.isManager();
        School school = Db.lambdaQuery(School.class).eq(School::getName, departmentDTO.getSchoolName()).one();
        if (school == null) {
            school = new School().setName(departmentDTO.getSchoolName()).setExhibition(true);
            Db.save(school);
        } else {
            if (!school.getExhibition()) throw new BusinessException(Code.BUSINESS_ERR, "该学校已被禁用，无法添加院系");
            Department department = lambdaQuery().eq(Department::getSchoolId, school.getId()).eq(Department::getName, departmentDTO.getName()).one();
            if (department != null) throw new BusinessException(Code.BUSINESS_ERR, "该院系已存在");
        }
        Department department = BeanUtil.copyProperties(departmentDTO, Department.class).setSchoolId(school.getId());
        return save(department);
    }

    @Override
    public boolean updateDepartment(List<DepartmentDTO> departmentDTOS) {

        CheckEmptyOrRuleUtil.isManager();
        List<Department> departments = BeanUtil.copyToList(departmentDTOS, Department.class);
        return updateBatchById(departments);
    }

    @Override
    public List<DepartmentVO> findBySchoolName(String schoolName) {

        School school = Db.lambdaQuery(School.class).eq(School::getName, schoolName).one();
        if (school == null) return Collections.emptyList();
        List<Department> departments = lambdaQuery().eq(Department::getSchoolId, school.getId()).orderByAsc(Department::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(departments)) return Collections.emptyList();
        return departments.stream().map(this::convertor).toList();
    }

    @Override
    public boolean deleteDepartment(List<Long> ids) {

        CheckEmptyOrRuleUtil.isManager();
        try {
            return removeBatchByIds(ids);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "该院系下存在关联数据，无法删除");
        }
    }

    @Override
    public List<DepartmentVO> findBySchoolId(Long schoolId) {
        List<Department> departments = lambdaQuery().eq(Department::getSchoolId, schoolId).eq(Department::getExhibition, true).orderByAsc(Department::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(departments)) return Collections.emptyList();
        return departments.stream().map(this::convertor).toList();
    }

    @Override
    public List<DepartmentVO> adminFindAllDepartment() {

        CheckEmptyOrRuleUtil.isManager();
        List<Department> departments = lambdaQuery().list();
        return BeanUtil.copyToList(departments, DepartmentVO.class);
    }

    private DepartmentVO convertor(Department department) {
        return BeanUtil.copyProperties(department, DepartmentVO.class);
    }
}
