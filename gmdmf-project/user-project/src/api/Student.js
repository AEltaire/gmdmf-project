import request from "@/utils/request";

export const loginStudentService = (loginDTO) => {
  return request.post("/student/login", loginDTO);
};

export const updatePasswordStudentService = (loginDTO) => {
  return request.patch("/student/password", loginDTO);
};

export const updatePictureStudentService = (picture) => {
  return request.patch(`/student/picture?picture=${picture}`)
};

export const updateStudentService = (studentDTO) => {
  return request.put("/student", studentDTO);
};

export const getStudentInfoService = () => {
  return request.get("/student");
};

export const findByTeamIdStudentService = (teamId) => {
  return request.get(`/student/teamId/${teamId}`);
}