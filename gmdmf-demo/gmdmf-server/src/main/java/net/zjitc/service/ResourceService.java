package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.ResourceDTO;
import net.zjitc.entity.Resource;
import net.zjitc.vo.ResourceVO;


import java.util.List;
import java.util.Map;

public interface ResourceService extends IService<Resource> {

    boolean saveResource(ResourceDTO resourceDTO);

    Map<String, List<ResourceVO>> findByCourseId(Long courseId);
}
