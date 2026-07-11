import request from "@/utils/request";

export const findByCourseIdResourceService = (id) => {
  return request.get(`/resource/${id}`);
};
