import request from "@/utils/request";

export const registerStudentService = (studentDTO) => {
  return request.post("/student/register", studentDTO);
};

export const deleteStudentListService = (ids) => {
  return request.post(`/student/deleteList`, ids);
};

export const updateStudentListService = (studentDTOS) => {
  return request.put("/student/updateList", studentDTOS);
};

export const findAllStudentService = () => {
  return request.get("/student/findAll");
};
