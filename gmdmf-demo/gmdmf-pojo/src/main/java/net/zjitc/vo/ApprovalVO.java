package net.zjitc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ApprovalVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String teacherName;

    private String majorName;

    private String courseName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Float point;

    private String introduce;

    private String picture;

    private String manage;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime handleTime;
}
