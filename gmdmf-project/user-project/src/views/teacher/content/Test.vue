<script lang="ts" setup>
import { ref, onMounted, onBeforeMount } from "vue";
import { findByTeacherIdPaperApprovalService } from "@/api/PaperApproval";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";

import ApprovalButton from "@/components/ApprovalButton.vue";

const empty = ref(true);

const findByTeacherIdPaperApproval = async () => {
  const result = await findByTeacherIdPaperApprovalService("考试");
  result.data.message == "查询记录为空"
    ? (empty.value = true)
    : (empty.value = false);
  rowData.value = result.data.data;
};

const columnDefs = ref([
  { field: "id", hide: true },
  { field: "studentId", hide: true },
  { field: "studentName", headerName: "学生" },
  { field: "paperName", headerName: "试卷" },
  { field: "teamName", headerName: "班级" },
  { field: "score", headerName: "总分" },
  { field: "point", headerName: "得分" },
  { field: "createTime", headerName: "提交时间" },
  {
    field: "setting",
    headerName: "操作",
    cellRenderer: ApprovalButton,
    sortable: false,
    filter: false,
    maxWidth: 100,
  },
]);

const rowData = ref([]);

const defaultColDef = {
  flex: 1,
  sortable: true,
  filter: true,
};

const gridApi = ref(null);
const columnApi = ref(null);

const onGridReady = (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

onMounted(async () => {
  await findByTeacherIdPaperApproval();
});
</script>

<template>
  <ag-grid-vue
    :rowData="rowData"
    :columnDefs="columnDefs"
    style="height: 82vh"
    class="ag-theme-alpine"
    :defaultColDef="defaultColDef"
    rowSelection="multiple"
    animateRows="true"
    @grid-ready="onGridReady"
    overlayNoRowsTemplate="暂无要批改的试卷"
  >
  </ag-grid-vue>
</template>

<style lang="scss" scoped>
.ag-theme-alpine {
  --ag-row-hover-color: rgb(216, 226, 255);
  --ag-row-border--radius: 10px;
  --ag-border-radius: 10px;
}
</style>
