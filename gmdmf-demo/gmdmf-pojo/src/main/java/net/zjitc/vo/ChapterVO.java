package net.zjitc.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class ChapterVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    // TableField 注解作用：指定数据库字段名，typeHandler 指定类型处理器
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<NodeVO> nodeVOS;
}
