package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.entity.*;
import net.zjitc.service.DataService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    @Override
    public Object getCountData() {

        long userCount = 0L, courseCount = 0L, subjectCount = 0L;
        Long studentCount = Db.lambdaQuery(Student.class).count();
        Long teacherCount = Db.lambdaQuery(Teacher.class).count();
        userCount = studentCount + teacherCount;
        courseCount = Db.lambdaQuery(Course.class).count();
        subjectCount = Db.lambdaQuery(Subject.class).count();
        return new Object[]{userCount, courseCount, subjectCount};
    }

    @Override
    public Object getSchoolData() {

        List<School> schools = Db.lambdaQuery(School.class).list();
        Map<String, List<Student>> studentMap = Db.lambdaQuery(Student.class).list().stream().collect(Collectors.groupingBy(Student::getSchool));
        Map<String, List<Teacher>> teacherMap = Db.lambdaQuery(Teacher.class).list().stream().collect(Collectors.groupingBy(Teacher::getSchool));
        HashMap<String, Integer[]> stringIntegerHashMap = new HashMap<>();

        for (School school : schools) {
            Integer[] ints = new Integer[2];
            if (studentMap.containsKey(school.getName())) {
                List<Student> students = studentMap.get(school.getName());
                int size = students.size();
                ints[0] = size;
            } else {
                ints[0] = 0;
            }
            if (teacherMap.containsKey(school.getName())) {
                List<Teacher> teachers = teacherMap.get(school.getName());
                int size = teachers.size();
                ints[1] = size;
            } else {
                ints[1] = 0;
            }
            stringIntegerHashMap.put(school.getName(), ints);
        }

        // 排序返回
        return stringIntegerHashMap;
    }

    @Override
    public Object getScoreData(Long studentId, Long courseId) {

        if (studentId == null) studentId = ThreadLocalUtil.getId();
        List<Long> paperIds = Db.lambdaQuery(Paper.class).eq(Paper::getCourseId, courseId).list().stream().map(Paper::getId).toList();
        if (paperIds.isEmpty()) return Collections.emptyList();
        List<PaperApproval> paperApprovalList = Db.lambdaQuery(PaperApproval.class).in(PaperApproval::getPaperId, paperIds).eq(PaperApproval::getStudentId, studentId).orderByAsc(PaperApproval::getPaperId).eq(PaperApproval::getStatus, true).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperApprovalList)) return Collections.emptyList();
        List<Long> paperApprovalIds = paperApprovalList.stream().map(PaperApproval::getId).toList();
        List<PaperStudent> paperStudentList = Db.lambdaQuery(PaperStudent.class).in(PaperStudent::getPaperApprovalId, paperApprovalIds).list();

        HashMap<String, List<Float>> scoreMap = new HashMap<>();
        Map<Long, List<PaperStudent>> collect = paperStudentList.stream().collect(Collectors.groupingBy(PaperStudent::getPaperApprovalId));
        collect.forEach((id, paperStudents) -> {

            List<Float> scoreList = new ArrayList<>();
            Float sum = paperStudents.stream().map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float singleScore = paperStudents.stream().filter(paperStudent -> "单选" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float manyScore = paperStudents.stream().filter(paperStudent -> "多选" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float fillScore = paperStudents.stream().filter(paperStudent -> "填空" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float answerScore = paperStudents.stream().filter(paperStudent -> "简答" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            scoreList.add(sum);
            scoreList.add(singleScore);
            scoreList.add(manyScore);
            scoreList.add(fillScore);
            scoreList.add(answerScore);
            scoreMap.put(String.valueOf(id), scoreList);
        });

        return scoreMap;
    }

    @Override
    public Object getPaperData(Long studentId, Long courseId) {

        if (studentId == null) studentId = ThreadLocalUtil.getId();
        List<Long> paperIds = Db.lambdaQuery(Paper.class).eq(Paper::getCourseId, courseId).list().stream().map(Paper::getId).toList();
        if (paperIds.isEmpty()) return Collections.emptyList();
        List<PaperApproval> paperApprovalList = Db.lambdaQuery(PaperApproval.class).in(PaperApproval::getPaperId, paperIds).eq(PaperApproval::getStudentId, studentId).eq(PaperApproval::getStatus, true).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperApprovalList)) return Collections.emptyList();
        return paperApprovalList.stream().collect(Collectors.groupingBy(PaperApproval::getId));
    }

    @Override
    public Object getAIData(Long studentId, Long courseId) {

        if (studentId == null) studentId = ThreadLocalUtil.getId();
        List<Paper> paperList = Db.lambdaQuery(Paper.class).eq(Paper::getCourseId, courseId).list();
        List<Long> paperIds = paperList.stream().map(Paper::getId).toList();

        List<PaperApproval> paperApprovalList = Db.lambdaQuery(PaperApproval.class).in(PaperApproval::getPaperId, paperIds).eq(PaperApproval::getStudentId, studentId).eq(PaperApproval::getStatus, true).orderByAsc(PaperApproval::getPaperId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(paperApprovalList)) return Collections.emptyList();
        List<Long> paperApprovalIds = paperApprovalList.stream().map(PaperApproval::getId).toList();
        List<PaperStudent> paperStudentList = Db.lambdaQuery(PaperStudent.class).in(PaperStudent::getPaperApprovalId, paperApprovalIds).list();

        List<String> scoreList = new ArrayList<>();
        Map<Long, List<PaperStudent>> collect = paperStudentList.stream().collect(Collectors.groupingBy(PaperStudent::getPaperApprovalId));
        Map<Long, List<PaperApproval>> PaperApprovalMap = paperApprovalList.stream().collect(Collectors.groupingBy(PaperApproval::getId));
        Map<Long, List<Paper>> paperMap = paperList.stream().collect(Collectors.groupingBy(Paper::getId));

        collect.forEach((id, paperStudents) -> {

            Float sum = paperStudents.stream().map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float singleScore = paperStudents.stream().filter(paperStudent -> "单选" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float manyScore = paperStudents.stream().filter(paperStudent -> "多选" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float fillScore = paperStudents.stream().filter(paperStudent -> "填空" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);
            Float answerScore = paperStudents.stream().filter(paperStudent -> "简答" .equals(paperStudent.getType())).map(PaperStudent::getPoint).reduce(0f, Float::sum);

            PaperApproval paperApproval = PaperApprovalMap.get(id).get(0);
            Paper paper = paperMap.get(paperApproval.getPaperId()).get(0);
            String paperScore = String.format("试卷名: %s, 试卷总得分: %.2f, 单选题得分: %.2f, 多选题得分: %.2f, 填空题得分: %.2f, 简答题得分: %.2f", paper.getTitle(), sum, singleScore, manyScore, fillScore, answerScore);

            scoreList.add(paperScore);
        });

        return scoreList;
    }
}
