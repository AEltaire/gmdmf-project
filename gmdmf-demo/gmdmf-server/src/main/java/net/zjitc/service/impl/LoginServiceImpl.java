package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.constant.Code;
import net.zjitc.dto.LoginDTO;
import net.zjitc.entity.Student;
import net.zjitc.entity.Teacher;
import net.zjitc.exception.BusinessException;
import net.zjitc.service.LoginService;
import net.zjitc.utils.Md5Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Override
    public void checkUser(LoginDTO loginDTO) {

        if ("学生".equals(loginDTO.getRule())) getStudent(loginDTO.getUsername(), loginDTO.getEmail());
        else if ("教师".equals(loginDTO.getRule())) getTeacher(loginDTO.getUsername(), loginDTO.getEmail());
        else throw new BusinessException(Code.BUSINESS_ERR, "用户身份错误");
    }

    @Override
    public void updateLoginPassword(LoginDTO loginDTO) {

        if ("学生".equals(loginDTO.getRule())) {
            Student student = getStudent(loginDTO.getUsername(), loginDTO.getEmail());
            student.setPassword(Md5Util.getMD5String(loginDTO.getPassword()));
            Db.updateById(student);
        } else if ("教师".equals(loginDTO.getRule())) {
            Teacher teacher = getTeacher(loginDTO.getUsername(), loginDTO.getEmail());
            teacher.setPassword(Md5Util.getMD5String(loginDTO.getPassword()));
            Db.updateById(teacher);
        } else {
            throw new BusinessException(Code.BUSINESS_ERR, "用户身份错误");
        }
    }

    private Student getStudent(String username, String email) {
        Student student = null;
        try {
            student = Db.lambdaQuery(Student.class).eq(Student::getUsername, username).eq(Student::getEmail, email).one();
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "用户数据不唯一，请联系管理");
        }
        if (student == null) throw new BusinessException(Code.BUSINESS_ERR, "用户不存在");
        return student;
    }

    private Teacher getTeacher(String username, String email) {
        Teacher teacher = null;
        try {
            teacher = Db.lambdaQuery(Teacher.class).eq(Teacher::getUsername, username).eq(Teacher::getEmail, email).one();
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "用户数据不唯一，请联系管理");
        }
        if (teacher == null) throw new BusinessException(Code.BUSINESS_ERR, "用户不存在");
        return teacher;
    }
}
