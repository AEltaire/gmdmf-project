package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ChapterDTO;
import net.zjitc.entity.Chapter;
import net.zjitc.entity.Course;
import net.zjitc.entity.Node;
import net.zjitc.enums.Pub;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.ChapterMapper;
import net.zjitc.service.ChapterService;
import net.zjitc.service.CourseTeamService;
import net.zjitc.utils.AliOssUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.ChapterVO;
import net.zjitc.vo.NodeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    private final CourseTeamService courseTeamService;

    @Override
    public List<ChapterVO> findAll() {

        List<Chapter> chapters = lambdaQuery().orderByAsc(Chapter::getId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(chapters)) return Collections.emptyList();
        return convertor(chapters);
    }

    @Override
    public List<ChapterVO> findById(Long courseId) {

        CheckEmptyOrRuleUtil.isEmptyId(courseId);
        List<Chapter> chapters = lambdaQuery().eq(Chapter::getCourseId, courseId).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(chapters)) return Collections.emptyList();
        return convertor(chapters);
    }

    @Override
    public List<ChapterVO> findByStudentId() {

        // 通过 id 查询课程班级表
        List<Course> courses = courseTeamService.getCourseByStuId(ThreadLocalUtil.getId());
        // 如果为空则直接返回
        if (CheckEmptyOrRuleUtil.isEmptyList(courses)) return Collections.emptyList();
        // 收集 id
        List<Long> ids = courses.stream().map(Course::getId).toList();
        // 获取章节列表
        List<Chapter> chapters = lambdaQuery().in(Chapter::getCourseId, ids).orderByAsc(Chapter::getId).list();
        // 封装结果
        return convertor(chapters);
    }

    @Override
    public boolean saveChapter(ChapterDTO chapterDTO) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();
        CheckEmptyOrRuleUtil.isEmptyId(chapterDTO.getCourseId());

        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, chapterDTO.getCourseId()).one();
        if (course == null) throw new BusinessException(Code.BUSINESS_ERR, "课程数据异常");
        if (course.getPub() == Pub.APPROVAL) throw new BusinessException(Code.BUSINESS_ERR, "课程审批中，请勿修改");

        Chapter chapter = BeanUtil.copyProperties(chapterDTO, Chapter.class);
        return save(chapter);
    }

    @Override
    public boolean updateChapter(ChapterDTO chapterDTO) {

        checkApproval(chapterDTO.getId());
        Chapter chapter = BeanUtil.copyProperties(chapterDTO, Chapter.class);
        return updateById(chapter);
    }

    @Override
    public boolean deleteChapter(Long id) {

        checkApproval(id);

        List<Node> nodeList = Db.lambdaQuery(Node.class).eq(Node::getChapterId, id).list();
        if (CollUtil.isNotEmpty(nodeList)) {
            nodeList.forEach(node -> {
                if (!CheckEmptyOrRuleUtil.isEmptyString(node.getContent())) {
                    String replace = node.getContent().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
                    try {
                        AliOssUtil.deleteFile(replace);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                Db.removeById(node.getId(), Node.class);
            });
        }

        return removeById(id);
    }

    private List<ChapterVO> convertor(List<Chapter> chapters) {

        if (chapters.isEmpty()) return Collections.emptyList();

        // 收集所有章节 id
        List<Long> ids = chapters.stream().map(Chapter::getId).toList();

        // 获取所有章节对应的节点
        List<Node> nodes = Db.lambdaQuery(Node.class).in(Node::getChapterId, ids).list();

        Map<Long, List<NodeVO>> nodeVOSMap = new HashMap<>(0);

        // 健壮性判断
        if (CollUtil.isNotEmpty(nodes)) {
            // 节点类型处理
            List<NodeVO> nodeVOS = BeanUtil.copyToList(nodes, NodeVO.class);
            // 通过章节 id 分组
            nodeVOSMap = nodeVOS.stream().collect(Collectors.groupingBy(NodeVO::getChapterId));
        }

        // 章节类型转换
        List<ChapterVO> chapterVOS = new ArrayList<>(chapters.size());

        for (Chapter chapter : chapters) {
            ChapterVO vo = BeanUtil.copyProperties(chapter, ChapterVO.class);
            if (nodeVOSMap.get(vo.getId()) == null) {
                vo.setNodeVOS(Collections.emptyList());
            } else {
                vo.setNodeVOS(nodeVOSMap.get(vo.getId()));
            }
            chapterVOS.add(vo);
        }
        return chapterVOS;
    }

    private void checkApproval(Long chapterId) {

        CheckEmptyOrRuleUtil.isTeacherOrManager();
        CheckEmptyOrRuleUtil.isEmptyId(chapterId);

        Chapter chapter = getById(chapterId);
        if (chapter == null) throw new BusinessException(Code.BUSINESS_ERR, "章节数据异常");

        Course course = Db.lambdaQuery(Course.class).eq(Course::getId, chapter.getCourseId()).one();
        if (course == null) throw new BusinessException(Code.BUSINESS_ERR, "课程数据异常");
        if (course.getPub() == Pub.APPROVAL) throw new BusinessException(Code.BUSINESS_ERR, "课程审批中，请勿修改");
    }
}
