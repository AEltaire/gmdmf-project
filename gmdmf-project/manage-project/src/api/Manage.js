import request from "@/utils/request";

export const loginManageService = (username, password) => {
  return request.get(`/manage/login`, {
    params: {
      username,
      password,
    },
  });
};

export const saveOrUpdateManageService = (manageDTO) => {
  return request.post(`/manage`, manageDTO);
};
export const deleteManageService = (id) => {
  return request.delete(`/manage/${id}`);
};

export const getManageInfoService = () => {
  return request.get(`/manage`);
};

export const findAllManageService = () => {
  return request.get(`/manage/all`);
};