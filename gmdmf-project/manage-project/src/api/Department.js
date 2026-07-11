import request from "@/utils/request";

export const saveDepartmentService = (departmentDTO) => {
  return request.post(`/department/admin`, departmentDTO);
};

export const updateDepartmentService = (departmentDTOS) => {
  return request.put(`/department/admin`, departmentDTOS);
};

export const deleteDepartmentService = (ids) => {
  return request.post(`/department/admin/deleteList`, ids);
};

export const adminFindAllDepartmentService = () => {
  return request.get(`/department/admin`);
};

export const findBySchoolNameDepartmentService = (schoolName) => {
  return request.get(`/department/schoolName`, {
    params: { schoolName }
  });
};
