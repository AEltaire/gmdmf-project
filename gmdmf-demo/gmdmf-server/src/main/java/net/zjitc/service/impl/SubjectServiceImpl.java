package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.SubjectDTO;
import net.zjitc.entity.Subject;
import net.zjitc.entity.Warehouse;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.SubjectMapper;
import net.zjitc.service.SubjectService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.SubjectVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public boolean saveSubject(SubjectDTO subjectDTO) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();

        Subject subject = BeanUtil.copyProperties(subjectDTO, Subject.class);
        subject.setPass(Float.valueOf('1'));

        if ("简单题".equals(subject.getLevel())) {
            subject.setCoefficient(Float.valueOf("0.7"));
        } else if ("中等题".equals(subject.getLevel())) {
            subject.setCoefficient(Float.valueOf("0.5"));
        } else if ("重难题".equals(subject.getLevel())) {
            subject.setCoefficient(Float.valueOf("0.3"));
        }

        return save(subject);
    }

    @Override
    public boolean deleteSubject(Long id) {
        CheckEmptyOrRuleUtil.isTeacherOrManager();
        try {
            return removeById(id);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "该题目正在被使用，无法删除");
        }
    }

    @Override
    public boolean updateSubject(SubjectDTO subjectDTO) {
        CheckEmptyOrRuleUtil.isTeacherOrManager();
        Subject subject = BeanUtil.copyProperties(subjectDTO, Subject.class);
        return updateById(subject);
    }

    @Override
    public Map<String, List<SubjectVO>> findByWarehouseId(Long warehouseId) {
        CheckEmptyOrRuleUtil.isTeacherOrManager();
        List<Subject> subjects = lambdaQuery().eq(Subject::getWarehouseId, warehouseId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(subjects)) return Collections.emptyMap();
        List<SubjectVO> subjectList = subjects.stream().map(this::convertor).toList();
        return subjectList.stream().collect(Collectors.groupingBy(SubjectVO::getType));
    }

    private SubjectVO convertor(Subject subject) {
        SubjectVO subjectVO = BeanUtil.copyProperties(subject, SubjectVO.class);
        Warehouse warehouse = Db.lambdaQuery(Warehouse.class).eq(Warehouse::getId, subject.getWarehouseId()).select(Warehouse::getName).one();
        subjectVO.setWarehouse(warehouse.getName());
        return subjectVO;
    }
}
