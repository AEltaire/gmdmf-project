//定制请求的实例
import axios from "axios";
import { ElMessage } from "element-plus";

// 配置代理，修改基地址
const baseURL = "/api";
const instance = axios.create({ baseURL });
axios.defaults.withCredentials = true;

// 导入 router，因为模块加载顺序，这里不能使用 useRouter 来获取
import router from "@/router";
import { useUserInfoStore } from "@/stores/userInfo";
// 10.请求拦截器
instance.interceptors.request.use(
  // 请求前的回调
  (config) => {
    // 判断 token 并设置请求头
    if (localStorage.getItem("token"))
      config.headers.Authorization = localStorage.getItem("token");
    // 返回设置好的配置对象
    return config;
  },
  // 请求错误的回调
  (err) => {
    return Promise.reject(err);
  }
);

// 优化响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 判断响应状态码
    if (
      response.data.code === 20011 ||
      response.data.code === 20021 ||
      response.data.code === 20031 ||
      response.data.code === 20041
    ) {
      return response;
    } else {
      // 提示信息
      ElMessage.error(
        response.data.message ? response.data.message : "服务异常"
      );
      // 将异步操作状态转为失败
      return Promise.reject(response.data);
    }
  },
  (err) => {
    // 判断响应状态码为 401 则说明未登录，跳转登录页面
    if (err.response?.status === 401) {
      router.push("/login");
      // 如果 redis 中没有 token，则删除本地存储中的 token，这样不仅可以长期存储 token，并且在 redis 中的 token 到期之后这边也无法登录
      localStorage.getItem("token") && localStorage.removeItem("token");
      // 删除用户信息
      useUserInfoStore().removeUserInfo();
      ElMessage.error("请先登录");
    } else {
      ElMessage.error("服务异常");
    }

    return Promise.reject(err);
  }
);

export default instance;
