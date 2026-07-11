package net.zjitc.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TeamDTO {

    @NotNull(groups = Update.class, message = "id不能为空")
    private Long id;
    @NotBlank(message = "班级名不能为空")
    private String name;

    private Long schoolId;
    private String schoolName;
    private Boolean schoolExhibition;

    private Long departmentId;
    private String departmentName;
    private Boolean departmentExhibition;

    private Long majorId;
    private String majorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/M/d HH:mm:ss")
    private LocalDateTime endTime;

    public interface Add extends Default {}
    public interface Update extends Default {}
}
