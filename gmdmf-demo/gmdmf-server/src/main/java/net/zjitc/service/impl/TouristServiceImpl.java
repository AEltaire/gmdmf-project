package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.entity.*;
import net.zjitc.mapper.TouristMapper;
import net.zjitc.service.TouristService;
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
public class TouristServiceImpl extends ServiceImpl<TouristMapper, Tourist> implements TouristService {


    @Override
    public boolean saveTourist(Tourist tourist) {

        CheckEmptyOrRuleUtil.isStudent();
        CheckEmptyOrRuleUtil.isEmptyId(tourist.getCourseId());
        Long id = ThreadLocalUtil.getId();
        tourist.setUserId(id);
        Tourist one = lambdaQuery().eq(Tourist::getUserId, id).eq(Tourist::getCourseId, tourist.getCourseId()).one();
        if (one != null) return true;
        save(tourist);
        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, tourist.getCourseId()).one();
        course.setNumber(course.getNumber() + 1);
        return Db.updateById(course);
    }

    @Override
    public boolean deleteTourist(Long id) {

        CheckEmptyOrRuleUtil.isStudent();
        CheckEmptyOrRuleUtil.isEmptyId(id);
        Tourist one = lambdaQuery().eq(Tourist::getUserId, ThreadLocalUtil.getId()).eq(Tourist::getCourseId, id).one();
        if (one == null) return false;
        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, one.getCourseId()).one();
        if (course.getNumber() > 0) course.setNumber(course.getNumber() - 1);
        Db.updateById(course);
        return removeById(one.getId());

    }

    @Override
    public List<CourseVO> findAll() {

        CheckEmptyOrRuleUtil.isStudent();
        Long id = ThreadLocalUtil.getId();
        List<Tourist> loves = lambdaQuery().eq(Tourist::getUserId, id).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(loves)) return Collections.emptyList();
        List<Long> ids = loves.stream().map(Tourist::getCourseId).toList();
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
