import request from "@/utils/request";

// 添加笔记
export const saveNoteService = (noteDTO) => {
  return request.post("/note", noteDTO);
};

// 删除笔记
export const deleteByIdNoteService = (id) => {
  return request.delete(`/note/${id}`);
};

// 回显数据
export const findByIdNoteService = (id) => {
  return request.get(`/note/${id}`);
};

// 修改数据
export const updateByIdNoteService = (noteDTO) => {
  return request.put('/note', noteDTO);
};

// 根据课程 id 查询
export const findByCourseIdNoteService = (courseId) => {
  return request.get(`/note/courseId/${courseId}`);
};

// 查询所有
export const findByStudentIdNoteService = () => {
  return request.get(`/note`);
};