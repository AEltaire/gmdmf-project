import { ref } from "vue";
import { defineStore } from "pinia";

export const usePaperApprovalStore = defineStore(
  "paperApproval",
  () => {
    // 定义 token
    const paperApproval = ref(null);
    // 提供修改 token 的方法
    const setPaperApproval = (newPaperApproval) => {
      paperApproval.value = newPaperApproval;
    };
    // 提供删除 token 的方法
    const removePaperApproval = () => {
      paperApproval.value = "";
    };
    // 返回 token 以及操作方法
    return { paperApproval, setPaperApproval, removePaperApproval };
  },
  {
    // 配置 Pinia 持久化插件 persist，开启该参数的持久化操作
    persist: true,
  }
);
