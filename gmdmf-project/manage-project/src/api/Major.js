import request from "@/utils/request";

export const saveOrUpdateMajorListService = (majorDTO) => {
  return request.post("/major/admin", majorDTO);
};

export const deleteMajorListService = (majorDTO) => {
  return request.post("/major/admin/delete", majorDTO);
};

export const adminFindAllMajorService = () => {
  return request.get("/major/admin");
};
