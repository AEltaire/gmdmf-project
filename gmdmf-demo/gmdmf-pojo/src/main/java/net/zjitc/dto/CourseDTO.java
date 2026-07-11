package net.zjitc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.zjitc.enums.Pub;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "课程DTO对象")
public class CourseDTO {

    private Long id;

    @NotBlank(message = "课程名不为空")
    private String name;    // 课程名

    private Long majorId;

    // 管理端
    private String majorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/M/d HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/M/d HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "学分不为空")
    @Min(value = 0, message = "学分必须大于0")
    private Float point;

    private String introduce;

    private String picture;

    private Pub pub;

    private Boolean exhibition;

    // 教师端
    private List<Long> teamIds;
}
