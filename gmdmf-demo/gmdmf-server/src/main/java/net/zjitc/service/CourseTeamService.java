package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.Course;
import net.zjitc.entity.CourseTeam;
import net.zjitc.entity.Team;
import net.zjitc.vo.TeamVO;

import java.util.List;

public interface CourseTeamService extends IService<CourseTeam> {

    // 通过课程 id 查询班级
    List<Team> getTeamByCourseId(Long courseId);

    // 通过学生 id 查找课程
    List<Course> getCourseByStuId(Long studentId);

    List<TeamVO> getTeamByTeacherId();
}
