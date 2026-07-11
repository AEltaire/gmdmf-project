import request from "@/utils/request";

export const sendMessageService = (manageId, content) => {
  return request.get(`/message/send`, {
    params: {
      manageId, content
    }
  });
};

export const findByManageIdMessageService = (id) => {
  return request.get(`/message/${id}`);
};