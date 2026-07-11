import request from "@/utils/request";

export const findAllSchoolService = () => {
  return request.get("/school");
};
