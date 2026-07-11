package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.entity.Course;
import net.zjitc.entity.Loves;
import net.zjitc.entity.Major;
import net.zjitc.entity.Teacher;
import net.zjitc.mapper.LovesMapper;
import net.zjitc.service.LovesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.CourseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@Transactional
public class LovesServiceImpl extends ServiceImpl<LovesMapper, Loves> implements LovesService {

    @Override
    public boolean saveLoves(Loves loves) {

        CheckEmptyOrRuleUtil.isStudent();
        CheckEmptyOrRuleUtil.isEmptyId(loves.getCourseId());
        Long id = ThreadLocalUtil.getId();
        loves.setUserId(id);
        Loves one = lambdaQuery().eq(Loves::getUserId, id).eq(Loves::getCourseId, loves.getCourseId()).one();
        if (one != null) return true;
        save(loves);
        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, loves.getCourseId()).one();
        course.setLove(course.getLove() + 1);
        return Db.updateById(course);
    }

    @Override
    public boolean deleteLoves(Long id) {

        CheckEmptyOrRuleUtil.isStudent();
        CheckEmptyOrRuleUtil.isEmptyId(id);
        Loves one = lambdaQuery().eq(Loves::getUserId, ThreadLocalUtil.getId()).eq(Loves::getCourseId, id).one();
        if (one == null) return false;
        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, one.getCourseId()).one();
        if (course.getLove() > 0) course.setLove(course.getLove() - 1);
        Db.updateById(course);
        return removeById(one.getId());
    }

    @Override
    public List<CourseVO> findAll() {

        CheckEmptyOrRuleUtil.isStudent();
        Long id = ThreadLocalUtil.getId();
        List<Loves> loves = lambdaQuery().eq(Loves::getUserId, id).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(loves)) return Collections.emptyList();
        List<Long> ids = loves.stream().map(Loves::getCourseId).toList();
        List<Course> courses = Db.lambdaQuery(Course.class).in(Course::getId, ids).list();
        return courses.stream().map(this::convertor).toList();
    }

    private CourseVO convertor(Course course) {

        Teacher teacher = Db.getById(course.getTeacherId(), Teacher.class);
        Major major = Db.getById(course.getMajorId(), Major.class);
        CourseVO courseVO = BeanUtil.copyProperties(course, CourseVO.class);
        return courseVO.setTeacherName(teacher.getName()).setMajorName(major.getName());
    }
}
