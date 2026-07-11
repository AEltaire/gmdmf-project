package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.EvaluateDTO;
import net.zjitc.entity.Evaluate;

import java.util.List;


public interface EvaluateService extends IService<Evaluate> {

    Evaluate findBySidAndCid(Long cid);

    boolean saveEvaluate(EvaluateDTO evaluateDTO);

    boolean updateEvaluate(EvaluateDTO evaluateDTO);
}
