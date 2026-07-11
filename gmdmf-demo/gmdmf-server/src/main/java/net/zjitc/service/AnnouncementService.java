package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.AnnouncementDTO;
import net.zjitc.entity.Announcement;
import net.zjitc.vo.AnnouncementVO;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {

    boolean saveAnnouncement(AnnouncementDTO announcementDTO);

    boolean updateAnnouncement(AnnouncementDTO announcementDTO);

    AnnouncementVO findById(Long id);

    List<AnnouncementVO> findByCourseId(Long id);

    List<AnnouncementVO> findByStudentId();
}
