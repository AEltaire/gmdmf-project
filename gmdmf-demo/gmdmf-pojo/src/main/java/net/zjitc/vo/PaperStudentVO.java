package net.zjitc.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.zjitc.enums.Pub;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperStudentVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paperApprovalId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subjectId;
    private String type;
    private String input;
    private String answer;
    private Float score;
    private Float point;
    private String label;
    private String question;
    private String options;
    private String analysis;
    private Pub pub;
}
