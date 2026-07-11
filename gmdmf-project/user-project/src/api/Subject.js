import request from "@/utils/request";

/* 
  SubjectDTO

    private Long id;
    private Long warehouseId;
    private String type;
    private String label;
    private String question;
    private String options;
    private String answer;
    private String analysis;
    private String level;
    private Pub pub;
    private Float coefficient;
    private Float pass;
    
*/

export const saveSubjectService = (subjectDTO) => {
  return request.post("/subject", subjectDTO);
};

export const deleteSubjectService = (id) => {
  return request.delete(`/subject/${id}`);
};

export const updateSubjectService = (subjectDTO) => {
  return request.put("/subject", subjectDTO);
};

export const findByWarehouseIdSubjectService = (id) => {
  return request.get(`/subject/warehouseId/${id}`);
};
