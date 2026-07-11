package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.Manage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ManageMapper extends BaseMapper<Manage> {

    @Select("select * from manage where username = #{username}")
    Manage findByUsername(String username);
}
