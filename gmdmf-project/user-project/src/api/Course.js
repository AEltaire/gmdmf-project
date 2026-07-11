import request from "@/utils/request";

/* 
  CourseDTO

    private Long id;
    private String name;    
    private Long majorId;  [教师端]
    private String majorName;  [管理端]
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Float point;
    private String introduce;
    private String picture;
    private Pub pub;
    private Boolean exhibition;

*/

export const saveCourseService = (courseDTO) => {
  return request.post("/course", courseDTO);
};

export const updateCourseService = (courseDTO) => {
  return request.put("/course", courseDTO);
};

// 根据 id 查询课程
export const findByIdCourseService = (id) => {
  return request.get(`/course/${id}`);
};

// 查询所有课程
export const findAllCourseService = (courseQuery) => {
  return request.post("/course/findByPage", courseQuery);
};

// 查询所有课程
export const findByCourseService = (courseQuery) => {
  return request.get("/course/findBy", {
    params: {
      courseName: courseQuery.courseName,
      majorName: courseQuery.majorName,
    }
  });
};

// 根据学生 id 查询课程
export const findByStudentIdCourseService = () => {
  return request.get("/course/studentId");
};

// 根据教师 id 查询课程
export const findByTeacherIdCourseService = (courseQuery) => {
  return request.post("/course/teacherId", courseQuery);
};

// 上线审批课程
export const approvalCourseService = (id) => {
  return request.post(`/course/approval/${id}`);
};
