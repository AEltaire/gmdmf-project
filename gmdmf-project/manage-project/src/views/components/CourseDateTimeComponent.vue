<template>
  <div v-if="field" class="ag-custom-component-popup">
    <VaDateInput :format-date="formatDate" v-model="startDate" />
    <div style="margin: 10px 0">
      <VaTimeInput v-model="startTime" class="w-28" />
    </div>
    <VaButton
      preset="primary"
      style="width: 100%"
      size="small"
      class="mr-6 mb-2 btn"
      @click="setStartDateTime"
      >确定</VaButton
    >
  </div>
  <div v-else class="ag-custom-component-popup">
    <VaDateInput :format-date="formatDate" v-model="endDate" />
    <div style="margin: 10px 0">
      <VaTimeInput v-model="endTime" class="w-28" />
    </div>
    <VaButton
      preset="primary"
      style="width: 100%"
      size="small"
      class="mr-6 mb-2 btn"
      @click="setEndDateTime"
      >确定</VaButton
    >
  </div>
</template>

<script>
import { onMounted, ref } from "vue";

export default {
  setup(props) {
    let { value } = props.params;
    const field = ref(true);

    onMounted(() => {
      if (props.params.colDef.field == "startTime") {
        field.value = true;
        startDate.value = new Date(props.params.node.data.startTime);
        startTime.value = new Date(props.params.node.data.startTime);
      } else {
        field.value = false;
        endDate.value = new Date(props.params.node.data.endTime);
        endTime.value = new Date(props.params.node.data.endTime);
      }
    });

    const startDate = ref();
    const endDate = ref();

    const startTime = ref();
    const endTime = ref();

    const setStartDateTime = () => {
      const dateTime = startDate.value;
      dateTime.setHours(startTime.value.getHours());
      dateTime.setMinutes(startTime.value.getMinutes());
      dateTime.setSeconds(startTime.value.getSeconds());

      const year = dateTime.getFullYear();
      const month =
        dateTime.getMonth() + 1 < 10
          ? "0" + (dateTime.getMonth() + 1)
          : dateTime.getMonth() + 1;
      const day =
        dateTime.getDate() < 10 ? "0" + dateTime.getDate() : dateTime.getDate();

      props.params.node.data.startTime =
        `${year}/${month}/${day}` + " " + dateTime.toLocaleTimeString();

      props.params.api.stopEditing();
    };

    const setEndDateTime = () => {
      const dateTime = endDate.value;
      dateTime.setHours(endTime.value.getHours());
      dateTime.setMinutes(endTime.value.getMinutes());
      dateTime.setSeconds(endTime.value.getSeconds());

      const year = dateTime.getFullYear();
      const month =
        dateTime.getMonth() + 1 < 10
          ? "0" + (dateTime.getMonth() + 1)
          : dateTime.getMonth() + 1;
      const day =
        dateTime.getDate() < 10 ? "0" + dateTime.getDate() : dateTime.getDate();

      props.params.node.data.endTime =
        `${year}/${month}/${day}` + " " + dateTime.toLocaleTimeString();

      props.params.api.stopEditing();
    };

    const formatDate = (date) => {
      const year = date.getFullYear();
      const month =
        date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1;
      const day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();

      return `${year}/${month}/${day}`;
    };

    return {
      startDate,
      endDate,
      startTime,
      endTime,
      setStartDateTime,
      setEndDateTime,
      formatDate,
      field,
    };
  },
};
</script>

<style>
.ag-custom-component-popup {
  width: 158.5px;
  margin: 8px 5px;
}
</style>
