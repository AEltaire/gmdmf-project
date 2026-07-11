package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.LoginDTO;
import net.zjitc.dto.StudentDTO;
import net.zjitc.entity.Student;
import net.zjitc.result.Result;
import net.zjitc.service.StudentService;
import net.zjitc.utils.JwtUtil;
import net.zjitc.utils.Md5Util;
import net.zjitc.vo.StudentVO;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "学生相关接口")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController extends BaseController {

    private final StudentService studentService;
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "学生登录")
    @PostMapping("/login")
    private Result<String> login(@Parameter(description = "登录数据") @RequestBody LoginDTO loginDTO) {

        Student student = studentService.findByUsername(loginDTO.getUsername());

        if (student == null) return Result.error(Code.GET_ERR, "用户不存在");

        if (!student.getPassword().equals(Md5Util.getMD5String(loginDTO.getPassword()))) {
            return Result.error(Code.GET_ERR, "密码错误");
        }

        // TreadLocal
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", student.getId());
        claims.put("name", student.getName());
        claims.put("username", student.getUsername());
        claims.put("rule", student.getRule());

        // JWT 令牌
        String token = JwtUtil.genToken(claims);

        // redis 存储，过期时间 3 天
        stringRedisTemplate.opsForValue().set(token, token, 3, TimeUnit.DAYS);

        return Result.success(Code.GET_OK, "学生登录成功", token);
    }

    // 管理端批量注册
    @Operation(summary = "学生注册")
    @PostMapping("/register")
    private Result<StudentVO> register(@RequestBody @Validated({StudentDTO.Add.class}) List<StudentDTO> studentDTO) {

        boolean flag = studentService.register(studentDTO);
        return toPostAjax(flag, "学生注册成功");
    }

    @Operation(summary = "修改密码")
    @PatchMapping("/password")
    private Result<StudentVO> updatePassword(@RequestBody LoginDTO loginDTO,
                                             @RequestHeader("Authorization") String token) {

        boolean flag = studentService.updatePassword(loginDTO.getOldPassword(), loginDTO.getPassword());
        if (flag) {
            stringRedisTemplate.opsForValue().getOperations().delete(token);
            return Result.success(Code.PUT_OK, "修改密码成功");
        }
        return Result.error(Code.PUT_ERR, "系统繁忙，请稍后再试");
    }

    @Operation(summary = "修改头像")
    @PatchMapping("/picture")
    private Result<StudentVO> updatePicture(@RequestParam @URL String picture) throws Exception {

        boolean flag = studentService.updatePicture(picture);
        return toPutAjax(flag, "修改头像成功");
    }

    @Operation(summary = "修改学生数据")
    @PutMapping
    private Result<StudentVO> update(@Parameter(description = "学生数据") @Validated(StudentDTO.Update.class) @RequestBody StudentDTO studentDTO) {

        boolean flag = studentService.updateStudent(studentDTO);
        return toPutAjax(flag, "修改信息成功");
    }

    // 管理端批量修改学生数据
    @Operation(summary = "批量修改学生数据")
    @PutMapping("/updateList")
    private Result<StudentVO> updateList(@Parameter(description = "学生数据") @Validated(StudentDTO.Update.class) @RequestBody List<StudentDTO> studentDTOS) {
        boolean flag = studentService.updateStudentList(studentDTOS);
        return toPutAjax(flag, "修改信息成功");
    }

    // 管理端批量删除学生数据
    @Operation(summary = "批量删除学生数据")
    @PostMapping("/deleteList")
    private Result<Student> delete(@RequestBody  List<Long> ids) {
        boolean flag = studentService.deleteStudent(ids);
        return toDeleteAjax(flag, "删除学生成功");
    }

    @Operation(summary = "获取学生信息")
    @GetMapping
    private Result<StudentVO> getStudentInfo() {
        StudentVO studentVO = studentService.getStudentInfo();
        return toGetAjax(studentVO == null, "获取学生信息成功", studentVO);
    }

    @Operation(summary = "根据班级id查询学生")
    @GetMapping("/teamId/{id}")
    private Result<List<StudentVO>> findByTeamId(@Parameter(description = "id") @PathVariable("id") Long teamId) {
        List<StudentVO> studentVOList = studentService.findByTeamId(teamId);
        return toGetAjax(CollUtil.isEmpty(studentVOList), "查询学生成功", studentVOList);
    }

    // 管理端查询学生
    @Operation(summary = "管理端查询所有学生")
    @GetMapping("/findAll")
    private Result<List<StudentVO>> findAll() {
        List<StudentVO> studentVOS = studentService.findAll();
        return toGetAjax(CollUtil.isEmpty(studentVOS), "查询学生成功", studentVOS);
    }
}
