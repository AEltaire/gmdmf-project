package net.zjitc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaperApproval {

    private Long id;
    private Long paperId;
    private Long studentId;
    private String type;
    private Float score;
    private Float point;
    private Boolean status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
