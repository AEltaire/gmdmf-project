import { ref } from "vue";
import { defineStore } from "pinia";


export const useTokenStore = defineStore(
  "token",
  () => {
    // 定义 token
    const token = ref("");
    // 提供修改 token 的方法
    const setToken = (newToken) => {
      token.value = newToken;
    };
    // 提供删除 token 的方法
    const removeToken = () => {
      token.value = "";
    };
    // 返回 token 以及操作方法
    return { token, setToken, removeToken };
  },
  {
    // 配置 Pinia 持久化插件 persist，开启该参数的持久化操作
    persist: true,
  }
);
