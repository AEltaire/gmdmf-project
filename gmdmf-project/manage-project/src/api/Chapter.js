import request from "@/utils/request";

/* 
  ChapterDTO

    private Long id;
    private String title;
    private Long courseId;
    
*/

export const saveChapterService = (chapterDTO) => {
  return request.post("/chapter", chapterDTO);
}

export const updateChapterService = (chapterDTO) => {
  return request.put("/chapter", chapterDTO);
};

export const deleteChapterService = (id) => {
  return request.delete(`/chapter/${id}`);
}

export const findByCourseIdChapterService = (id) => {
  return request.get(`/chapter/${id}`);
};

export const findAllChapterService = () => {
  return request.get("/chapter/findAll");
}

export const findByStudentIdChapterService = () => {
  return request.get("/chapter/studentId");
};

