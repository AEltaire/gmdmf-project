package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.entity.Course;
import net.zjitc.entity.CourseTeam;
import net.zjitc.entity.Student;
import net.zjitc.entity.Team;
import net.zjitc.enums.Pub;
import net.zjitc.mapper.CourseTeamMapper;
import net.zjitc.service.CourseTeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.TeamVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseTeamServiceImpl extends ServiceImpl<CourseTeamMapper, CourseTeam> implements CourseTeamService {

    @Override
    public List<Team> getTeamByCourseId(Long courseId) {

        CheckEmptyOrRuleUtil.isEmptyId(courseId);

        // 通过 courseId 查询所有班级 id
        List<CourseTeam> courseTeamList = lambdaQuery()
                .eq(CourseTeam::getCourseId, courseId)
                .select(CourseTeam::getTeamId)
                .orderByAsc(CourseTeam::getTeamId)
                .list();

        // 集合为空就直接返回
        if(CheckEmptyOrRuleUtil.isEmptyList(courseTeamList)) return Collections.emptyList();

        // 搜集查询到的班级 id
        List<Long> collect = courseTeamList.stream().map(CourseTeam::getTeamId).collect(Collectors.toList());

        // 通过 teamId 获得 list
        return Db.lambdaQuery(Team.class).in(Team::getId, collect).list();
    }

    @Override
    public List<Course> getCourseByStuId(Long studentId) {

        CheckEmptyOrRuleUtil.isEmptyId(studentId);

        // 通过 stuId 查询班级 id
        Student student = Db.lambdaQuery(Student.class).eq(Student::getId, studentId).select(Student::getTeamId).one();

        // 通过班级 id 获得课程 id 集合
        List<CourseTeam> courseTeamList = lambdaQuery()
                .eq(CourseTeam::getTeamId, student.getTeamId())
                .select(CourseTeam::getCourseId)
                .orderByAsc(CourseTeam::getCourseId)
                .list();

        // 集合为空就直接返回
        if(CheckEmptyOrRuleUtil.isEmptyList(courseTeamList)) return Collections.emptyList();

        // 搜集查询到的课程 id
        List<Long> collect = courseTeamList.stream().map(CourseTeam::getCourseId).collect(Collectors.toList());

        // 通过 courseId 获得 list
        return Db.lambdaQuery(Course.class).in(Course::getId, collect).eq(Course::getExhibition, true).eq(Course::getPub, Pub.PUBLIC).list();
    }

    @Override
    public List<TeamVO> getTeamByTeacherId() {

        List<Course> list = Db.lambdaQuery(Course.class).eq(Course::getExhibition, true).eq(Course::getTeacherId, ThreadLocalUtil.getId()).list();

        if(CheckEmptyOrRuleUtil.isEmptyList(list)) return Collections.emptyList();

        List<Long> courseIds = list.stream().map(Course::getId).toList();

        List<CourseTeam> courseTeamList = lambdaQuery().in(CourseTeam::getCourseId, courseIds).list();

        Set<Long> collect = courseTeamList.stream().map(CourseTeam::getTeamId).collect(Collectors.toSet());

        List<Team> teamList = Db.lambdaQuery(Team.class).in(Team::getId, collect).ge(Team::getEndTime, LocalDateTime.now()).list();

        return BeanUtil.copyToList(teamList, TeamVO.class);
    }
}
