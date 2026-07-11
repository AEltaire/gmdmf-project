import request from "@/utils/request";

import { useTokenStore } from "@/stores/token";

export const uploadImgService = (file) => {
  return request.post("/uploadImg", file, {
    headers: {
      Authorization: useTokenStore().token,
    },
  });
};

export const uploadFileService = (resourceDTO) => {
  return request.post("/uploadFile", resourceDTO, {
    headers: {
      Authorization: useTokenStore().token,
    },
  });
};

export const uploadMP4Service = (nodeDTO) => {
  return request.post("/uploadMP4", nodeDTO, {
    headers: {
      Authorization: useTokenStore().token,
    },
  });
};
