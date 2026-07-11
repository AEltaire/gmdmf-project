package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.PaperApprovalDTO;
import net.zjitc.entity.PaperApproval;
import net.zjitc.vo.PaperApprovalVO;

import java.util.List;
import java.util.Map;

public interface PaperApprovalService extends IService<PaperApproval> {

    PaperApprovalVO findByPaperId(Long paperId);

    boolean saveOrUpdatePaperApproval(PaperApprovalDTO paperApprovalDTO);

    Map<Long, List<PaperApprovalVO>> findByCourseId(Long courseId, String type);

    boolean approvalPaper(Long paperApprovalId);

    List<PaperApprovalVO> findByStudentId();

    List<PaperApprovalVO> findByTeacherId(String type);

    boolean saveStudentPaper(Long courseId, Long paperId);
}
