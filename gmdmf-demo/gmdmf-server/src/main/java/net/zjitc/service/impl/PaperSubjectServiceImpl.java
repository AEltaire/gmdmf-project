package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperSubjectDTO;
import net.zjitc.entity.Paper;
import net.zjitc.entity.Subject;
import net.zjitc.entity.Warehouse;
import net.zjitc.entity.PaperSubject;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.WorkTestMapper;
import net.zjitc.service.PaperSubjectService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.vo.SubjectVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaperSubjectServiceImpl extends ServiceImpl<WorkTestMapper, PaperSubject> implements PaperSubjectService {

    @Override
    public boolean savePaperSubject(List<PaperSubjectDTO> paperSubjectDTOList) {

        if (CheckEmptyOrRuleUtil.isEmptyList(paperSubjectDTOList)) throw new BusinessException(Code.BUSINESS_ERR, "传输数据为空");

        ArrayList<PaperSubject> paperSubjects = new ArrayList<>(0);
        Long paperId = paperSubjectDTOList.get(0).getPaperId();
        List<PaperSubject> paperSubjectList = BeanUtil.copyToList(paperSubjectDTOList, PaperSubject.class);

        paperSubjectList.forEach(paperSubject -> {
            Long subjectId = paperSubject.getSubjectId();
            List<Long> ids = lambdaQuery().eq(PaperSubject::getPaperId, paperId).list().stream().map(PaperSubject::getSubjectId).toList();
            boolean contains = ids.contains(subjectId);
            if (!contains) paperSubjects.add(paperSubject);
        });

        if (CheckEmptyOrRuleUtil.isEmptyList(paperSubjects)) return true;

        saveBatch(paperSubjects);
        Float reduce = lambdaQuery().eq(PaperSubject::getPaperId, paperId).select(PaperSubject::getScore).list().stream().map(PaperSubject::getScore).reduce(Float.valueOf("0.0"), Float::sum);

        return  Db.lambdaUpdate(Paper.class).eq(Paper::getId, paperId).set(Paper::getSum, reduce).update();
    }

    @Override
    public boolean deleteByPaperId(Long paperId) {

        List<PaperSubject> paperSubjects = lambdaQuery().eq(PaperSubject::getPaperId, paperId).select(PaperSubject::getPaperId).list();
        List<Long> ids = paperSubjects.stream().map(PaperSubject::getId).toList();
        return removeBatchByIds(ids);
    }

    @Override
    public boolean updatePaperSubject(Long paperId, List<PaperSubjectDTO> paperSubjectDTOList) {

        if (CheckEmptyOrRuleUtil.isEmptyList(paperSubjectDTOList)) {
            List<PaperSubject> paperSubjects = lambdaQuery().eq(PaperSubject::getPaperId, paperId).list();
            if (paperSubjects != null && !paperSubjects.isEmpty()) {
                List<Long> ids = paperSubjects.stream().map(PaperSubject::getId).toList();
                return removeBatchByIds(ids);
            }
            return false;
        }

        // ids 是修改之后的题目
        List<Long> newIds = paperSubjectDTOList.stream().map(PaperSubjectDTO::getSubjectId).toList();

        // 原来的集合
        List<PaperSubject> oldList = lambdaQuery().eq(PaperSubject::getPaperId, paperId).list();

        if (!CheckEmptyOrRuleUtil.isEmptyList(oldList)) {

            List<Long> oldIds = oldList.stream().map(PaperSubject::getSubjectId).toList();
            List<Long> deleteIds = CollectionUtil.subtractToList(oldIds, newIds);
            List<Long> saveIds = CollectionUtil.subtractToList(newIds, oldIds);

            if (!deleteIds.isEmpty()) {
                List<Long> ids = lambdaQuery().eq(PaperSubject::getPaperId, paperId).in(PaperSubject::getSubjectId, deleteIds).list().stream().map(PaperSubject::getId).toList();
                removeBatchByIds(ids);
            }

            if (!saveIds.isEmpty()) {
                List<PaperSubject> paperSubjectList = BeanUtil.copyToList(paperSubjectDTOList, PaperSubject.class).stream().filter(item -> saveIds.contains(item.getSubjectId())).toList();
                saveBatch(paperSubjectList);
            }

            List<PaperSubject> tests = BeanUtil.copyToList(paperSubjectDTOList, PaperSubject.class);
            tests.forEach(item -> lambdaUpdate().eq(PaperSubject::getSubjectId, item.getSubjectId()).set(PaperSubject::getScore, item.getScore()).update());

            Float reduce = lambdaQuery().eq(PaperSubject::getPaperId, paperId).select(PaperSubject::getScore).list().stream().map(PaperSubject::getScore).reduce(Float.valueOf("0.0"), Float::sum);
            Db.lambdaUpdate(Paper.class).eq(Paper::getId, paperId).set(Paper::getSum, reduce).update();

            return true;
        }

        return false;
    }

    @Override
    public Map<String, List<SubjectVO>> findAll(Long paperId) {

        CheckEmptyOrRuleUtil.isEmptyId(paperId);

        List<PaperSubject> paperSubjects = lambdaQuery().eq(PaperSubject::getPaperId, paperId).orderByAsc(PaperSubject::getId).list();

        if(CheckEmptyOrRuleUtil.isEmptyList(paperSubjects)) return Collections.emptyMap();

        List<Long> subjectIds = paperSubjects.stream().map(PaperSubject::getSubjectId).toList();
        List<Subject> subjectList = Db.lambdaQuery(Subject.class).in(Subject::getId, subjectIds).list();

        if(CheckEmptyOrRuleUtil.isEmptyList(subjectList)) return Collections.emptyMap();

        List<SubjectVO> subjectVOS = subjectList.stream().map(this::convertorToSubject).toList();
        Map<Long, List<PaperSubject>> testMap = paperSubjects.stream().collect(Collectors.groupingBy(PaperSubject::getSubjectId));
        subjectVOS.forEach(subjectVO -> subjectVO.setScore(testMap.get(subjectVO.getId()).get(0).getScore()));

        return subjectVOS.stream().collect(Collectors.groupingBy(SubjectVO::getType));
    }

    private SubjectVO convertorToSubject(Subject subject) {

        SubjectVO subjectVO = BeanUtil.copyProperties(subject, SubjectVO.class);
        Warehouse warehouse = Db.getById(subject.getWarehouseId(), Warehouse.class);
        subjectVO.setWarehouse(warehouse.getName());
        return subjectVO;
    }
}
