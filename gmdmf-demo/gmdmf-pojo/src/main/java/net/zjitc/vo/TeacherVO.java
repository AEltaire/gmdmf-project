package net.zjitc.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    private String sex;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime birth;

    private String phone;

    private String email;

    private String address;

    private String school;

    private String department;

    private String username;

    private String picture;

    private String introduce;

    private String rule;
}
