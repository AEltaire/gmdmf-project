package net.zjitc.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ManageDTO;
import net.zjitc.entity.Manage;
import net.zjitc.result.Result;
import net.zjitc.service.ManageService;
import net.zjitc.utils.JwtUtil;
import net.zjitc.utils.Md5Util;
import net.zjitc.vo.ManageVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "管理员相关接口")
@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManageController extends BaseController{

    private final ManageService manageService;
    private final StringRedisTemplate stringRedisTemplate;

    /*
     * 接口：管理员登录
     * 测试：通过
     * */
    @Operation(summary = "管理员登录")
    @GetMapping("/login")
    private Result<String> login(@Parameter(description = "用户名") @NotBlank(message = "用户名为空") String username,
                                 @Parameter(description = "密码") @NotBlank(message = "密码为空") String password) {

        Manage manage = manageService.findByUsername(username);

        if (manage == null) {
            return Result.error(Code.GET_ERR, "用户不存在");
        }

        if (!manage.getPassword().equals(Md5Util.getMD5String(password))) {
            return Result.error(Code.GET_ERR, "密码错误");
        }

        // TreadLocal
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", manage.getId());
        claims.put("name", manage.getName());
        claims.put("username", manage.getUsername());
        claims.put("rule", manage.getRule());

        // JWT 令牌
        String token = JwtUtil.genToken(claims);

        // redis 存储，过期时间 3 天
        stringRedisTemplate.opsForValue().set(token, token, 3, TimeUnit.DAYS);

        return Result.success(Code.GET_OK, "登录成功", token);
    }

    @Operation(summary = "保存管理员")
    @PostMapping
    private Result<String> saveOrUpdate(@RequestBody ManageDTO manageDTO) throws Exception {
        boolean flag = manageService.saveOrUpdateManage(manageDTO);
        return toPostAjax(flag, "保存成功");
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    private Result<String> delete(@PathVariable Integer id) {
        boolean flag = manageService.deleteManage(id);
        return toDeleteAjax(flag, "删除成功");
    }

    @Operation(summary = "获取管理员数据")
    @GetMapping
    private Result<ManageVO> getManageInfo() {

        ManageVO manageVO = manageService.getManageInfo();
        return toGetAjax(manageVO == null, "查询成功", manageVO);
    }

    @Operation(summary = "查询所有管理")
    @GetMapping("/all")
    private Result<List<ManageVO>> findAll() {

        List<ManageVO> manageVOS = manageService.findAll();
        return toGetAjax(CollUtil.isEmpty(manageVOS), "查询成功", manageVOS);
    }
}
