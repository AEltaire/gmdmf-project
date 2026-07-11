package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Student {

    private Long id;

    private String name;

    private String sex;

    private LocalDateTime birth;

    private String address;

    private String phone;

    private String email;

    private String school;

    private String department;

    private String major;

    private Long teamId;

    private String number;

    private String idCard;

    private String username;

    @TableField(select = false)
    private String password;

    private String picture;

    private String introduce;

    private String rule;
}
