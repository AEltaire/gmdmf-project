import request from "@/utils/request";

import { useTokenStore } from "@/stores/token";
const token = localStorage.getItem("token");

export const uploadImgService = (file) => {
  return request.post("/uploadImg", file, {
    headers: {
      Authorization: token
    },
  });
};

export const uploadFileService = (resourceDTO) => {
  return request.post("/uploadFile", resourceDTO, {
    headers: {
      Authorization: token
    },
  });
};

export const uploadMP4Service = (nodeDTO) => {
  return request.post("/uploadMP4", nodeDTO, {
    headers: {
      Authorization: token
    },
  });
};
