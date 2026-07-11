package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.MajorDTO;
import net.zjitc.entity.Major;
import net.zjitc.vo.MajorVO;

import java.util.List;


public interface MajorService extends IService<Major> {

    List<MajorVO> findAll();

    boolean saveOrUpdateMajor(List<MajorDTO> majorDTO);

    boolean deleteMajor(List<MajorDTO> majorDTO);

    List<MajorVO> adminFindAll();
}
