import { defineStore } from "pinia";
import { ref } from "vue";

export const useCourseStore = defineStore(
  "course",
  () => {
    const course = ref({
      id: null,
    });

    const setCourse = (newCourse) => {
      course.value = newCourse;
    };

    const removeCourse = () => {
      course.value = {};
    };

    return { course, setCourse, removeCourse };
  },
  { persist: true }
);
