package net.zjitc.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.zjitc.enums.Pub;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String warehouse;
    private String type;
    private String label;
    private String question;
    private String options;
    private String answer;
    private String analysis;
    private String level;
    private Pub pub;
    private Float pass;
    private Float coefficient;
    private Float score;
}
