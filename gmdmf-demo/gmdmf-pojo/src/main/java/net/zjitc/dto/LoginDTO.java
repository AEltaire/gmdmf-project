package net.zjitc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.zjitc.annotation.UserRule;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "登录DTO对象")
public class LoginDTO {

    @UserRule
    private String rule;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    private String oldPassword;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "邮箱验证码")
    private String code;
}
