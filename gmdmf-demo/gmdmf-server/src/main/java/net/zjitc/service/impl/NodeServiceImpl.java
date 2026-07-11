package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.NodeDTO;
import net.zjitc.entity.Chapter;
import net.zjitc.entity.Course;
import net.zjitc.entity.Node;
import net.zjitc.enums.Pub;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.NodeMapper;
import net.zjitc.service.NodeService;
import net.zjitc.utils.AliOssUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.vo.NodeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node> implements NodeService {

    @Override
    public List<NodeVO> findAll() {

        List<Node> nodes = lambdaQuery().orderByAsc(Node::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(nodes)) return Collections.emptyList();
        return BeanUtil.copyToList(nodes, NodeVO.class);
    }

    @Override
    public List<NodeVO> findByChapterId(Long chapterId) {

        CheckEmptyOrRuleUtil.isEmptyId(chapterId);
        List<Node> nodes = lambdaQuery().eq(Node::getChapterId, chapterId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(nodes)) return Collections.emptyList();
        return BeanUtil.copyToList(nodes, NodeVO.class);
    }

    @Override
    public boolean saveNode(NodeDTO nodeDTO) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();

        Chapter chapter = Db.lambdaQuery(Chapter.class).eq(Chapter::getId, nodeDTO.getChapterId()).one();
        if (chapter == null) throw new BusinessException(Code.BUSINESS_ERR, "章节数据异常");

        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, chapter.getCourseId()).one();
        if (course == null) throw new BusinessException(Code.BUSINESS_ERR, "课程数据异常");

        if (course.getPub() == Pub.APPROVAL) throw new BusinessException(Code.BUSINESS_ERR, "课程审批中，请勿修改");
        if (course.getPub() == Pub.PUBLIC) throw new BusinessException(Code.BUSINESS_ERR, "课程已上线，请勿修改");

        Node node = BeanUtil.copyProperties(nodeDTO, Node.class);
        node.setContent(nodeDTO.getContent()).setCreateTime(LocalDateTime.now());
        return save(node);
    }

    @Override
    public boolean updateNode(NodeDTO nodeDTO) throws Exception {

        checkPub(nodeDTO.getId());
        Node oldNode = getById(nodeDTO.getId());
        if (!CheckEmptyOrRuleUtil.isEmptyString(oldNode.getContent()) && !CheckEmptyOrRuleUtil.isEmptyString(nodeDTO.getContent())) {
            String replace = oldNode.getContent().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
            AliOssUtil.deleteFile(replace);
        }
        Node node = BeanUtil.copyProperties(nodeDTO, Node.class);
        return updateById(node);
    }

    @Override
    public boolean deleteNode(Long id) throws Exception {

        Node node = checkPub(id);
        if (!CheckEmptyOrRuleUtil.isEmptyString(node.getContent())) {
            String replace = node.getContent().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
            AliOssUtil.deleteFile(replace);
        }
        return removeById(id);
    }

    private Node checkPub(Long nodeId) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();
        CheckEmptyOrRuleUtil.isEmptyId(nodeId);

        Node node = getById(nodeId);
        if (node == null) throw new BusinessException(Code.BUSINESS_ERR, "节点数据异常");

        Chapter chapter = Db.lambdaQuery(Chapter.class).eq(Chapter::getId, node.getChapterId()).one();
        if (chapter == null) throw new BusinessException(Code.BUSINESS_ERR, "章节数据异常");

        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, chapter.getCourseId()).one();
        if (course == null) throw new BusinessException(Code.BUSINESS_ERR, "课程数据异常");
        if (course.getPub() == Pub.APPROVAL) throw new BusinessException(Code.BUSINESS_ERR, "课程审批中，请勿修改");
        if (course.getPub() == Pub.PUBLIC) throw new BusinessException(Code.BUSINESS_ERR, "课程已上线，请勿修改");

        return node;
    }
}
