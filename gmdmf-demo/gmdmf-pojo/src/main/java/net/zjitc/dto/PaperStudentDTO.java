package net.zjitc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperStudentDTO {

    private Long id;
    private String type;
    private Long paperId;
    private Long paperApprovalId;
    private Long subjectId;
    private String input;
    private Float score;
    private Float point;
}
