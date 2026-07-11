package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ChapterDTO;
import net.zjitc.result.Result;
import net.zjitc.service.ChapterService;
import net.zjitc.vo.ChapterVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "章节相关接口")
@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
public class ChapterController extends BaseController{

    private final ChapterService chapterService;

    @Operation(summary = "添加章节")
    @PostMapping
    private Result<ChapterVO> save(@RequestBody ChapterDTO chapterDTO) {

        boolean flag = chapterService.saveChapter(chapterDTO);
        return toPostAjax(flag, "添加章节成功");
    }

    @Operation(summary = "删除章节")
    @DeleteMapping("{id}")
    private Result<ChapterVO> delete(@PathVariable("id") Long id) {

        boolean flag = chapterService.deleteChapter(id);
        return toDeleteAjax(flag, "删除章节成功");
    }

    @Operation(summary = "修改章节")
    @PutMapping
    private Result<ChapterVO> update(@RequestBody ChapterDTO chapterDTO) {

        boolean flag = chapterService.updateChapter(chapterDTO);
        return toPutAjax(flag, "修改章节成功");
    }

    // 课程查询
    @Operation(summary = "根据id查询")
    @GetMapping("{id}")
    private Result<List<ChapterVO>> findByCourseId(@PathVariable("id") Long courseId) {

        List<ChapterVO> chapterVOS = chapterService.findById(courseId);
        return toGetAjax(CollUtil.isEmpty(chapterVOS), "查询章节成功", chapterVOS);
    }

    @Operation(summary = "查询所有")
    @GetMapping("/findAll")
    private Result<List<ChapterVO>> findAll() {

        List<ChapterVO> chapterVOS = chapterService.findAll();
        return toGetAjax(CollUtil.isEmpty(chapterVOS), "查询章节成功", chapterVOS);
    }

    @Operation(summary = "根据学生id查询")
    @GetMapping("/studentId")
    private Result<List<ChapterVO>> findByStudentId() {

        List<ChapterVO> chapterVOS = chapterService.findByStudentId();
        return toGetAjax(CollUtil.isEmpty(chapterVOS), "查询章节成功", chapterVOS);
    }
}
