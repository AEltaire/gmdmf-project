import request from "@/utils/request";

// 添加节点
export const saveNodeService = (nodeDTO) => {
  return request.post(`/node`, nodeDTO);
};

// 删除节点
export const deleteNodeByIdService = (id) => {
  return request.delete(`/node/${id}`);
}

// 修改节点
export const updateNodeByIdService = (nodeDTO) => {
  return request.put(`/node`, nodeDTO);
};

export const findAllNodeService = () => {
  return request.get("/node/all");
}

export const findByChapterIdNodeService = (chapterId) => {
  return request.get(`/node/${chapterId}`);
};