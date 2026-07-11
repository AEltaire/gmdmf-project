package net.zjitc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperStudent {

    private Long id;
    private Long paperApprovalId;
    private Long subjectId;
    private String type;
    private String input;
    private String answer;
    private Float score;
    private Float point;
    private boolean result;
}
