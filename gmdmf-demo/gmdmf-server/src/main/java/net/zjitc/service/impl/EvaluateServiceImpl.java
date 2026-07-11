package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import net.zjitc.constant.Code;
import net.zjitc.dto.EvaluateDTO;
import net.zjitc.entity.Course;
import net.zjitc.entity.Evaluate;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.EvaluateMapper;
import net.zjitc.service.EvaluateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@Transactional
public class EvaluateServiceImpl extends ServiceImpl<EvaluateMapper, Evaluate> implements EvaluateService {

    @Override
    public boolean saveEvaluate(EvaluateDTO evaluateDTO) {
        Evaluate evaluate = BeanUtil.copyProperties(evaluateDTO, Evaluate.class);
        evaluate.setUserId(ThreadLocalUtil.getId());
        boolean flag = save(evaluate);
        CheckEmptyOrRuleUtil.isEmptyId(evaluate.getCourseId());
        updateCourseAssess(evaluate.getCourseId());
        return flag;
    }

    @Override
    public boolean updateEvaluate(EvaluateDTO evaluateDTO) {
        boolean flag = lambdaUpdate()
                .eq(Evaluate::getUserId, ThreadLocalUtil.getId())
                .eq(Evaluate::getCourseId, evaluateDTO.getCourseId())
                .set(Evaluate::getScore, evaluateDTO.getScore())
                .update();
        CheckEmptyOrRuleUtil.isEmptyId(evaluateDTO.getCourseId());
        updateCourseAssess(evaluateDTO.getCourseId());
        return flag;
    }

    @Override
    public Evaluate findBySidAndCid(Long cid) {
        return lambdaQuery().eq(cid != null, Evaluate::getCourseId, cid)
                .eq(Evaluate::getUserId, ThreadLocalUtil.getId()).one();
    }

    private void updateCourseAssess(Long courseId) {
        List<Evaluate> evaluates = lambdaQuery().list();
        Float reduce = evaluates.stream().map(Evaluate::getScore).reduce(Float.valueOf(0), Float::sum);
        Float avg = reduce / evaluates.size();
        Db.lambdaUpdate(Course.class).eq(Course::getId, courseId).set(Course::getAssess, avg).update();
    }
}
