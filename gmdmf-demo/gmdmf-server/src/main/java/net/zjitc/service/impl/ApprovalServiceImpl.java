package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ApprovalDTO;
import net.zjitc.entity.Approval;
import net.zjitc.entity.Course;
import net.zjitc.entity.Major;
import net.zjitc.entity.Teacher;
import net.zjitc.enums.Pub;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.ApprovalMapper;
import net.zjitc.service.ApprovalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.vo.ApprovalVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalServiceImpl extends ServiceImpl<ApprovalMapper, Approval> implements ApprovalService {

    @Override
    public boolean saveApproval(ApprovalDTO approvalDTO) {
        CheckEmptyOrRuleUtil.isTeacher();
        Approval approval = BeanUtil.copyProperties(approvalDTO, Approval.class);
        approval.setCreateTime(LocalDateTime.now()).setState(false);
        return save(approval);
    }

    @Override
    public boolean updateApproval(ApprovalDTO approvalDTO) {
        CheckEmptyOrRuleUtil.isManager();
        Approval approval = lambdaQuery().eq(Approval::getCourseId, approvalDTO.getCourseId()).one();
        if (approval == null) throw new BusinessException(Code.BUSINESS_ERR, "数据异常");
        approval.setHandleTime(LocalDateTime.now()).setState(approvalDTO.getState()).setManage(approvalDTO.getManage());
        Course course = Db.getById(approvalDTO.getCourseId(), Course.class);
        if (approvalDTO.getState()) course.setPub(Pub.PUBLIC);
        else course.setPub(Pub.PRIVATE);
        Db.updateById(course);
        return updateById(approval);

    }

    @Override
    public List<ApprovalVO> findAllApproval() {
        CheckEmptyOrRuleUtil.isManager();
        List<Approval> approvals = lambdaQuery().eq(Approval::getManage, null).eq(Approval::getHandleTime, null).eq(Approval::getState, false).list();
        return approvals.stream().map(this::convertor).toList();
    }

    private ApprovalVO convertor(Approval approval) {

        ApprovalVO approvalVO = BeanUtil.copyProperties(approval, ApprovalVO.class);
        CheckEmptyOrRuleUtil.isEmptyId(approval.getCourseId());
        Course course = Db.getById(approval.getCourseId(), Course.class);
        Major major = Db.getById(course.getMajorId(), Major.class);
        Teacher teacher = Db.getById(course.getTeacherId(), Teacher.class);

        return approvalVO.setTeacherName(teacher.getName())
                .setMajorName(major.getName())
                .setStartTime(course.getStartTime())
                .setEndTime(course.getEndTime())
                .setPoint(course.getPoint())
                .setIntroduce(course.getIntroduce())
                .setPicture(course.getPicture());
    }
}
