package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.PaperApproval;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkStudentMapper extends BaseMapper<PaperApproval> {
}
