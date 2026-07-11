package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.NoteDTO;
import net.zjitc.entity.Note;
import net.zjitc.vo.NoteVO;

import java.util.List;

public interface NoteService extends IService<Note> {

    boolean saveNote(NoteDTO noteDTO);

    boolean updateNote(NoteDTO noteDTO);

    NoteVO findById(Long id);

    List<NoteVO> findAll();

    List<NoteVO> findByCourseId(Long courseId);

    List<NoteVO> findByStudentId();
}
