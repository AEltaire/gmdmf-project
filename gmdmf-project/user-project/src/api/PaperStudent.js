import request from "@/utils/request";

export const saveOrUpdatePaperStudentService = (paperStudentDTOS) => {
  return request.post("/paperStudent", paperStudentDTOS);
};

export const updatePaperStudentService = (paperStudentDTOS) => {
  return request.put("/paperStudent", paperStudentDTOS);
};

export const approvalPaperStudentService = (paperStudentDTOS) => {
  return request.put("/paperStudent", paperStudentDTOS);
};

export const findByPaperApprovalIdPaperStudentService = (paperApprovalId) => {
  return request.get(`/paperStudent/${paperApprovalId}`);
};

export const findByStudentIdPaperStudentService = (studentId, courseId) => {
  return request.get(`/paperStudent/studentId`, { params: { studentId, courseId } });
};

export const findByPaperIdAndStudentIdService = (paperId, studentId) => {
  return request.get(`/paperStudent`, {
    params: {
      paperId,
      studentId,
    }
  });
};
