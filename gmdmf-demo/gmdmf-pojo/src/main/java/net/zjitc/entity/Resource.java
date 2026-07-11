package net.zjitc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Resource {

    private Long id;
    private Long courseId;
    private String teacher;
    private String name;
    private String type;
    private String url;
    private LocalDateTime createTime;
}
