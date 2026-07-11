package net.zjitc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {

    private Long id;

    @NotBlank(groups = Add.class, message = "姓名不能为空")
    private String name;

    @NotBlank(groups = Add.class, message = "性别不能为空")
    private String sex;

    @NotBlank(message = "手机不能为空")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String address;

    @NotBlank(groups = Add.class, message = "学校不能为空")
    private String school;

    @NotBlank(groups = Add.class, message = "学院不能为空")
    private String department;

    @NotBlank(groups = Add.class, message = "身份证不能为空")
    private String idCard;

    @NotBlank(groups = Add.class, message = "用户名不能为空")
    private String username;

    @NotBlank(groups = Add.class, message = "密码不能为空")
    private String password;

    @URL(message = "URL格式不正确")
    private String picture;

    private String introduce;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
