package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.AnnouncementDTO;
import net.zjitc.entity.Announcement;
import net.zjitc.result.Result;
import net.zjitc.service.AnnouncementService;
import net.zjitc.vo.AnnouncementVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "公告相关接口")
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController extends BaseController{

    private final AnnouncementService announcementService;

    @Operation(summary = "发布公告")
    @PostMapping
    private Result<Announcement> save(@Parameter(description = "公告数据") @Validated @RequestBody AnnouncementDTO announcementDTO) {

        boolean flag = announcementService.saveAnnouncement(announcementDTO);
        return toPostAjax(flag, "发布公告成功");
    }

    @Operation(summary = "修改公告")
    @PutMapping
    private Result<Announcement> update(@Parameter(description = "公告数据") @Validated @RequestBody AnnouncementDTO announcementDTO) {

        System.out.println(announcementDTO);
        boolean flag = announcementService.updateAnnouncement(announcementDTO);
        return toPutAjax(flag, "修改公告成功");
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("{id}")
    private Result<Announcement> delete(@Parameter(description = "id") @PathVariable("id") Long id) {

        boolean flag = announcementService.removeById(id);
        return toDeleteAjax(flag, "删除公告成功");
    }

    @Operation(summary = "根据id查询公告")
    @GetMapping("{id}")
    private Result<AnnouncementVO> findById(@Parameter(description = "id") @PathVariable("id") Long id) {

        AnnouncementVO announcementVO = announcementService.findById(id);
        return toGetAjax(announcementVO == null, "公告查询成功", announcementVO);
    }

    @Operation(summary = "根据课程id查询公告")
    @GetMapping("/courseId/{id}")
    private Result<List<AnnouncementVO>> findByCourseId(@Parameter(description = "id") @PathVariable("id") Long courseId) {

        List<AnnouncementVO> announcementVOS = announcementService.findByCourseId(courseId);
        return toGetAjax(CollUtil.isEmpty(announcementVOS), "公告查询成功", announcementVOS);
    }

    @Operation(summary = "根据学生id查询公告")
    @GetMapping("/studentId")
    private Result<List<AnnouncementVO>> findByStudentId() {

        List<AnnouncementVO> announcementVOS = announcementService.findByStudentId();
        return toGetAjax(CollUtil.isEmpty(announcementVOS), "公告查询成功", announcementVOS);
    }
}
