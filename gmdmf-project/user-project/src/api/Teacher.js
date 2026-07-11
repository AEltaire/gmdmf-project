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

export const loginTeacherService = (loginDTO) => {
  return request.post("/teacher/login", loginDTO);
};

export const registerTeacherService = (teacherDTO) => {
  return request.post("/teacher/register", teacherDTO);
};

export const updateTeacherService = (teacherDTO) => {
  return request.put("/teacher", teacherDTO);
};

export const updatePasswordTeacherService = (loginDTO) => {
  return request.patch("/teacher/password", loginDTO);
};

export const updatePictureTeacherService = (picture) => {
  return request.patch(`/teacher/picture?picture=${picture}`)
};

export const getTeacherInfoService = () => {
  return request.get("/teacher");
};
