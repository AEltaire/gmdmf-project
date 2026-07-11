package net.zjitc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NoteDTO {

    @NotNull(message = "id不为空", groups = NoteDTO.Update.class)
    private Long id;

    @NotNull(message = "课程id不为空")
    private Long courseId;

    @NotBlank(message = "章节标题不为空")
    private String chapterTitle;

    @NotBlank(message = "节点标题不为空")
    private String nodeTitle;

    private String title;

    @NotBlank(message = "笔记内容不为空")
    private String content;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
