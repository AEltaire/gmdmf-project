package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.PaperSubjectDTO;
import net.zjitc.entity.PaperSubject;
import net.zjitc.vo.SubjectVO;

import java.util.List;
import java.util.Map;

public interface PaperSubjectService extends IService<PaperSubject> {

    boolean savePaperSubject(List<PaperSubjectDTO> paperSubjectDTOList);

    boolean deleteByPaperId(Long paperId);

    boolean updatePaperSubject(Long paperId, List<PaperSubjectDTO> paperSubjectDTOList);

    Map<String, List<SubjectVO>> findAll(Long paperId);
}
