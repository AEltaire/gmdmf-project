package net.zjitc.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/*
* DTO: 是和数据库对应的类
* */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AnnouncementDTO {

    private Long id;
    @NotNull(message = "课程id不能为空")
    private Long courseId;
    @NotBlank(message = "公告标题不能为空")
    private String title;
    @NotBlank(message = "公告内容不能为空")
    private String content;
}
