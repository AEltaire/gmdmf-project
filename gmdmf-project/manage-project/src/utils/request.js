import axios from "axios";
import { ElMessage } from "element-plus";

const baseURL = "/api";
const instance = axios.create({ baseURL });
axios.defaults.withCredentials = true;

import router from "@/router";
import { useTokenStore } from "@/stores/token";
import { useUserInfoStore } from "@/stores/userInfo";

instance.interceptors.request.use(
  (config) => {
    if (useTokenStore().token)
      config.headers.Authorization = useTokenStore().token;
    return config;
  },
  (err) => {
    return Promise.reject(err);
  }
);

instance.interceptors.response.use(
  (response) => {
    if (
      response.data.code === 20011 ||
      response.data.code === 20021 ||
      response.data.code === 20031 ||
      response.data.code === 20041
    ) {
      return response;
    } else {
      ElMessage.error(
        response.data.message ? response.data.message : "服务异常"
      );
      return Promise.reject(response.data);
    }
  },
  (err) => {
    if (err.response.status === 401) {
      router.push("/login");
      useTokenStore().token && useTokenStore().removeToken();
      useUserInfoStore().removeUserInfo();
      ElMessage.error("请先登录");
    } else {
      ElMessage.error("服务异常");
    }
    return Promise.reject(err);
  }
);

export default instance;
