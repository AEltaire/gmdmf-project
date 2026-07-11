<template>
  <div style="display: flex">
    <VaButton
      preset="primary"
      icon-right="edit_note"
      size="small"
      class="btn"
      round
      @click="showModal = !showModal"
    >
    </VaButton>
  </div>
  <VaModal v-model="showModal" ok-text="好的">
    <h6 class="va-h6">课程信息</h6>
    <VaForm ref="formRef" class="flex flex-col items-baseline gap-6">
      <VaInput
        v-model="courseDTO.name"
        :rules="[
          (value) => (value && value.length > 0) || 'First name is required',
        ]"
        label="firstName"
      />

      <VaInput
        v-model="courseDTO.majorName"
        :rules="[
          (value) => (value && value.length > 0) || 'Last name is required',
        ]"
        label="Last Name"
      />

      <VaDateInput
        v-model="courseDTO.teacherName"
        :rules="[(v) => validateBirthday(v)]"
        label="Birth Date"
        manual-input
        clearable
      />

      <VaTimeInput
        v-model="courseDTO.startTime"
        :rules="[(v) => v || 'We need to now pick-up time!']"
        label="Pick-up time"
        manual-input
        clearable
      />

      <VaCounter
        v-model="courseDTO.point"
        label="Amount"
        :rules="[
          (v) => v || 'Field is required',
          (v) => v < 10 || 'You can not buy less than 10 items',
        ]"
        manual-input
      />

      <VaSelect
        v-model="courseDTO.point"
        :options="countries"
        :rules="[
          (v) => v || 'Field is required',
          (v) =>
            v.value === 'ua'
              ? 'Delivery currently unavailable in your country'
              : undefined,
        ]"
        label="Country"
      />

      <VaSwitch
        v-model="courseDTO.point"
        label="Notifications"
        size="small"
        :rules="[(v) => v || 'You must agree on notifications']"
      />

      <VaButton :disabled="!isValid" @click="validate() && submit()">
        Submit
      </VaButton>
    </VaForm>
  </VaModal>
</template>

<script lang="ts">
import { onMounted, ref } from "vue";
import { useForm } from "vuestic-ui";
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
