<template>
  <div style=" margin-top: 10px">
    <VaButton
      preset="primary"
      icon-right="search"
      size="small"
      class="btn"
      round
      @click="showModal = !showModal"
      style="width: 50px; height: 25px; margin: auto"
    >
    </VaButton>
    <!-- <el-button
      type="primary"
      :icon="Search"
      @click="showModal = !showModal"
    ></el-button> -->
  </div>
  <VaModal v-model="showModal" ok-text="好的" cancel-text="">
    <h6 class="va-h6">成绩分析</h6>
  </VaModal>
</template>

<script lang="ts">
import { onMounted, ref } from "vue";
import { useForm } from "vuestic-ui";
import { Search } from "@element-plus/icons-vue";

export default {
  setup(props) {
    const showModal = ref(false);

    const { isValid, validate, reset, resetValidation } = useForm("formRef");

    interface Course {
      id: number;
      name: string;
      majorName: string;
      teacherName: string;
      point: number;
      introduce: string;
      startTime: Date;
      endTime: Date;
    }

    const courseDTO = ref<Course>({
      id: 0,
      name: "",
      majorName: "",
      teacherName: "",
      point: 0,
      introduce: "",
      startTime: new Date(),
      endTime: new Date(),
    });

    const countries = [
      { value: "ua", text: "Ukraine" },
      { value: "us", text: "USA" },
      { value: "uk", text: "United Kingdom" },
    ];

    const validateBirthday = (value: Date | null) => {
      if (!value) {
        return "Field is required";
      }

      const today = new Date();
      let yearDiff = today.getFullYear() - value.getFullYear();
      const monthDiff = today.getMonth() - value.getMonth();

      if (
        monthDiff < 0 ||
        (monthDiff === 0 && today.getDate() < value.getDate())
      ) {
        yearDiff--;
      }

      return yearDiff >= 18 || "You must be at least 18 years old";
    };

    const submit = () => alert("Form submitted!");
    return {
      showModal,
      submit,
      courseDTO,
      countries,
      validateBirthday,
      isValid,
      validate,
      reset,
      resetValidation,
      Search,
    };
  },
};
</script>

<style>
.btn {
  margin: 8px auto;
  width: 24px;
}
</style>
