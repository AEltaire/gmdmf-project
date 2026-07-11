import request from "@/utils/request";

export const savePaperService = (paperDTO) => {
  return request.post("/paper", paperDTO);
};

export const deletePaperService = (id) => {
  return request.delete(`/paper/${id}`);
};

export const updatePaperService = (paperDTO) => {
  return request.put("/paper", paperDTO);
};

export const findByIdPaperService = (id) => {
  return request.get(`/paper/${id}`);
};

export const findByPaperQueryService = (paperQuery) => {
  return request.post("/paper/paperQuery", paperQuery);
};
