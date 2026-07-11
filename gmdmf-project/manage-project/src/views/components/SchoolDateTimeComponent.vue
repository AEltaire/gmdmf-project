<template>
  <div class="ag-custom-component-popup">
    <VaDateInput  v-model="endDate" />
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

    onMounted(() => {
      endDate.value = new Date(props.params.node.data.endTime);
      endTime.value = new Date(props.params.node.data.endTime);
    });

    const endDate = ref();
    const endTime = ref();

    const setEndDateTime = () => {
      props.params.node.data.endTime =
        formatDate(endDate.value) + " " +  endTime.value.toLocaleTimeString();
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
      endDate,
      endTime,
      setEndDateTime,
      formatDate,
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
