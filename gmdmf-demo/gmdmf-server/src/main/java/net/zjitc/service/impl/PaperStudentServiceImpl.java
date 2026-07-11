package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.dto.PaperStudentDTO;
import net.zjitc.entity.*;
import net.zjitc.mapper.PaperStudentMapper;
import net.zjitc.service.PaperStudentService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.PaperStudentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class PaperStudentServiceImpl extends ServiceImpl<PaperStudentMapper, PaperStudent> implements PaperStudentService {

    @Override
    public boolean saveOrUpdatePaperStudent(List<PaperStudentDTO> paperStudentDTOS) {

        if (CheckEmptyOrRuleUtil.isEmptyList(paperStudentDTOS)) return false;

        List<PaperSubject> paperSubjects = Db.lambdaQuery(PaperSubject.class).eq(PaperSubject::getPaperId, paperStudentDTOS.get(0).getPaperId()).list();

        Map<Long, List<PaperSubject>> workMap = paperSubjects.stream().collect(Collectors.groupingBy(PaperSubject::getSubjectId));

        List<Long> subIds = paperSubjects.stream().map(PaperSubject::getSubjectId).toList();
        List<Subject> subjects = Db.lambdaQuery(Subject.class).in(Subject::getId, subIds).list();

        Map<Long, List<Subject>> collect = subjects.stream().collect(Collectors.groupingBy(Subject::getId));
        paperStudentDTOS.forEach(paperStudentDTO -> {

            PaperStudent paperStudent = BeanUtil.copyProperties(paperStudentDTO, PaperStudent.class);
            Subject subject = collect.get(paperStudent.getSubjectId()).get(0);
            PaperSubject paperSubject = workMap.get(paperStudent.getSubjectId()).get(0);
            Float score = paperSubject.getScore();
            paperStudent.setScore(score);

            if ("单选".equals(subject.getType()) || "多选".equals(subject.getType())) {
                paperStudent = checkSingleOrMany(subject, paperStudent, score);
            } else if ("填空".equals(subject.getType())) {
                paperStudent = checkFill(subject, paperStudent, score);
            } else {
                paperStudent = checkAnswer(subject, paperStudent, score);
            }
            saveOrUpdate(paperStudent);
        });
        return true;
    }

    @Override
    public boolean approvalPaperStudent(List<PaperStudentDTO> paperStudentDTOS) {

        List<PaperStudent> paperStudents = BeanUtil.copyToList(paperStudentDTOS, PaperStudent.class);
        paperStudents.forEach(paperStudent -> {
            if (ObjectUtil.equals(paperStudent.getScore(), paperStudent.getPoint()))
                paperStudent.setResult(true);
            else paperStudent.setResult(false);
        });
        return updateBatchById(paperStudents);
    }

    @Override
    public Map<String, List<PaperStudentVO>> findByPaperApprovalId(Long paperApprovalId) {

        if (paperApprovalId == null) return Collections.emptyMap();
        List<PaperStudent> paperStudentList = lambdaQuery().eq(PaperStudent::getPaperApprovalId, paperApprovalId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperStudentList)) return Collections.emptyMap();

        List<Long> subjectIds = paperStudentList.stream().map(PaperStudent::getSubjectId).toList();
        List<Subject> subjects = Db.lambdaQuery(Subject.class).in(Subject::getId, subjectIds).list();
        Map<Long, List<Subject>> collect = subjects.stream().collect(Collectors.groupingBy(Subject::getId));
        return BeanUtil.copyToList(paperStudentList, PaperStudentVO.class).stream().map(paperStudentVO -> {
            Subject subject = collect.get(paperStudentVO.getSubjectId()).get(0);
            return paperStudentVO.setLabel(subject.getLabel()).setQuestion(subject.getQuestion()).setOptions(subject.getOptions()).setAnalysis(subject.getAnalysis()).setPub(subject.getPub());
        }).collect(Collectors.groupingBy(PaperStudentVO::getType));
    }

    @Override
    public Map<String, List<PaperStudentVO>> findByPaperIdAndStudentId(Long paperId, Long studentId) {

        PaperApproval paperApproval = Db.lambdaQuery(PaperApproval.class).eq(PaperApproval::getPaperId, paperId).eq(PaperApproval::getStudentId, studentId).one();
        if (paperApproval == null) return Collections.emptyMap();
        List<PaperStudent> paperStudentList = lambdaQuery().eq(PaperStudent::getPaperApprovalId, paperApproval.getId()).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperStudentList)) return Collections.emptyMap();

        List<Long> subjectIds = paperStudentList.stream().map(PaperStudent::getSubjectId).toList();
        List<Subject> subjects = Db.lambdaQuery(Subject.class).in(Subject::getId, subjectIds).list();
        Map<Long, List<Subject>> collect = subjects.stream().collect(Collectors.groupingBy(Subject::getId));
        return BeanUtil.copyToList(paperStudentList, PaperStudentVO.class).stream().map(paperStudentVO -> {
            Subject subject = collect.get(paperStudentVO.getSubjectId()).get(0);
            return paperStudentVO.setLabel(subject.getLabel()).setQuestion(subject.getQuestion()).setOptions(subject.getOptions()).setAnalysis(subject.getAnalysis()).setPub(subject.getPub());
        }).collect(Collectors.groupingBy(PaperStudentVO::getType));
    }

    @Override
    public List<Object> findByStudentId(Long studentId, Long courseId) {

        if (studentId == null) studentId = ThreadLocalUtil.getId();
        List<Long> paperIds = Db.lambdaQuery(Paper.class).eq(Paper::getCourseId, courseId).list().stream().map(Paper::getId).toList();
        if (paperIds.isEmpty()) return Collections.emptyList();
        List<PaperApproval> paperApprovalList = Db.lambdaQuery(PaperApproval.class).in(PaperApproval::getPaperId, paperIds).eq(PaperApproval::getStudentId, studentId).orderByAsc(PaperApproval::getPaperId).eq(PaperApproval::getStatus, true).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperApprovalList)) return Collections.emptyList();
        List<Object> objectList = new ArrayList<>();
        paperIds = paperApprovalList.stream().map(PaperApproval::getPaperId).toList();
        Map<Long, List<PaperApproval>> collect = paperApprovalList.stream().collect(Collectors.groupingBy(PaperApproval::getPaperId));
        Map<Long, List<Paper>> paperMap = Db.lambdaQuery(Paper.class).in(Paper::getId, paperIds).orderByAsc(Paper::getId).list().stream().collect(Collectors.groupingBy(Paper::getId));
        paperIds.forEach(paperId -> {
            PaperApproval paperApproval = collect.get(paperId).get(0);
            Paper paper = paperMap.get(paperId).get(0);
            objectList.add(Map.of("paperApprovalId", String.valueOf(paperApproval.getId()), "paperName", paper.getTitle()));
        });

        return objectList;
    }

    private PaperStudent checkSingleOrMany(Subject subject, PaperStudent paperStudent, Float score) {

        String input = paperStudent.getInput();
        String answer = subject.getAnswer();

        if (StrUtil.isNotBlank(input)) {
            List<String> inputList = Arrays.asList(input.split(""));
            List<String> answerList = Arrays.asList(answer.split(""));

            if (answerList.stream().sorted().collect(Collectors.joining()).equals(inputList.stream().sorted().collect(Collectors.joining()))) {
                paperStudent.setResult(true).setPoint(score).setAnswer(subject.getAnswer());
            } else {
                paperStudent.setResult(false).setPoint(Float.valueOf("0.0")).setAnswer(subject.getAnswer());
            }
            return paperStudent;
        } else {
            paperStudent.setResult(false).setPoint(Float.valueOf("0.0")).setAnswer(subject.getAnswer());
            return paperStudent;
        }
    }

    private PaperStudent checkFill(Subject subject, PaperStudent paperStudent, Float score) {

        int flag = 0;
        int num = subject.getAnswer().split("&").length;
        int length = 0;
        boolean contains = paperStudent.getInput().contains("&");
        if (contains) length = paperStudent.getInput().split("&")[0].length();

        for (int i = 0; i < num || i < length; i++) {
            if (i <= length) {
                if (subject.getAnswer().split("&")[i].equals(paperStudent.getInput().split("&")[i])) {
                    flag += 1;
                }
            }
        }

        Float point = score / num * flag;
        paperStudent.setPoint(point).setAnswer(subject.getAnswer());

        if (flag == num) {
            paperStudent.setResult(true);
        } else {
            paperStudent.setResult(false);
        }

        return paperStudent;
    }

    private PaperStudent checkAnswer(Subject subject, PaperStudent paperStudent, Float score) {

        paperStudent.setResult(false).setPoint(Float.valueOf("0.0")).setAnswer(subject.getAnswer());
        return paperStudent;
    }
}
