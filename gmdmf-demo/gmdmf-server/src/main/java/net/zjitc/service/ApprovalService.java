package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.ApprovalDTO;
import net.zjitc.entity.Approval;
import net.zjitc.vo.ApprovalVO;

import java.util.List;


public interface ApprovalService extends IService<Approval> {

    boolean saveApproval(ApprovalDTO approvalDTO);

    boolean updateApproval(ApprovalDTO approvalDTO);

    List<ApprovalVO> findAllApproval();
}
