// 导入 createRouter 和 createWebHistory 函数
import { createRouter, createWebHistory } from "vue-router";

// 定义路由关系
const routes = [
  {
    path: "/",
    component: () => import("@/views/Manage.vue"),
    redirect: "/data",
    children: [
      {
        path: "/data",
        component: () => import("@/views/Data.vue")
      },
      {
        path: "/course",
        component: () => import("@/views/Course.vue")
      },
      {
        path: "/register",
        component: () => import("@/views/StudentRegister.vue")
      },
      {
        path: "/student",
        component: () => import("@/views/StudentManage.vue")
      },
      {
        path: "/teacher",
        component: () => import("@/views/TeacherManage.vue")
      },
      {
        path: "/approval",
        component: () => import("@/views/Approval.vue")
      },
      {
        path: "/setting",
        component: () => import("@/views/Setting.vue")
      },
      {
        path: "/opinion",
        component: () => import("@/views/Opinion.vue")
      },
      {
        path: "/school",
        component: () => import("@/views/School.vue")
      },
      {
        path: "/major",
        component: () => import("@/views/Major.vue")
      },

    ]
  },
  {
    path: "/login",
    component: () => import("@/views/login/Login.vue")
  },
  {
    path: "/video",
    component: () => import("@/views/Video.vue")
  }
];

// 创建路由器实例
const router = createRouter({
  history: createWebHistory(),
  routes: routes,
});

// 导出路由器实例
export default router;