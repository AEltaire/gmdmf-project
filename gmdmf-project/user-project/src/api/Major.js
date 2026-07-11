import request from "@/utils/request";

export const findAllMajorService = () => {
  return request.get("/major");
};
