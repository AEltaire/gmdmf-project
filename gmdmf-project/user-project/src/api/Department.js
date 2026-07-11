import request from "@/utils/request";

export const findAllBySchoolIdDepartmentService = (schoolId) => {
  return request.get(`/department/schoolId`, { params: { schoolId } });
};
