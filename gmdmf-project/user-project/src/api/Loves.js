import request from "@/utils/request";

export const saveLovesService = (loves) => {
  return request.post("/loves", loves);
}

export const deleteLovesService = (id) => {
  return request.delete(`/loves/${id}`);
}

export const findAllLovesService = () => {
  return request.get(`/loves`);
}

