import request from "@/utils/request";

export const findByCourseIdService = (id) => {
  return request.get(`/team/courseId/${id}`);
};

export const findByTeamIdService = (id) => {
  return request.get(`/team/${id}`);
};

export const findByTeacherIdTeamService = () => {
  return request.get(`/team/teacherId`);
};

export const findByTeamQueryTeamService = (teamQuery) => {
  return request.get(`/team/findByTeamQuery`, {
    params: {
      schoolName: teamQuery.schoolName,
      departmentName: teamQuery.departmentName,
      majorName: teamQuery.majorName,
    }
  });
};
