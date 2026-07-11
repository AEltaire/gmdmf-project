package net.zjitc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
public class CourseVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;    // 课程id

    private String name;    // 课程名

    private Long majorId;   // 专业id

    private String majorName;

    private String teacherName;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
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
