import request from "@/utils/request";

export const saveTeamService = (teamDTO) => {
  return request.post("/team/save", teamDTO);
}

export const updateListTeamService = (teamDTOS) => {
  return request.put("/team/updateList", teamDTOS);
}

export const deleteListTeamService = (teamIds) => {
  return request.post("/team/deleteList", teamIds);
}

export const findAllTeamService = () => {
  return request.get("/team/findAll");
}

export const findByTeamQueryService = (schoolName, departmentName, majorName) => {
  return request.get("/team/findByTeamQuery", {
    params: {
      schoolName,
      departmentName,
      majorName
    }
  });
};