package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.MajorDTO;
import net.zjitc.entity.*;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.MajorMapper;
import net.zjitc.service.MajorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.vo.MajorVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {


    @Override
    public boolean saveOrUpdateMajor(List<MajorDTO> majorDTO) {

        CheckEmptyOrRuleUtil.isManager();
        List<Major> majors = BeanUtil.copyToList(majorDTO, Major.class);

        Map<String, List<Major>> collect = lambdaQuery().list().stream().collect(Collectors.groupingBy(Major::getName));

        majors.forEach(major -> {
            if (collect.containsKey(major.getName()) && !collect.get(major.getName()).get(0).getId().equals(major.getId()))
                throw new BusinessException(Code.BUSINESS_ERR, major.getName() + "专业已存在");
        });

        return saveOrUpdateBatch(majors);
    }

    @Override
    public boolean deleteMajor(List<MajorDTO> majorDTO) {

        CheckEmptyOrRuleUtil.isManager();
        CheckEmptyOrRuleUtil.isEmptyList(majorDTO);

        List<Long> ids = majorDTO.stream().map(MajorDTO::getId).toList();

        List<Course> courses = Db.lambdaQuery(Course.class).in(Course::getMajorId, ids).list();
        if (!CheckEmptyOrRuleUtil.isEmptyList(courses))
            throw new BusinessException(Code.BUSINESS_ERR, "所选专业下存在课程数据，无法删除");

        List<Team> teams = Db.lambdaQuery(Team.class).in(Team::getMajorId, ids).list();
        if (!CheckEmptyOrRuleUtil.isEmptyList(teams))
            throw new BusinessException(Code.BUSINESS_ERR, "所选专业下存在班级数据，无法删除");

        return removeByIds(ids);
    }

    @Override
    public List<MajorVO> findAll() {
        List<Major> majors = lambdaQuery().eq(Major::getExhibition, true).orderByAsc(Major::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(majors)) return Collections.emptyList();
        return majors.stream().map(this::convertor).toList();
    }

    @Override
    public List<MajorVO> adminFindAll() {
        List<Major> majors = lambdaQuery().orderByAsc(Major::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(majors)) return Collections.emptyList();
        return majors.stream().map(this::convertor).toList();
    }

    private MajorVO convertor(Major major) {
        return BeanUtil.copyProperties(major, MajorVO.class);
    }
}
