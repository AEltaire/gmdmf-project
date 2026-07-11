package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.LoginDTO;
import net.zjitc.dto.StudentDTO;
import net.zjitc.dto.TeacherDTO;
import net.zjitc.entity.Teacher;
import net.zjitc.result.Result;
import net.zjitc.service.TeacherService;
import net.zjitc.utils.JwtUtil;
import net.zjitc.utils.Md5Util;
import net.zjitc.vo.StudentVO;
import net.zjitc.vo.TeacherVO;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "教师相关接口")
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController extends BaseController {

    private final TeacherService teacherService;
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "教师登录")
    @PostMapping("/login")
    private Result<String> login(@Parameter(description = "登陆数据") @RequestBody LoginDTO loginDTO) {

        Teacher teacher = teacherService.findByUsername(loginDTO.getUsername());

        if (teacher == null) {
            return Result.error(Code.GET_ERR, "用户不存在");
        }

        if (!teacher.getPassword().equals(Md5Util.getMD5String(loginDTO.getPassword()))) {
            return Result.error(Code.GET_ERR, "密码错误");
        }

        // ThreadLocal
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", teacher.getId());
        claims.put("name", teacher.getName());
        claims.put("username", teacher.getUsername());
        claims.put("rule", teacher.getRule());
        claims.put("school", teacher.getSchool());

        // JWT 令牌
        String token = JwtUtil.genToken(claims);

        // redis 存储，过期时间 3 天
        stringRedisTemplate.opsForValue().set(token, token, 3, TimeUnit.DAYS);

        return Result.success(Code.POST_OK, "教师登录成功", token);
    }

    @Operation(summary = "教师注册")
    @PostMapping("/register")
    private Result<TeacherVO> save(@Parameter(description = "教师数据") @Validated(TeacherDTO.Add.class) @RequestBody TeacherDTO teacherDTO) {

        Teacher teacher = teacherService.findByUsername(teacherDTO.getUsername());

        if (teacher != null) return Result.error(Code.POST_ERR, "用户名已存在");

        boolean flag = teacherService.saveTeacher(teacherDTO);

        return toPostAjax(flag, "注册教师成功");
    }

    @Operation(summary = "修改密码")
    @PatchMapping("/password")
    private Result<StudentVO> updatePassword(@RequestBody LoginDTO loginDTO,
                                             @RequestHeader("Authorization") String token) {

        boolean flag = teacherService.updateByPassword(loginDTO.getOldPassword(), loginDTO.getPassword());
        if (flag) {
            stringRedisTemplate.opsForValue().getOperations().delete(token);
            return Result.success(Code.PUT_OK, "修改密码成功");
        }
        return Result.error(Code.PUT_ERR, "系统繁忙，请稍后再试");
    }

    @Operation(summary = "修改头像")
    @PatchMapping("/picture")
    private Result<TeacherVO> updatePicture(@RequestParam @URL String picture) throws Exception {

        boolean flag = teacherService.updateByPicture(picture);
        return toPutAjax(flag, "修改头像成功");
    }

    @Operation(summary = "修改教师数据")
    @PutMapping
    private Result<TeacherVO> update(@Parameter(description = "教师数据") @Validated(TeacherDTO.Update.class) @RequestBody TeacherDTO teacherDTO) {

        boolean flag = teacherService.updateTeacher(teacherDTO);
        return toPutAjax(flag, "修改教师数据成功");
    }

    // 管理端批量修改学生数据
    @Operation(summary = "批量修改学生数据")
    @PutMapping("/updateList")
    private Result<StudentVO> updateList(@Parameter(description = "学生数据") @Validated(StudentDTO.Update.class) @RequestBody List<TeacherDTO> teacherDTOS) {
        boolean flag = teacherService.updateTeacherList(teacherDTOS);
        return toPutAjax(flag, "修改学生数据成功");
    }

    @Operation(summary = "管理端批量删除教师")
    @PostMapping("/deleteList")
    private Result<TeacherVO> delete(@RequestBody List<Long> ids) {
        boolean flag = teacherService.deleteTeacherList(ids);
        return toDeleteAjax(flag, "删除教师成功");
    }

    @Operation(summary = "获取教师信息")
    @GetMapping
    private Result<TeacherVO> getTeacherInfo() {
        TeacherVO teacherVO = teacherService.getTeacherInfo();
        return toGetAjax(teacherVO == null, "获取教师信息成功", teacherVO);
    }

    @Operation(summary = "管理端查询全部教师")
    @GetMapping("/findAll")
    private Result<List<TeacherVO>> findAll() {
        List<TeacherVO> teacherVO = teacherService.findAll();
        return toGetAjax(CollUtil.isEmpty(teacherVO), "查询教师成功", teacherVO);
    }
}
