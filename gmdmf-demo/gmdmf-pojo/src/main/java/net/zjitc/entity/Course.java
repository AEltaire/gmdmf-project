package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.zjitc.enums.Pub;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class Course {

    private Long id;    // 课程id

    private String name;    // 课程名

    private Long majorId;

    // 线程提供
    private Long teacherId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Float point;    // 学分

    private String introduce;   // 介绍

    private String picture;

    private Integer number; // 人数

    private Float assess;   // 评分

    private Integer love;

    private Pub pub;

    private Boolean exhibition;
}
