import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserInfoStore = defineStore(
  "userInfo",
  () => {
    const userInfo = ref({
      id: null,
      name: "",
      username: "",
      picture: "",
      rule: ""
    });

    const setUserInfo = (newUserInfo) => {
      userInfo.value = newUserInfo;
    };

    const removeUserInfo = () => {
      userInfo.value = {
        id: null,
        name: "",
        username: "",
        picture: "",
        rule: ""
      };
    };

    return { userInfo, setUserInfo, removeUserInfo }
  },
  { persist: true }
);