package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperDTO;
import net.zjitc.entity.Course;
import net.zjitc.entity.Paper;
import net.zjitc.entity.PaperApproval;
import net.zjitc.entity.PaperSubject;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.PaperMapper;
import net.zjitc.query.PaperQuery;
import net.zjitc.service.PaperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.PaperVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Override
    public boolean savePaper(PaperDTO paperDTO) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();
        Paper paper = BeanUtil.copyProperties(paperDTO, Paper.class);
        paper.setTeacher(ThreadLocalUtil.getName()).setCreateTime(LocalDateTime.now());
        return save(paper);
    }

    @Override
    public boolean deletePaper(Long id) {

        CheckEmptyOrRuleUtil.isTeacher();
        Paper paper = lambdaQuery().eq(Paper::getId, id).one();
        if (paper == null) throw new BusinessException(Code.BUSINESS_ERR, "该试卷不存在");
        if (!paper.getTeacher().equals(ThreadLocalUtil.getName()))
            throw new BusinessException(Code.BUSINESS_ERR, "非试卷创建教师");
        if (LocalDateTime.now().isAfter(paper.getStartTime()))
            throw new BusinessException(Code.BUSINESS_ERR, "该试题已开始作答");
        List<PaperSubject> paperSubjectList = Db.lambdaQuery(PaperSubject.class).eq(PaperSubject::getPaperId, id).list();
        if (!CheckEmptyOrRuleUtil.isEmptyList(paperSubjectList)) {
            List<Long> ids = paperSubjectList.stream().map(PaperSubject::getId).toList();
            Db.removeByIds(ids, PaperSubject.class);
        }
        List<PaperApproval> paperApprovalList = Db.lambdaQuery(PaperApproval.class).eq(PaperApproval::getPaperId, id).list();
        if (!CheckEmptyOrRuleUtil.isEmptyList(paperApprovalList)) {
            List<Long> ids = paperApprovalList.stream().map(PaperApproval::getId).toList();
            Db.removeByIds(ids, PaperApproval.class);
        }
        return removeById(id);
    }

    @Override
    public boolean updatePaper(PaperDTO paperDTO) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();
        Paper paper = getById(paperDTO.getId());
        if (paper == null) throw new BusinessException(Code.BUSINESS_ERR, "该试卷不存在");
        if (LocalDateTime.now().isAfter(paper.getStartTime()))
            throw new BusinessException(Code.BUSINESS_ERR, "该试题已开始作答，不能执行修改操作");
        Paper newPaper = BeanUtil.copyProperties(paperDTO, Paper.class);
        return updateById(newPaper);
    }

    @Override
    public PaperVO findById(Long id) {

        CheckEmptyOrRuleUtil.isEmptyId(id);
        Paper paper = lambdaQuery().eq(Paper::getId, id).one();
        return convertor(paper);
    }

    // 学生端
    @Override
    public List<PaperVO> findByQuery(PaperQuery paperQuery) {

        List<Paper> paperList = lambdaQuery()
                .eq(StrUtil.isNotBlank(paperQuery.getType()), Paper::getType, paperQuery.getType())
                .eq(paperQuery.getCourseId() != null, Paper::getCourseId, paperQuery.getCourseId())
                .like(StrUtil.isNotBlank(paperQuery.getTitle()), Paper::getTitle, paperQuery.getTitle())
                .orderByDesc(Paper::getCreateTime)
                .orderByDesc(!paperQuery.getNotEndTime(), Paper::getEndTime)
                .orderByAsc(paperQuery.getNotEndTime(), Paper::getEndTime)
                .orderByAsc(Paper::getStartTime)
                .orderByDesc(Paper::getTitle)
                .list();

        if (CheckEmptyOrRuleUtil.isEmptyList(paperList)) return Collections.emptyList();
        List<PaperVO> list = paperList.stream().map(this::convertor).toList();
        return list.stream().map(item -> {
            PaperApproval one = Db.lambdaQuery(PaperApproval.class).eq(PaperApproval::getPaperId, item.getId()).eq(PaperApproval::getStudentId, ThreadLocalUtil.getId()).one();
            if (one != null) item.setPoint(one.getPoint());
            else item.setPoint(0F);
            return item;
        }).toList();
    }

    private PaperVO convertor(Paper paper) {

        Course course = Db.getById(paper.getCourseId(), Course.class);
        PaperVO paperVO = BeanUtil.copyProperties(paper, PaperVO.class);
        return paperVO.setCourseName(course.getName());
    }
}
