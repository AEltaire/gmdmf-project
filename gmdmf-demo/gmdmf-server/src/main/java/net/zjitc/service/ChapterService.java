package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.ChapterDTO;
import net.zjitc.entity.Chapter;
import net.zjitc.vo.ChapterVO;

import java.util.List;

public interface ChapterService extends IService<Chapter> {

    List<ChapterVO> findAll();

    List<ChapterVO> findById(Long courseId);

    List<ChapterVO> findByStudentId();

    boolean saveChapter(ChapterDTO chapterDTO);

    boolean updateChapter(ChapterDTO chapterDTO);

    boolean deleteChapter(Long id);
}
