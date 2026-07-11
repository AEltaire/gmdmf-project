package net.zjitc.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperQuery extends PageQuery{

    private Long id;

    private String type;

    private Long courseId;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy/M/d HH:mm:ss")
    private LocalDateTime startTime;

    // false 教师 | true 学生
    private Boolean notEndTime = false;
}
