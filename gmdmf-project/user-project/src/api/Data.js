import request from "@/utils/request";

export const getScoreByStudentIdService = (studentId, courseId) => {
  return request.get(`/data/score`, { params: { studentId, courseId } })
}

export const getPaperByStudentIdService = (studentId, courseId) => {
  return request.get(`/data/paper`, { params: { studentId, courseId } })
}

export const getMyScoreService = (studentId, courseId) => {
  return request.get(`/data/ai`, { params: { studentId, courseId } })
}