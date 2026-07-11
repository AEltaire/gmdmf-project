package net.zjitc.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperSubjectVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long paperId;
    private Long subjectId;
    private Float score;
}
