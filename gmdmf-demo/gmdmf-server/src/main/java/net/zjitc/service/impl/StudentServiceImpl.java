package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.StudentDTO;
import net.zjitc.entity.*;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.StudentMapper;
import net.zjitc.service.CourseTeamService;
import net.zjitc.service.StudentService;
import net.zjitc.utils.AliOssUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.Md5Util;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.StudentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final CourseTeamService courseTeamService;

    @Override
    public Student findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public boolean register(List<StudentDTO> studentDTOS) {

        CheckEmptyOrRuleUtil.isManager();

        List<Student> studentList = new ArrayList<>();
        List<Team> teamList = new ArrayList<>();

        studentDTOS.forEach(studentDTO -> {

            Student student = BeanUtil.copyProperties(studentDTO, Student.class);

            Student s = lambdaQuery().eq(Student::getIdCard, student.getIdCard()).one();
            if (s != null) throw new BusinessException(Code.BUSINESS_ERR, "身份证号重复");

            // 通过身份证填充出生日期
            String number = String.valueOf(studentDTO.getIdCard());
            Integer year = Convert.toInt(number.substring(6, 10));
            Integer month = Convert.toInt(number.substring(10, 12));
            Integer day = Convert.toInt(number.substring(12, 14));
            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0, 0);

            // 设置出生日期
            student.setBirth(localDateTime);

            // 学生学号 + "_" + 身份证后4位 = username
            student.setUsername(student.getNumber() + "_" + number.substring(14));

            // 身份证后6位 = password (初始密码)
            student.setPassword(Md5Util.getMD5String(number.substring(12)));

            School school = Db.lambdaQuery(School.class).eq(School::getName, student.getSchool()).one();
            if (school == null) throw new BusinessException(Code.BUSINESS_ERR, "学校不存在");

            Department department = Db.lambdaQuery(Department.class).eq(Department::getSchoolId, school.getId()).eq(Department::getName, student.getDepartment()).one();
            if (department == null) throw new BusinessException(Code.BUSINESS_ERR, "院系不存在");

            Major major = Db.lambdaQuery(Major.class).eq(Major::getName, student.getMajor()).one();
            if (major == null) throw new BusinessException(Code.BUSINESS_ERR, "专业不存在");

            Team team = Db.lambdaQuery(Team.class).eq(Team::getDepartmentId, department.getId()).eq(Team::getMajorId, major.getId()).eq(Team::getName, studentDTO.getTeamName()).one();
            if (team == null) throw new BusinessException(Code.BUSINESS_ERR, "班级不存在");

            // 根据 team id 获取班级名
            student.setTeamId(team.getId());
            student.setRule("学生");
            studentList.add(student);
            teamList.add(team);

        });

        saveBatch(studentList);

        // 修改人数
        studentList.forEach(student -> {
            List<Course> course = courseTeamService.getCourseByStuId(student.getId());
            if (CheckEmptyOrRuleUtil.isEmptyList(course)) return;
            course = course.stream().map(c -> c.setNumber(c.getNumber() + 1)).toList();
            Db.updateBatchById(course);
        });

        return true;
    }

    @Override
    public boolean deleteStudent(List<Long> ids) {
        CheckEmptyOrRuleUtil.isManager();
        if (CheckEmptyOrRuleUtil.isEmptyList(ids)) throw new BusinessException(Code.BUSINESS_ERR, "数据异常");
        try {
            return removeBatchByIds(ids);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "选中学生存在关联数据，无法删除");
        }
    }

    @Override
    public boolean updatePassword(String oldPassword, String newPassword) {
        CheckEmptyOrRuleUtil.isStudent();
        String username = ThreadLocalUtil.getUsername();
        Student stu = baseMapper.findByUsername(username);
        if (!stu.getPassword().equals(Md5Util.getMD5String(oldPassword)))
            throw new BusinessException(Code.BUSINESS_ERR, "原密码输入错误");
        Student student = lambdaQuery().eq(Student::getId, ThreadLocalUtil.getId()).one();
        if (student == null) throw new BusinessException(Code.BUSINESS_ERR, "用户不存在");
        student.setPassword(Md5Util.getMD5String(newPassword));
        return updateById(student);
    }

    @Override
    public boolean updatePicture(String pictureUrl) throws Exception {

        CheckEmptyOrRuleUtil.isStudent();
        Student student = getById(ThreadLocalUtil.getId());
        if (student != null && StrUtil.isNotBlank(student.getPicture())) {
            String replace = student.getPicture().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
            AliOssUtil.deleteFile(replace);
        }
        return lambdaUpdate().eq(Student::getId, ThreadLocalUtil.getId()).set(Student::getPicture, pictureUrl).update();
    }

    @Override
    public boolean updateStudent(StudentDTO studentDTO) {
        Student student = BeanUtil.copyProperties(studentDTO, Student.class);
        Team team = Db.lambdaQuery(Team.class).eq(Team::getName, studentDTO.getTeamName()).one();
        student.setTeamId(team.getId());
        return updateById(student);
    }

    @Override
    public boolean updateStudentList(List<StudentDTO> studentDTOS) {

        CheckEmptyOrRuleUtil.isManager();
        if (CheckEmptyOrRuleUtil.isEmptyList(studentDTOS))
            throw new BusinessException(Code.BUSINESS_ERR, "数据异常");
        Map<String, List<Team>> collect = Db.lambdaQuery(Team.class).list().stream().collect(Collectors.groupingBy(Team::getName));
        ArrayList<Student> students = new ArrayList<>();
        studentDTOS.forEach(studentDTO -> {
            Long teamId = collect.get(studentDTO.getTeamName()).get(0).getId();
            students.add(BeanUtil.copyProperties(studentDTO, Student.class).setTeamId(teamId));
        });

        return updateBatchById(students);
    }

    @Override
    public StudentVO getStudentInfo() {
        CheckEmptyOrRuleUtil.isStudent();
        Student student = lambdaQuery().eq(Student::getId, ThreadLocalUtil.getId()).one();
        if (student == null) throw new BusinessException(Code.BUSINESS_ERR, "用户不存在");
        Team team = Db.getById(student.getTeamId(), Team.class);
        StudentVO studentVO = BeanUtil.copyProperties(student, StudentVO.class);
        return studentVO.setTeamName(team.getName());
    }

    @Override
    public List<StudentVO> findByTeamId(Long id) {
        CheckEmptyOrRuleUtil.isTeacherOrManager();
        List<Student> students = lambdaQuery().eq(Student::getTeamId, id).list();
        return BeanUtil.copyToList(students, StudentVO.class);
    }

    @Override
    public List<StudentVO> findAll() {
        CheckEmptyOrRuleUtil.isManager();
        List<Student> students = lambdaQuery().list();
        if (CheckEmptyOrRuleUtil.isEmptyList(students)) return Collections.emptyList();
        List<Long> ids = students.stream().map(Student::getTeamId).toList();
        Map<Long, List<Team>> collect = Db.lambdaQuery(Team.class).in(Team::getId, ids).list().stream().collect(Collectors.groupingBy(Team::getId));
        return students.stream().map(student -> {
            Team team = collect.get(student.getTeamId()).get(0);
            return BeanUtil.copyProperties(student, StudentVO.class).setTeamName(team.getName());
        }).toList();
    }
}
