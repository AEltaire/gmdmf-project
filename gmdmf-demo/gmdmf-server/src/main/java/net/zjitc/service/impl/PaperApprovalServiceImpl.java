package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.PaperApprovalDTO;
import net.zjitc.entity.*;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.WorkStudentMapper;
import net.zjitc.service.CourseTeamService;
import net.zjitc.service.PaperApprovalService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.PaperApprovalVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PaperApprovalServiceImpl extends ServiceImpl<WorkStudentMapper, PaperApproval> implements PaperApprovalService {

    private final CourseTeamService courseTeamService;

    @Override
    public PaperApprovalVO findByPaperId(Long paperId) {

        CheckEmptyOrRuleUtil.isEmptyId(paperId);
        PaperApproval paperApproval = lambdaQuery().eq(PaperApproval::getPaperId, paperId).eq(PaperApproval::getStudentId, ThreadLocalUtil.getId()).one();
        return BeanUtil.copyProperties(paperApproval, PaperApprovalVO.class);
    }

    @Override
    public boolean saveOrUpdatePaperApproval(PaperApprovalDTO paperApprovalDTO) {

        if (paperApprovalDTO.getId() != null) {
            PaperApproval paperApproval = getById(paperApprovalDTO.getId());
            if (paperApproval.getStatus()) throw new RuntimeException("该试卷已批阅，无法修改");
        }

        // 直接通过试卷获得成绩
        CheckEmptyOrRuleUtil.isEmptyId(paperApprovalDTO.getPaperId());
        PaperApproval paperApproval = BeanUtil.copyProperties(paperApprovalDTO, PaperApproval.class);

        if (paperApproval.getId() != null) {
            Float sumPoint = this.getSumPoint(paperApprovalDTO.getId());
            paperApproval.setPoint(sumPoint);
        }

        // 可以在这个类中在添加一些统计字段，来用于学生的个性化数据分析... [待定]
        paperApproval.setStatus(false).setUpdateTime(LocalDateTime.now());

        return saveOrUpdate(paperApproval);
    }

    @Override
    public Map<Long, List<PaperApprovalVO>> findByCourseId(Long courseId, String type) {

        Course course = Db.getById(courseId, Course.class);
        List<Paper> paperList = Db.lambdaQuery(Paper.class).in(Paper::getCourseId, course.getId()).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperList)) return Collections.emptyMap();

        List<Long> ids = paperList.stream().map(Paper::getId).toList();
        List<PaperApproval> paperApprovals = lambdaQuery()
                .in(PaperApproval::getPaperId, ids)
                .eq(PaperApproval::getType, type)
                .orderByDesc(PaperApproval::getUpdateTime)
                .list();

        if (CheckEmptyOrRuleUtil.isEmptyList(paperApprovals)) return Collections.emptyMap();

        List<PaperApprovalVO> list = paperApprovals.stream().map(this::convertor).toList();
        return list.stream().collect(Collectors.groupingBy(PaperApprovalVO::getTeamId));
    }

    @Override
    public boolean approvalPaper(Long paperApprovalId) {

        PaperApproval paperApproval = getById(paperApprovalId);
        if (paperApproval == null) return false;
        Float sumPoint = getSumPoint(paperApprovalId);
        paperApproval.setPoint(sumPoint).setStatus(true);
        return updateById(paperApproval);
    }

    @Override
    public List<PaperApprovalVO> findByStudentId() {

        List<PaperApproval> list = lambdaQuery().eq(PaperApproval::getStudentId, ThreadLocalUtil.getId()).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(list)) return Collections.emptyList();
        return list.stream().map(this::convertor).toList();
    }

    @Override
    public List<PaperApprovalVO> findByTeacherId(String type) {

        List<Course> courses = Db.lambdaQuery(Course.class).eq(Course::getTeacherId, ThreadLocalUtil.getId()).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(courses)) return Collections.emptyList();

        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        if (CheckEmptyOrRuleUtil.isEmptyList(courseIds)) return Collections.emptyList();

        List<Paper> paperList = Db.lambdaQuery(Paper.class).in(Paper::getCourseId, courseIds).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperList)) return Collections.emptyList();

        List<Long> paperIds = paperList.stream().map(Paper::getId).toList();
        List<PaperApproval> paperApprovals = lambdaQuery().in(PaperApproval::getPaperId, paperIds)
                .eq(StrUtil.isNotBlank(type), PaperApproval::getType, type)
                .eq(PaperApproval::getStatus, false)
                .isNotNull(PaperApproval::getUpdateTime)
                .orderByDesc(PaperApproval::getPaperId)
                .orderByAsc(PaperApproval::getUpdateTime).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperApprovals)) return Collections.emptyList();

        return paperApprovals.stream().map(this::convertor).toList();
    }

    @Override
    public boolean saveStudentPaper(Long courseId, Long paperId) {

        Paper paper = Db.lambdaQuery(Paper.class).eq(Paper::getId, paperId).one();
        if (paper == null) throw new BusinessException(Code.BUSINESS_ERR, "试题卷数据异常");

        List<Team> team = courseTeamService.getTeamByCourseId(courseId);
        if (CheckEmptyOrRuleUtil.isEmptyList(team)) throw new BusinessException(Code.BUSINESS_ERR, "该课程学生数据为空");

        List<Long> teamIds = team.stream().map(Team::getId).toList();
        List<Student> studentList = Db.lambdaQuery(Student.class).in(Student::getTeamId, teamIds).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(studentList))  throw new BusinessException(Code.BUSINESS_ERR, "该课程学生数据异常");

        List<Long> studentIds = studentList.stream().map(Student::getId).toList();
        List<PaperApproval> paperApprovals = studentIds.stream().map(studentId -> {
            PaperApproval paperApproval = new PaperApproval();
            paperApproval.setPaperId(paperId).setStudentId(studentId).setType(paper.getType()).setScore(paper.getSum()).setPoint(0.0F).setCreateTime(LocalDateTime.now());
            return paperApproval;
        }).toList();

        return saveBatch(paperApprovals);
    }

    private Float getSumPoint(Long paperApprovalId) {

        List<PaperStudent> paperStudentList = Db.lambdaQuery(PaperStudent.class)
                .eq(PaperStudent::getPaperApprovalId, paperApprovalId)
                .list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperStudentList)) return Float.valueOf("0.0");
        return paperStudentList.stream().map(PaperStudent::getPoint).reduce(Float.valueOf("0.0"), Float::sum);
    }

    private PaperApprovalVO convertor(PaperApproval paperApproval) {

        PaperApprovalVO paperApprovalVO = BeanUtil.copyProperties(paperApproval, PaperApprovalVO.class);
        Student student = Db.lambdaQuery(Student.class).eq(Student::getId, paperApproval.getStudentId()).one();
        Paper paper = Db.lambdaQuery(Paper.class).eq(Paper::getId, paperApproval.getPaperId()).one();
        Team team = Db.getById(student.getTeamId(), Team.class);
        paperApprovalVO.setStudentName(student.getName()).setPaperName(paper.getTitle()).setTeamName(team.getName()).setTeamId(team.getId());
        return paperApprovalVO;
    }
}
