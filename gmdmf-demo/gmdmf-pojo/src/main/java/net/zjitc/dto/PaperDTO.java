package net.zjitc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperDTO {

    private Long id;

    @NotBlank(message = "题目集类型不能为空")
    private String type;

    @NotNull(message = "课程id不能为空")
    private Long courseId;

    @NotBlank(message = "题目集名不能为空")
    private String title;

    @NotBlank(message = "开始时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy/M/d HH:mm:ss")
    private LocalDateTime startTime;

    @NotBlank(message = "结束时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy/M/d HH:mm:ss")
    private LocalDateTime endTime;
}
