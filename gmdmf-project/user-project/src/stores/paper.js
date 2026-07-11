import { ref } from "vue";
import { defineStore } from "pinia";

export const usePaperStore = defineStore(
  "paper",
  () => {
    // 定义 token
    const paper = ref(null);
    // 提供修改 token 的方法
    const setPaper = (newPaper) => {
      paper.value = newPaper;
    };
    // 提供删除 token 的方法
    const removePaper = () => {
      paper.value = "";
    };
    // 返回 token 以及操作方法
    return { paper, setPaper, removePaper };
  },
  {
    // 配置 Pinia 持久化插件 persist，开启该参数的持久化操作
    persist: true,
  }
);
