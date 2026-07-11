import request from "@/utils/request";

/* 
  TeacherDTO

    private Long id;
    private String name;
    private String sex;
    private String phone;
    private String email;
    private String address;
    private String school;
    private String department;
    private String idCard;
    private String username;
    private String password;
    private String picture;
    private String introduce;

*/

export const registerTeacherService = (teacherDTO) => {
  return request.post("/teacher/register", teacherDTO);
};

export const updateTeacherListService = (teacherDTOS) => {
  return request.put("/teacher/updateList", teacherDTOS);
};

export const deleteTeacherListService = (ids) => {
  return request.post(`/teacher/deleteList`, ids);
};

export const findAllTeacherService = () => {
  return request.get("/teacher/findAll");
};
