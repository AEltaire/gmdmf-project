package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.TeamDTO;
import net.zjitc.result.Result;
import net.zjitc.query.TeamQuery;
import net.zjitc.service.TeamService;
import net.zjitc.vo.StudentVO;
import net.zjitc.vo.TeamVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班级相关接口")
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController extends BaseController {

    private final TeamService teamService;

    @Operation(summary = "添加班级")
    @PostMapping("/save")
    private Result<List<TeamVO>> save(@RequestBody TeamDTO teamDTO) {

        boolean flag = teamService.saveTeam(teamDTO);
        return toPostAjax(flag);
    }

    @Operation(summary = "修改班级")
    @PutMapping("/updateList")
    private Result<List<TeamVO>> update(@RequestBody List<TeamDTO> teamDTOS) {

        boolean flag = teamService.updateList(teamDTOS);
        return toPostAjax(flag);
    }

    @Operation(summary = "删除班级")
    @PostMapping("/deleteList")
    private Result<List<TeamVO>> delete(@RequestBody List<Long> teamIds) {

        boolean flag = teamService.deleteList(teamIds);
        return toPostAjax(flag);
    }

    @Operation(summary = "根据课程id查询班级")
    @GetMapping("/courseId/{id}")
    private Result<List<TeamVO>> findByCourseId(@PathVariable("id") Long courseId) {

        List<TeamVO> teamVOS = teamService.findByCourseId(courseId);
        return toGetAjax(CollUtil.isEmpty(teamVOS), "查询成功", teamVOS);
    }

    @Operation(summary = "根据班级id查询学生")
    @GetMapping("{id}")
    private Result<List<StudentVO>> findByTeamId(@PathVariable("id") Long id) {

        List<StudentVO> studentVOS = teamService.findById(id);
        return toGetAjax(CollUtil.isEmpty(studentVOS), "查询成功", studentVOS);
    }

    @Operation(summary = "根据教师id查询班级")
    @GetMapping("/teacherId")
    private Result<List<TeamVO>> findByTeacherId() {

        List<TeamVO> teamVOS = teamService.findByTeacherId();
        return toGetAjax(CollUtil.isEmpty(teamVOS), "查询成功", teamVOS);
    }

    @Operation(summary = "根据条件查询班级")
    @GetMapping("/findByTeamQuery")
    private Result<List<TeamVO>> findByTeamQuery(TeamQuery teamQuery) {

        List<TeamVO> teamVOS = teamService.findByTeamQuery(teamQuery);
        return toGetAjax(CollUtil.isEmpty(teamVOS), "查询成功", teamVOS);
    }

    @Operation(summary = "查询所有班级")
    @GetMapping("/findAll")
    private Result<List<Object>> findAll() {

        List<Object> teamVOS = teamService.findAll();
        return toGetAjax(CollUtil.isEmpty(teamVOS), "查询成功", teamVOS);
    }
}
