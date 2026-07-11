package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.SchoolDTO;
import net.zjitc.entity.Department;
import net.zjitc.entity.School;
import net.zjitc.entity.Team;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.SchoolMapper;
import net.zjitc.service.SchoolService;
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
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    @Override
    public boolean saveOrUpdateSchool(SchoolDTO schoolDTO) {
        CheckEmptyOrRuleUtil.isManager();
        School school = BeanUtil.copyProperties(schoolDTO, School.class);
        return saveOrUpdate(school);
    }

    @Override
    public boolean deleteSchool(Long id) {
        CheckEmptyOrRuleUtil.isManager();
        try {
            return removeById(id);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "该学校下存在关联数据，无法删除");
        }
    }

    @Override
    public List<SchoolVO> findAll() {

        List<School> schools = lambdaQuery().eq(School::getExhibition, true).orderByAsc(School::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(schools)) return Collections.emptyList();
        return schools.stream().map(this::convertor).toList();
    }

    @Override
    public List<SchoolVO> findAllSchoolAndDepartment() {

        CheckEmptyOrRuleUtil.isManager();
        List<School> schools = lambdaQuery().orderByAsc(School::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(schools)) return Collections.emptyList();
        List<SchoolVO> schoolVOList = schools.stream().map(this::convertor).toList();

        Map<Long, List<Department>> listMap = Db.lambdaQuery(Department.class).list().stream().collect(Collectors.groupingBy(Department::getSchoolId));

        return schoolVOList.stream().map(schoolVO -> {
            List<Department> departments = listMap.get(schoolVO.getId());
            return schoolVO.setDepartmentVOS(BeanUtil.copyToList(departments, DepartmentVO.class));
        }).toList();
    }

    private SchoolVO convertor(School school) {
        return BeanUtil.copyProperties(school, SchoolVO.class);
    }
}
