package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.NoteDTO;
import net.zjitc.entity.Course;
import net.zjitc.entity.Note;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.NoteMapper;
import net.zjitc.service.NoteService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.NoteVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Override
    public boolean saveNote(NoteDTO noteDTO) {

        CheckEmptyOrRuleUtil.isStudent();
        Note note = BeanUtil.copyProperties(noteDTO, Note.class);
        note.setStudentId(ThreadLocalUtil.getId()).setCreateTime(LocalDateTime.now());
        return save(note);
    }

    @Override
    public boolean updateNote(NoteDTO noteDTO) {

        CheckEmptyOrRuleUtil.isStudent();
        Note note = BeanUtil.copyProperties(noteDTO, Note.class);
        note.setStudentId(ThreadLocalUtil.getId());
        return updateById(note);
    }

    @Override
    public NoteVO findById(Long id) {

        CheckEmptyOrRuleUtil.isEmptyId(id);
        return convertor(getById(id));
    }

    @Override
    public List<NoteVO> findAll() {

        List<Note> list = lambdaQuery()
                .orderByDesc(Note::getCreateTime).list();
        if(CheckEmptyOrRuleUtil.isEmptyList(list)) return Collections.emptyList();
        return list.stream().map(this::convertor).toList();
    }

    @Override
    public List<NoteVO> findByCourseId(Long courseId) {

        List<Note> list = lambdaQuery()
                .eq(Note::getCourseId, courseId)
                .eq(Note::getStudentId, ThreadLocalUtil.getId())
                .orderByDesc(Note::getCreateTime).list();
        if(CheckEmptyOrRuleUtil.isEmptyList(list)) return Collections.emptyList();
        return BeanUtil.copyToList(list, NoteVO.class);
    }

    @Override
    public List<NoteVO> findByStudentId() {
        List<Note> list = lambdaQuery()
                .eq(Note::getStudentId, ThreadLocalUtil.getId())
                .orderByDesc(Note::getCreateTime).list();
        if(CheckEmptyOrRuleUtil.isEmptyList(list)) return Collections.emptyList();
        return BeanUtil.copyToList(list, NoteVO.class);
    }

    private NoteVO convertor(Note note) {

        CheckEmptyOrRuleUtil.isEmptyId(note.getCourseId());
        NoteVO noteVO = BeanUtil.copyProperties(note, NoteVO.class);
        Course course = Db.lambdaQuery(Course.class)
                .eq(Course::getId, note.getCourseId())
                .select(Course::getName)
                .one();
        return noteVO.setCourseName(course.getName());
    }
}
