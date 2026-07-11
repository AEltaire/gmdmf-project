import request from "@/utils/request";

/* 
  EvaluateDTO

    private Long id;
    private Long courseId;
    private Float score;

*/

export const saveEvaluateService = (evaluateDTO) => {
  return request.post("/evaluate", evaluateDTO);
}

export const updateEvaluateService = (evaluateDTO) => {
  return request.put("/evaluate", evaluateDTO);
}

export const findBySidAndCidEvaluateService = (id) => {
  return request.get(`/evaluate/${id}`);
}

