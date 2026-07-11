package net.zjitc.controller;

import cn.hutool.extra.mail.MailUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.LoginDTO;
import net.zjitc.result.Result;
import net.zjitc.service.LoginService;
import net.zjitc.utils.CheckCodeUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "登录相关接口")
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "获取邮箱验证码")
    @PostMapping
    private Result<String> sendEmailCode(@RequestBody LoginDTO loginDTO) {

        // 通过用户名和邮箱查询该用户是否存在
        loginService.checkUser(loginDTO);

        // 发送邮件
        String code = CheckCodeUtil.getCode();
        String content = "您的验证码是：" + code + "，验证码有效时间为 3 分钟";
        MailUtil.send(loginDTO.getEmail(), "验证码", content, false);

        // 在 Redis 中存储，设置过期时间 3 分钟
        stringRedisTemplate.opsForValue().set(loginDTO.getEmail(), code, 3, TimeUnit.MINUTES);
        return Result.success(Code.POST_OK, "验证码已发送至邮箱", code);
    }

    @Operation(summary = "忘记密码")
    @PatchMapping
    private Result<String> updatePassword(@RequestBody LoginDTO loginDTO) {

        // 从 redis 中获取验证码
        String checkCode = stringRedisTemplate.opsForValue().get(loginDTO.getEmail());

        if (CheckEmptyOrRuleUtil.isEmptyString(checkCode)) return Result.error(Code.PUT_ERR, "验证码已过期");
        if (!CheckCodeUtil.checkCode(loginDTO.getCode(), checkCode)) return Result.error(Code.PUT_ERR, "验证码错误");

        // 调用修改密码
        loginService.updateLoginPassword(loginDTO);

        // 删除 redis 中的验证码
        stringRedisTemplate.opsForValue().getOperations().delete(loginDTO.getEmail());
        return Result.success(Code.PUT_OK, "修改密码成功");
    }
}
