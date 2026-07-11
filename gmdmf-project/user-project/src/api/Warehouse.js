import request from "@/utils/request";

export const saveOrUpdateWarehouseService = (warehouseDTO) => {
  return request.post("/warehouse", warehouseDTO);
};

export const deleteWarehouseService = (id) => {
  return request.delete(`/warehouse/${id}`);
};

export const findByTeacherIdWarehouseService = () => {
  return request.get("/warehouse");
};

export const findByPageWarehouseService = (warehouseQuery) => {
  return request.post("/warehouse/page", warehouseQuery);
};