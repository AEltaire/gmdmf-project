import { createRouter, createWebHistory } from "vue-router";

import IndexVue from "@/views/Index.vue";
import LoginVue from "@/views/login/Login.vue";

import StudentVue from "@/views/student/Student.vue";
import StudentCourseNav from "@/views/student/content/Course.vue";
import StudentAnnouncementNav from "@/views/student/content/Announcement.vue";
import StudentWorkNav from "@/views/student/content/Work.vue";
import StudentTestNav from "@/views/student/content/Test.vue";
import StudentNoteNav from "@/views/student/content/Note.vue";

import TeacherVue from "@/views/teacher/Teacher.vue";
import TeacherCourseVue from "@/views/teacher/content/Course.vue";
import TeacherWarehouseVue from "@/views/teacher/content/Warehouse.vue";
import TeacherWorkVue from "@/views/teacher/content/Work.vue";
import TeacherTestVue from "@/views/teacher/content/Test.vue";
import TeacherTeamVue from "@/views/teacher/content/Team.vue";

const routes = [
  {
    path: "/",
    component: IndexVue
  },
  {
    path: "/login",
    component: LoginVue
  },
  {
    path: "/register",
    component: () => import("@/views/login/Register.vue")
  },
  {
    path: "/student",
    redirect: "/sc",
    component: StudentVue,
    children: [
      {
        path: "/sc",
        component: StudentCourseNav
      },
      {
        path: "/sa",
        component: StudentAnnouncementNav
      },
      {
        path: "/sw",
        component: StudentWorkNav
      },
      {
        path: "/st",
        component: StudentTestNav
      },
      {
        path: "/sn",
        component: StudentNoteNav
      },
    ]
  },
  {
    path: "/stu-course",
    component: () => import("@/views/student/course/Course.vue"),
    redirect: "/stu-course/announcement",
    children: [
      {
        path: "/stu-course/announcement",
        component: () => import("@/views/student/course/content/Announcement.vue")
      },
      {
        path: "/stu-course/chapter",
        component: () => import("@/views/student/course/content/Chapter.vue")
      },
      {
        path: "/stu-course/work",
        component: () => import("@/views/student/course/content/Work.vue")
      },
      {
        path: "/stu-course/test",
        component: () => import("@/views/student/course/content/Test.vue")
      },
      {
        path: "/stu-course/resource",
        component: () => import("@/views/student/course/content/Resource.vue")
      },
      {
        path: "/stu-course/note",
        component: () => import("@/views/student/course/content/Note.vue")
      },
      {
        path: "/stu-course/score",
        component: () => import("@/views/student/course/content/Score.vue")
      },
      {
        path: "/stu-course/chat",
        component: () => import("@/views/student/course/content/Chat.vue")
      }
    ]
  },
  {
    path: "/tourist-course",
    component: () => import("@/views/student/tourist/Course.vue"),
    redirect: "/tourist-course/announcement",
    children: [
      {
        path: "/tourist-course/announcement",
        component: () => import("@/views/student/tourist/content/Announcement.vue")
      },
      {
        path: "/tourist-course/chapter",
        component: () => import("@/views/student/tourist/content/Chapter.vue")
      },
      {
        path: "/tourist-course/resource",
        component: () => import("@/views/student/tourist/content/Resource.vue")
      },
      {
        path: "/tourist-course/note",
        component: () => import("@/views/student/tourist/content/Note.vue")
      },
    ]
  },
  {
    path: "/s-paper",
    component: () => import("@/views/student/Paper.vue"),
  },
  {
    path: "/s-chapter",
    component: () => import("@/views/student/course/Chapter.vue"),
  },
  {
    path: "/tourist-chapter",
    component: () => import("@/views/student/tourist/Chapter.vue"),
  },
  {
    path: "/course-paper",
    component: () => import("@/views/student/course/Paper.vue"),
  },
  {
    path: "/teacher",
    redirect: "/tc",
    component: TeacherVue,
    children: [
      {
        path: "/tc",
        component: TeacherCourseVue
      },
      {
        path: "/th",
        component: TeacherWarehouseVue
      },
      {
        path: "/tw",
        component: TeacherWorkVue
      },
      {
        path: "/te",
        component: TeacherTestVue
      },
      {
        path: "/tt",
        component: TeacherTeamVue
      }
    ]
  },
  {
    path: "/t-course",
    component: () => import("@/views/teacher/Course.vue"),
  },
  {
    path: "/t-warehouse",
    component: () => import("@/views/teacher/Warehouse.vue"),
  },
  {
    path: "/t-paper",
    component: () => import("@/views/teacher/Paper.vue"),
  },
  {
    path: "/tea-course",
    component: () => import("@/views/teacher/course/Course.vue"),
    redirect: "/tea-course/chapter",
    children: [
      {
        path: "/tea-course/announcement",
        component: () => import("@/views/teacher/course/content/Announcement.vue")
      },
      {
        path: "/tea-course/chapter",
        component: () => import("@/views/teacher/course/content/Chapter.vue")
      },
      {
        path: "/tea-course/work",
        component: () => import("@/views/teacher/course/content/Work.vue")
      },
      {
        path: "/tea-course/test",
        component: () => import("@/views/teacher/course/content/Test.vue")
      },
      {
        path: "/tea-course/resource",
        component: () => import("@/views/teacher/course/content/Resource.vue")
      },
      {
        path: "/tea-course/score",
        component: () => import("@/views/teacher/course/content/Score.vue")
      },
      {
        path: "/tea-course/paper",
        component: () => import("@/views/teacher/course/content/Paper.vue")
      },
      {
        path: "/tea-course/courseInfo",
        component: () => import("@/views/teacher/course/content/CourseInfo.vue")
      },
      {
        path: "/tea-course/team",
        component: () => import("@/views/teacher/course/content/Team.vue")
      },
    ]
  },
  {
    path: "/tc-paper",
    component: () => import("@/views/teacher/course/Paper.vue"),
  },
  {
    path: "/t-chapter",
    component: () => import("@/views/teacher/course/Chapter.vue"),
  },
];

const router = createRouter({
  mode: "history",
  history: createWebHistory(),
  routes: routes,
});

export default router;
