import request from "@/utils/request";

export const saveOrUpdateSchoolService = (schoolDTO) => {
  return request.post("/school/admin", schoolDTO);
};

export const deleteSchoolService = (id) => {
  return request.delete(`/school/admin/${id}`);
}

export const findAllSchoolService = () => {
  return request.get("/school");
};

export const findAllSchoolAndDepartmentService = () => {
  return request.get("/school/SchoolAndDepartment");
};
