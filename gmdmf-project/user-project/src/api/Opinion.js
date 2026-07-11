import request from "@/utils/request";

/* 
  opinionDTO

    private String content;
    private String state;

*/

export const saveOpinionService = (opinionDTO) => {
  return request.post("/opinion", opinionDTO);
};