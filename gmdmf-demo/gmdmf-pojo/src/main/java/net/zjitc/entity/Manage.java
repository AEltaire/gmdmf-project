package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.zjitc.annotation.UserRule;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String username;

    // 不参与查询，但是不约束自定义查询方法
    @TableField(select = false)
    private String password;

    private String introduce;

    private String picture;

    @UserRule
    private String rule;
}
