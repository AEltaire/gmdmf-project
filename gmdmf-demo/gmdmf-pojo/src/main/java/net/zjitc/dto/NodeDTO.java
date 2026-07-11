package net.zjitc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.zjitc.enums.NodeStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NodeDTO {

    private Long id;
    private Long chapterId;
    private String title;
    private MultipartFile file;
    private String content;
    private String introduce;
    private String approval;
    private String status;
}
