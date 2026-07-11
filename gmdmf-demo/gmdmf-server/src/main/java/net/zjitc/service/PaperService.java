package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.PaperDTO;
import net.zjitc.entity.Paper;
import net.zjitc.query.PaperQuery;
import net.zjitc.vo.PaperVO;

import java.util.List;

public interface PaperService extends IService<Paper> {

    // 添加
    boolean savePaper(PaperDTO paperDTO);

    // 删除
    boolean deletePaper(Long id);

    // 修改
    boolean updatePaper(PaperDTO paperDTO);

    // 查询
    PaperVO findById(Long id);

    List<PaperVO> findByQuery(PaperQuery paperQuery);
}
