package net.zjitc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TeamVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private Long schoolId;
    private String schoolName;
    private Boolean schoolExhibition;
    private Long departmentId;
    private String departmentName;
    private Boolean departmentExhibition;
    private Long majorId;
    private String majorName;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime endTime;
}
