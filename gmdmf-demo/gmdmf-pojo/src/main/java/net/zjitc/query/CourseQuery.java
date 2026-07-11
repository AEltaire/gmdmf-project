package net.zjitc.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "课程条件查询对象")
public class CourseQuery extends PageQuery{

    @Schema(description = "专业名")
    private String majorName;

    @Schema(description = "课程名")
    private String courseName;
}
