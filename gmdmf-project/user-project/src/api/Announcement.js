import request from "@/utils/request";

/* 
  announcementDTO

    private Long id;
    private Long courseId;
    private String title;
    private String content;
    
*/

export const saveAnnouncementService = (announcementDTO) => {
  return request.post("/announcement", announcementDTO);
};

export const updateAnnouncementService = (announcementDTO) => {
  return request.put("/announcement", announcementDTO);
};

export const deleteAnnouncementService = (id) => {
  return request.delete(`/announcement/${id}`);
};

export const findByIdAnnouncementService = (id) => {
  return request.get(`/announcement/${id}`);
};

export const findByCourseIdAnnouncementService = (courseId) => {
  return request.get(`/announcement/courseId/${courseId}`);
};

export const findByStudentIdAnnouncementService = () => {
  return request.get(`/announcement/studentId`);
};