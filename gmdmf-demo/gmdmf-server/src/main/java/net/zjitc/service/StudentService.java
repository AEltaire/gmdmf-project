package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.StudentDTO;
import net.zjitc.entity.Student;
import net.zjitc.vo.StudentVO;

import java.util.List;

public interface StudentService extends IService<Student> {

    // 登录
    Student findByUsername(String username);

    // 注册
    boolean register(List<StudentDTO> studentDTO);

    // 管理端
    boolean deleteStudent(List<Long> ids);

    // 修改密码
    boolean updatePassword(String oldPassword, String newPassword);

    // 修改头像
    boolean updatePicture(String pictureUrl) throws Exception;

    boolean updateStudent(StudentDTO student);

    StudentVO getStudentInfo();

    List<StudentVO> findByTeamId(Long id);

    List<StudentVO> findAll();

    boolean updateStudentList(List<StudentDTO> studentDTOS);
}
