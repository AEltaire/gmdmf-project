import request from "@/utils/request";

export const loginManageService = (username, password) => {
  return request.get(`/manage/login`, {
    params: {
      username,
      password,
    },
  });
};