package net.zjitc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperSubjectDTO {

    private Long id;
    private Long paperId;
    private Long subjectId;
    private Float score;
}
