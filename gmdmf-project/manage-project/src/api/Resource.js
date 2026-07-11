import request from "@/utils/request";

export const findAllResourceService = (id) => {
  return request.get(`/resource/${id}`);
};
