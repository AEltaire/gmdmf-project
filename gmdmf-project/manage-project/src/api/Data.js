import request from "@/utils/request";

export const getCountDataService = () => {
  return request.get(`/data/count`);
};

export const getSchoolDataService = () => {
  return request.get(`/data/school`);
};

