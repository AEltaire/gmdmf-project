import request from "@/utils/request";

export const saveTestService = (testDTOList) => {
  return request.post("/test", testDTOList);
};

export const deleteByIdTestService = (id) => {
  return request.delete(`/test/${id}`);
};

export const updateTestService = (paperId, testDTOList) => {
  return request.put(`/test/${paperId}`, testDTOList);
};

export const findAllTestService = (paperId) => {
  return request.get(`/test/${paperId}`);
};
