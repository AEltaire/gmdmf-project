package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.CourseDTO;
import net.zjitc.entity.Course;
import net.zjitc.query.CourseQuery;
import net.zjitc.page.PageBean;
import net.zjitc.vo.CourseVO;


import java.util.List;

public interface CourseService extends IService<Course> {

    Long saveCourse(CourseDTO courseDTO);

    boolean approvalCourse(Long courseId);

    boolean deleteCourse(Long id);

    boolean deleteCourseList(List<Long> ids);

    boolean updateCourse(CourseDTO courseDTO) throws Exception;

    boolean updateCourseList(List<CourseDTO> courseDTO);

    List<CourseVO> findByStudentId();

    PageBean<CourseVO> findByTeacherId(CourseQuery query);

    List<CourseVO> findAll();

    List<CourseVO> findBy(CourseQuery courseQuery);

    CourseVO findById(Long id);

    List<CourseVO> findAllApproval();
}
