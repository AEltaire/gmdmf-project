import request from "@/utils/request";

export const saveTouristService = (tourist) => {
  return request.post("/tourist", tourist);
}

export const deleteTouristService = (id) => {
  return request.delete(`/tourist/${id}`);
}

export const findAllTouristService = () => {
  return request.get(`/tourist`);
}

