package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.CourseDTO;
import net.zjitc.entity.*;
import net.zjitc.enums.Pub;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.CourseMapper;
import net.zjitc.query.CourseQuery;
import net.zjitc.page.PageBean;
import net.zjitc.service.CourseService;
import net.zjitc.service.CourseTeamService;
import net.zjitc.utils.AliOssUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.CourseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseTeamService courseTeamService;

    @Override
    public Long saveCourse(CourseDTO courseDTO) {

        // 新增课程
        CheckEmptyOrRuleUtil.isTeacherOrManager();
        Course course = BeanUtil.copyProperties(courseDTO, Course.class);
        course.setLove(0).setAssess(Float.valueOf("5")).setNumber(0).setTeacherId(ThreadLocalUtil.getId());
        save(course);
        // 保存班级
        Long courseId = course.getId();
        List<Long> teamIds = courseDTO.getTeamIds();
        if (CheckEmptyOrRuleUtil.isEmptyList(teamIds)) return null;
        List<CourseTeam> courseTeams = teamIds.stream().map(teamId -> new CourseTeam(null, courseId, teamId)).toList();
        Db.saveBatch(courseTeams);
        // 修改人数
        Long count = Db.lambdaQuery(Student.class).in(Student::getTeamId, teamIds).count();
        if (count == null || count == 0) return null;
        course.setNumber(count.intValue());
        updateById(course);
        return courseId;
    }

    @Override
    public boolean approvalCourse(Long courseId) {

        Course course = getById(courseId);
        if (course != null && course.getPub() == Pub.APPROVAL)
            throw new BusinessException(Code.BUSINESS_ERR, "课程已提交审批，请勿重复提交");
        if (course != null && course.getPub() == Pub.PRIVATE) {
            course.setPub(Pub.valueOf("APPROVAL"));
            updateById(course);
            Approval approval = new Approval().setCourseId(course.getId()).setCreateTime(LocalDateTime.now());
            return Db.save(approval);
        } else {
            return true;
        }
    }

    @Override
    public boolean deleteCourse(Long id) {

        try {
            return removeById(id);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "选中课程存在关联信息，无法删除");
        }
    }

    @Override
    public boolean deleteCourseList(List<Long> ids) {

        try {
            return removeBatchByIds(ids);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "选中课程存在关联信息，无法删除");
        }
    }

    @Override
    public boolean updateCourse(CourseDTO courseDTO) throws Exception {

        // 修改课程
        CheckEmptyOrRuleUtil.isTeacherOrManager();
        Course oldCourse = getById(courseDTO.getId());
        if (oldCourse.getPub() == Pub.APPROVAL) {
            throw new BusinessException(Code.BUSINESS_ERR, "课程审批中，请勿修改");
        }
        Course course = BeanUtil.copyProperties(courseDTO, Course.class);
        // 修改 OSS
        if (!CheckEmptyOrRuleUtil.isEmptyString(oldCourse.getPicture()) && course.getPicture() != null) {
            String replace = oldCourse.getPicture().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
            AliOssUtil.deleteFile(replace);
        }

        updateById(course);
        // 修改班级
        Long courseId = course.getId();
        List<Long> teamIds = courseDTO.getTeamIds();
        if (CheckEmptyOrRuleUtil.isEmptyList(teamIds)) return false;
        List<Long> longList = Db.lambdaQuery(CourseTeam.class).eq(CourseTeam::getCourseId, courseId).list().stream().map(CourseTeam::getId).toList();
        courseTeamService.removeByIds(longList);
        List<CourseTeam> courseTeams = teamIds.stream().map(teamId -> new CourseTeam(null, courseId, teamId)).toList();
        Db.saveBatch(courseTeams);
        // 修改人数
        Long count = Db.lambdaQuery(Student.class).in(Student::getTeamId, teamIds).count();
        if (count == null || count == 0) return false;
        course.setNumber(count.intValue());
        return updateById(course);
    }

    @Override
    public boolean updateCourseList(List<CourseDTO> courseDTOS) {

        CheckEmptyOrRuleUtil.isManager();
        if (CheckEmptyOrRuleUtil.isEmptyList(courseDTOS))
            throw new BusinessException(Code.BUSINESS_ERR, "课程列表为空");

        List<Course> list = courseDTOS.stream().map(courseDTO -> {
            Major major = Db.lambdaQuery(Major.class).eq(Major::getName, courseDTO.getMajorName()).one();
            courseDTO.setMajorId(major.getId());
            return BeanUtil.copyProperties(courseDTO, Course.class);
        }).toList();

        List<Course> courses = BeanUtil.copyToList(list, Course.class);
        return updateBatchById(courses);
    }

    @Override
    public List<CourseVO> findByStudentId() {

        // 通过 courseTeamService 获得所有课程集合
        System.out.println(ThreadLocalUtil.getId());
        List<Course> courses = courseTeamService.getCourseByStuId(ThreadLocalUtil.getId());
        // 代码健壮性判断
        if (CheckEmptyOrRuleUtil.isEmptyList(courses)) return Collections.emptyList();
        // 获得课程并封装结果
        return courses.stream().map(this::convertor).collect(Collectors.toList());
    }

    @Override
    public PageBean<CourseVO> findByTeacherId(CourseQuery query) {

        if (query.getPageSize() == null) query.setPageSize(5);
        if (query.getCurrentPage() == null) query.setCurrentPage(1);

        // 分页查询
        Page<Course> coursePage = lambdaQuery().eq(Course::getExhibition, true).eq(Course::getTeacherId, ThreadLocalUtil.getId()).page(new Page<>(query.getCurrentPage(), query.getPageSize()));

        // 代码健壮性
        if (CheckEmptyOrRuleUtil.isEmptyPage(coursePage)) {
            return new PageBean<CourseVO>().setTotal(0L).setPageNum(0L).setList(Collections.emptyList());
        }

        // 自定义方法处理 Entity -> VO
        return PageBean.of(coursePage, this::convertor);
    }

    @Override
    public List<CourseVO> findAll() {

        CheckEmptyOrRuleUtil.isManager();
        List<Course> courses = lambdaQuery().list();
        if (CheckEmptyOrRuleUtil.isEmptyList(courses)) return Collections.emptyList();
        return courses.stream().map(this::convertor).collect(Collectors.toList());
    }

    @Override
    public List<CourseVO> findBy(CourseQuery courseQuery) {

        Long majorId = null;
        if (StrUtil.isNotBlank(courseQuery.getMajorName())) {
            Major major = Db.lambdaQuery(Major.class).eq(Major::getName, courseQuery.getMajorName()).one();
            majorId = major.getId();
        }

        List<Course> courses = lambdaQuery()
                .like(StrUtil.isNotBlank(courseQuery.getCourseName()), Course::getName, courseQuery.getCourseName())
                .eq(StrUtil.isNotBlank(courseQuery.getMajorName()), Course::getMajorId, majorId)
                .eq(Course::getExhibition, true)
                .eq(Course::getPub, Pub.PUBLIC)
                .list();

        return courses.stream().map(this::convertor).toList();
    }

    @Override
    public CourseVO findById(Long id) {
        Course course = getById(id);
        return convertor(course);
    }

    @Override
    public List<CourseVO> findAllApproval() {

        CheckEmptyOrRuleUtil.isManager();
        List<Course> courses = lambdaQuery().eq(Course::getPub, Pub.APPROVAL).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(courses)) return Collections.emptyList();
        return courses.stream().map(this::convertor).collect(Collectors.toList());
    }

    private CourseVO convertor(Course course) {

        Teacher teacher = Db.getById(course.getTeacherId(), Teacher.class);
        Major major = Db.getById(course.getMajorId(), Major.class);
        CourseVO courseVO = BeanUtil.copyProperties(course, CourseVO.class);
        return courseVO.setTeacherName(teacher.getName()).setMajorName(major.getName());
    }
}
