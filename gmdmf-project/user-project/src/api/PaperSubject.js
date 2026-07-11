import request from "@/utils/request";

export const savePaperSubjectService = (paperSubjectDTOList) => {
  return request.post("/paperSubject", paperSubjectDTOList);
};

export const deletePaperSubjectService = (paperId) => {
  return request.delete(`/paperSubject/${paperId}`);
};

export const updatePaperSubjectService = (paperId, paperSubjectDTOList) => {
  return request.put(`/paperSubject/${paperId}`, paperSubjectDTOList);
};

export const findByPaperIdPaperSubjectService = (paperApprovalId) => {
  return request.get(`/paperSubject/${paperApprovalId}`);
};

export const findByPaperIdAndStudentIdPaperSubjectService = (paperId, studentId) => {
  return request.get(`/paperSubject/`, { params: { paperId, studentId } });
};