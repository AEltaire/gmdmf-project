package net.zjitc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StudentDTO {

    @NotNull(groups = Update.class, message = "id不能为空")
    private Long id;

    @NotBlank(groups = Add.class, message = "姓名不能为空")
    private String name;

    private String username;

    @NotBlank(groups = Add.class, message = "性别不能为空")
    private String sex;

    private String address;

    @NotNull(groups = Add.class, message = "手机号不能为空")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(groups = Add.class, message = "学校不能为空")
    private String school;

    @NotNull(groups = Add.class, message = "学院不能为空")
    private String department;

    @NotNull(groups = Add.class, message = "专业不能为空")
    private String major;

    @NotNull(groups = Add.class, message = "班级不能为空")
    private String teamName;

    @NotNull(groups = Add.class, message = "学号不能为空")
    private String number;

    @NotBlank(groups = Add.class, message = "身份证不能为空")
    @Pattern(regexp = "/^([1-6][1-9]|50)\\d{4}(18|19|20)\\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$/",
            message = "身份证格式错误"
    )
    private String idCard;

    @URL(message = "URL格式不正确")
    private String picture;

    private String introduce;

//    @UserRule
    private String rule;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
