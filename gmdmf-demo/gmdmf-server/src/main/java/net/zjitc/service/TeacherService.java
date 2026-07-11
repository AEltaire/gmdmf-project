package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.TeacherDTO;
import net.zjitc.entity.Teacher;
import net.zjitc.vo.TeacherVO;

import java.util.List;

public interface TeacherService extends IService<Teacher> {

    boolean saveTeacher(TeacherDTO teacherDTO);

    boolean updateByPassword(String oldPassword, String newPassword);

    boolean updateByPicture(String pictureUrl) throws Exception;

    boolean updateTeacher(TeacherDTO teacherDTO);

    boolean deleteTeacher(Long id);

    TeacherVO getTeacherInfo();

    List<TeacherVO> findAll();

    // 登录注册
    TeacherVO findById(Long id);

    Teacher findByUsername(String username);

    boolean deleteTeacherList(List<Long> ids);

    boolean updateTeacherList(List<TeacherDTO> teacherDTOS);
}
