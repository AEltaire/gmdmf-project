package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.AnnouncementDTO;
import net.zjitc.entity.Announcement;
import net.zjitc.entity.Course;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.AnnouncementMapper;
import net.zjitc.service.AnnouncementService;
import net.zjitc.service.CourseTeamService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.AnnouncementVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final CourseTeamService courseTeamService;

    @Override
    public boolean saveAnnouncement(AnnouncementDTO announcementDTO) {

        Announcement announcement = BeanUtil.copyProperties(announcementDTO, Announcement.class);
        announcement.setTeacherName(ThreadLocalUtil.getName()).setCreateTime(LocalDateTime.now());
        return save(announcement);
    }

    @Override
    public boolean updateAnnouncement(AnnouncementDTO announcementDTO) {

        Announcement announcement = BeanUtil.copyProperties(announcementDTO, Announcement.class);
        return updateById(announcement);
    }

    @Override
    public AnnouncementVO findById(Long id) {

        Announcement announcement = getById(id);
        if (announcement == null) throw new BusinessException(Code.BUSINESS_ERR, "公告数据异常");
        return convertor(announcement);
    }

    @Override
    public List<AnnouncementVO> findByCourseId(Long id) {

        List<Announcement> announcements = lambdaQuery().eq(Announcement::getCourseId, id).orderByDesc(Announcement::getCreateTime).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(announcements)) return Collections.emptyList();
        return announcements.stream().map(this::convertor).toList();
    }

    @Override
    public List<AnnouncementVO> findByStudentId() {

        List<Course> course = courseTeamService.getCourseByStuId(ThreadLocalUtil.getId());
        List<Long> ids = course.stream().map(Course::getId).toList();
        if (CheckEmptyOrRuleUtil.isEmptyList(ids)) return Collections.emptyList();
        List<Announcement> announcements = lambdaQuery().in(Announcement::getCourseId, ids).list();
        return announcements.stream().map(this::convertor).toList();
    }

    private AnnouncementVO convertor(Announcement announcement) {

        Course course = Db.getById(announcement.getCourseId(), Course.class);
        if (course == null) throw new BusinessException(Code.BUSINESS_ERR, "课程数据异常");
        AnnouncementVO announcementVO = BeanUtil.copyProperties(announcement, AnnouncementVO.class);
        announcementVO.setCourseName(course.getName());
        return announcementVO;
    }
}
