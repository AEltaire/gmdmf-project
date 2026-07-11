package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.PaperStudentDTO;
import net.zjitc.entity.PaperStudent;
import net.zjitc.vo.PaperStudentVO;

import java.util.List;
import java.util.Map;

public interface PaperStudentService extends IService<PaperStudent> {

    boolean saveOrUpdatePaperStudent(List<PaperStudentDTO> paperStudentDTOS);

    boolean approvalPaperStudent(List<PaperStudentDTO> paperStudentDTOS);

    Map<String, List<PaperStudentVO>> findByPaperApprovalId(Long paperApprovalId);

    Map<String, List<PaperStudentVO>> findByPaperIdAndStudentId(Long paperId, Long studentId);

    List<Object> findByStudentId(Long studentId, Long courseId);
}
