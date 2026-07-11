import request from "@/utils/request";

export const getChatService = (input) => {
  return request.get("/chat", { params: { input } })
}