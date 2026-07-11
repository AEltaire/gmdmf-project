package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class Approval {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String manage;

    private LocalDateTime createTime;

    private LocalDateTime handleTime;

    private Boolean state;
}
