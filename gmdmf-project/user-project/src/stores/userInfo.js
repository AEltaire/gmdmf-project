import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserInfoStore = defineStore(
  "userInfo",
  () => {
    const userInfo = ref({
      id: 0,
      name: "",
      sex: "",
      birth: "",
      address: "",
      phone: "",
      email: "",
      school: "",
      department: "",
      major: "",
      teamName: "",
      number: "",
      username: "",
      picture: "",
      introduce: "",
      rule: ""
    });

    const setUserInfo = (newUserInfo) => {
      userInfo.value = newUserInfo;
    };

    const removeUserInfo = () => {
      userInfo.value = {
        id: 0,
        name: "",
        sex: "",
        birth: "",
        address: "",
        phone: "",
        email: "",
        school: "",
        department: "",
        major: "",
        teamName: "",
        number: "",
        username: "",
        picture: "",
        introduce: "",
        rule: ""
      };
    };

    return { userInfo, setUserInfo, removeUserInfo }
  },
  { persist: true }
);