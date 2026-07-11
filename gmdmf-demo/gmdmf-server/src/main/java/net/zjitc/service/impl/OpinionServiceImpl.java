package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import net.zjitc.dto.OpinionDTO;
import net.zjitc.entity.Opinion;
import net.zjitc.entity.Student;
import net.zjitc.mapper.OpinionMapper;
import net.zjitc.service.OpinionService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.OpinionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OpinionServiceImpl extends ServiceImpl<OpinionMapper, Opinion> implements OpinionService {

//    private final WebSocketServer webSocketServer;

    @Override
    public boolean save(OpinionDTO opinionDTO) {

        Opinion opinion = BeanUtil.copyProperties(opinionDTO, Opinion.class);
        Student student = Db.lambdaQuery(Student.class).eq(Student::getId, ThreadLocalUtil.getId()).one();
        opinion.setUsername(ThreadLocalUtil.getUsername())
                .setPicture(student.getPicture())
                .setCreateTime(LocalDateTime.now());
        if (opinion.getState() == null) opinion.setState("好评");
        save(opinion);
//        webSocketServer.sendToAllClient(Json.pretty(findAll()));
        return true;
    }

    @Override
    public boolean deleteOpinion(Long id) {
        CheckEmptyOrRuleUtil.isEmptyId(id);
        return removeById(id);
    }

    @Override
    public List<OpinionVO> findAll() {
        List<Opinion> list = lambdaQuery().orderByDesc(Opinion::getCreateTime).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(list)) return Collections.emptyList();
        return BeanUtil.copyToList(list, OpinionVO.class);
    }
}
