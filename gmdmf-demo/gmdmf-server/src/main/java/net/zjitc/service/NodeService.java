package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.NodeDTO;
import net.zjitc.entity.Node;
import net.zjitc.vo.NodeVO;

import java.util.List;

public interface NodeService extends IService<Node> {

    List<NodeVO> findAll();

    List<NodeVO> findByChapterId(Long chapterId);

    boolean saveNode(NodeDTO nodeDTO);

    boolean updateNode(NodeDTO nodeDTO) throws Exception;

    boolean deleteNode(Long id) throws Exception;
}
