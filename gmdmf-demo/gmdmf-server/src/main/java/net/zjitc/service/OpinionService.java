package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.OpinionDTO;
import net.zjitc.entity.Opinion;
import net.zjitc.vo.OpinionVO;

import java.util.List;

public interface OpinionService extends IService<Opinion> {

    // 意见反馈
    boolean save(OpinionDTO opinionDTO);

    // 删除意见反馈
    boolean deleteOpinion(Long id);

    List<OpinionVO> findAll();
}
