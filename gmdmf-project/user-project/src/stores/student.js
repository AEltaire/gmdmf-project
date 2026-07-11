import { defineStore } from "pinia";
import { ref } from "vue";

export const useStudentIdStore = defineStore(
  "studentId",
  () => {
    const studentId = ref(null);

    const setStudentId = (newUserInfo) => {
      studentId.value = newUserInfo;
    };

    const removeStudentId = () => {
      studentId.value = null
    };

    return { studentId, setStudentId, removeStudentId };
  },
  { persist: true }
);
