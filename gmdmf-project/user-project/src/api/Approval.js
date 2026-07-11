import request from "@/utils/request";

/* 
  ApprovalDTO

    private Long id;
    private Long courseId;

*/

export const saveApprovalService = (approvalDTO) => {
  return request.post(`/approval`, approvalDTO);
};

export const updateApprovalService = (approvalDTO) => {
  return request.put(`/approval`, approvalDTO);
};

export const findAllApprovalService = () => {
  return request.get(`/approval`);
};
