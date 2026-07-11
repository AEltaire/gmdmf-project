package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.NoteDTO;
import net.zjitc.result.Result;
import net.zjitc.service.NoteService;
import net.zjitc.vo.NoteVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "笔记相关接口")
@RestController
@RequestMapping("note")
@RequiredArgsConstructor
public class NoteController extends BaseController {

    private final NoteService noteService;

    @Operation(summary = "添加笔记")
    @PostMapping
    private Result<NoteVO> save(@Parameter(description = "笔记数据") @RequestBody @Validated(NoteDTO.Add.class) NoteDTO noteDTO) {

        boolean flag = noteService.saveNote(noteDTO);
        return toPostAjax(flag, "添加笔记成功");
    }

    @Operation(summary = "修改笔记")
    @PutMapping
    private Result<NoteVO> update(@Parameter(description = "笔记数据") @RequestBody @Validated(NoteDTO.Update.class) NoteDTO noteDTO) {

        boolean flag = noteService.updateNote(noteDTO);
        return toPutAjax(flag, "修改笔记成功");
    }

    @Operation(summary = "删除笔记")
    @DeleteMapping("{id}")
    private Result<NoteVO> delete(@Parameter(description = "id") @PathVariable("id") Long id) {

        boolean flag = noteService.removeById(id);
        return toDeleteAjax(flag, "删除笔记成功");
    }

    @Operation(summary = "根据学生id查询笔记")
    @GetMapping("{id}")
    private Result<NoteVO> findById(@PathVariable Long id) {

        NoteVO note = noteService.findById(id);
        return toGetAjax(note == null, "查询笔记成功", note);
    }

    @Operation(summary = "根据学生id查询笔记")
    @GetMapping
    private Result<List<NoteVO>> findByStudentId() {

        List<NoteVO> notes = noteService.findByStudentId();
        return toGetAjax(CollUtil.isEmpty(notes), "查询笔记成功", notes);
    }

    @Operation(summary = "根据课程id查询学生笔记")
    @GetMapping("/courseId/{id}")
    private Result<List<NoteVO>> findByCourseId(@PathVariable("id") Long courseId) {

        List<NoteVO> notes = noteService.findByCourseId(courseId);
        return toGetAjax(CollUtil.isEmpty(notes), "查询笔记成功", notes);
    }
}
