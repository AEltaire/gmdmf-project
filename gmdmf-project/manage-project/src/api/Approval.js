import request from "@/utils/request";

export const updateApprovalService = (approvalDTO) => {
  return request.put(`/approval`, approvalDTO);
};

export const findByPageApprovalService = (currentPage, pageSize) => {
  return request.get(`/approval`, {
    params: {
      currentPage,
      pageSize,
    },
  });
};
