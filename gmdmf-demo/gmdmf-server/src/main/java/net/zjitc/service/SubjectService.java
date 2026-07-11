package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.SubjectDTO;
import net.zjitc.entity.Subject;
import net.zjitc.vo.SubjectVO;

import java.util.List;
import java.util.Map;

public interface SubjectService extends IService<Subject> {

    boolean saveSubject(SubjectDTO subjectDTO);

    boolean deleteSubject(Long id);

    boolean updateSubject(SubjectDTO subjectDTO);

    Map<String, List<SubjectVO>> findByWarehouseId(Long warehouseId);
}
