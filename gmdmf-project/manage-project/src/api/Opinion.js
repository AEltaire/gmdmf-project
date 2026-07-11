import request from "@/utils/request";

export const deleteOpinionService = (id) => {
  return request.delete(`/opinion/${id}`);
};

export const findAllOpinionService = () => {
  return request.get("/opinion/findAll");
};