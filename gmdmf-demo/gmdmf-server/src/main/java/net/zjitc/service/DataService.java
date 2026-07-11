package net.zjitc.service;

public interface DataService {

    Object getCountData();

    Object getSchoolData();

    Object getScoreData(Long studentId, Long courseId);

    Object getPaperData(Long studentId, Long courseId);

    Object getAIData(Long studentId, Long courseId);
}
