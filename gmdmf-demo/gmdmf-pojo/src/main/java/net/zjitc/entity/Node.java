package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.zjitc.enums.NodeStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Node {

    private Long id;

    private Long chapterId;

    private String title;

    private String content;

    private String introduce;

    private LocalDateTime createTime;

    private String approval;

    private NodeStatus status;
}
