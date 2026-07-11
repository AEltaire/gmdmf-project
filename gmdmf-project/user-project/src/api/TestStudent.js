import request from "@/utils/request";

export const saveTestStudentService = (testStudentDTO) => {
  return request.post("/testStudent", testStudentDTO);
};

export const approvalTestStudentService = (testStudentDTO) => {
  return request.patch("/testStudent", testStudentDTO);
};

export const findByStudentIdTestStudentService = () => {
  return request.get(`/testStudent/student`);
};

export const findByPaperIdTestStudentService = (paperId) => {
  return request.get(`/testStudent/paper/${paperId}`);
};

export const findByCourseIdTestStudentService = (courseId) => {
  return request.get(`/testStudent/course/${courseId}`);
};

export const findByPaperIdAndStudentIdTestStudentService = (
  paperId,
  studentId
) => {
  return request.get(`/testStudent`, {
    params: {
      paperId,
      studentId,
    },
  });
};
