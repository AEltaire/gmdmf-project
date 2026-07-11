import request from "@/utils/request";

export const savePaperApprovalService = (paperApprovalDTO) => {
  return request.post("/paperApproval/student", paperApprovalDTO);
};

export const saveStudentPaperApprovalService = (courseId, paperId) => {
  return request.get("/paperApproval/studentPaper", {
    params: { courseId, paperId },
  });
};

export const approvalPaperApprovalService = (paperApprovalId) => {
  return request.put(`/paperApproval/${paperApprovalId}`);
};


export const findByPaperIdPaperApprovalService = (paperId) => {
  return request.get(`/paperApproval/paperId/${paperId}`);
};

export const findByTeacherIdPaperApprovalService = (type) => {
  return request.get(`/paperApproval/teacherId`, { params: { type } });
};

export const findByCourseIdPaperApprovalService = (courseId, type) => {
  return request.get(`/paperApproval/courseId/${courseId}`, {
    params: { type },
  });
};
