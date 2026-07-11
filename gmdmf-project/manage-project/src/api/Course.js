import request from "@/utils/request";

export const saveCourseService = (courseDTO) => {
  return request.post("/course", courseDTO);
};

export const deleteCourseListService = (ids) => {
  return request.post("/course/deleteList", ids);
};

export const updateListCourseService = (courseDTO) => {
  return request.put("/course/updateList", courseDTO);
};

// 查询所有课程
export const findAllCourseService = () => {
  return request.get("/course/admin");
};

// 查询所有审批课程
export const findAllApprovalCourseService = () => {
  return request.get("/course/approval");
};
