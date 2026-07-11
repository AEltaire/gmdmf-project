package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.dto.ResourceDTO;
import net.zjitc.entity.Resource;
import net.zjitc.mapper.ResourceMapper;
import net.zjitc.service.ResourceService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.ResourceVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Override
    public boolean saveResource(ResourceDTO resourceDTO) {

        Resource resource = BeanUtil.copyProperties(resourceDTO, Resource.class);
        resource.setTeacher(ThreadLocalUtil.getName()).setCreateTime(LocalDateTime.now());
        return save(resource);
    }

    @Override
    public Map<String, List<ResourceVO>> findByCourseId(Long courseId) {

        List<Resource> resources = lambdaQuery()
                .eq(Resource::getCourseId, courseId)
                .list();

        if(CheckEmptyOrRuleUtil.isEmptyList(resources)) return Collections.emptyMap();
        List<ResourceVO> list = resources.stream().map(resource -> BeanUtil.copyProperties(resource, ResourceVO.class)).toList();
        return list.stream().collect(Collectors.groupingBy(ResourceVO::getType));
    }
}
