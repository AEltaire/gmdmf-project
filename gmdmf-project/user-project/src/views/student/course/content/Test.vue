<script lang="ts" setup>
import { ref, onMounted, onBeforeMount } from "vue";
import { findByPaperQueryService } from "@/api/Paper";
import { useRouter } from "vue-router";
import { usePaperStore } from "@/stores/paper";
import { useCourseStore } from "@/stores/course";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";

import WorkButton from "@/components/WorkButton.vue";

const router = useRouter();
const paperStore = usePaperStore();
const courseId = useCourseStore().course.id;

const paperQuery = ref({
  id: 0,
  type: "考试",
  courseId: courseId,
  title: "",
  startTime: "",
  notEndTime: true,
});

interface Paper {
  id: number;
  type: string;
  teacher: string;
  courseName: string;
  title: string;
  sum: number;
  point: number;
  startTime: string;
  endTime: string;
  createTime: string;
}

const empty = ref(true);

const findByPage = async () => {
  paperQuery.value.startTime =
    new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString();
  const result = await findByPaperQueryService(paperQuery.value);
  result.data.message == "查询记录为空"
    ? (empty.value = true)
    : (empty.value = false);
  rowData.value = result.data.data;
};

const goPaper = (id) => {
  router.push("/s-paper");
  paperStore.setPaper(id);
};

const columnDefs = ref([
  { field: "id", hide: true },
  { field: "title", headerName: "作业" },
  { field: "teacher", headerName: "教师" },
  { field: "startTime", headerName: "开始时间" },
  { field: "endTime", headerName: "截止时间" },
  { field: "sum", headerName: "总分" },
  { field: "point", headerName: "得分" },
  {
    field: "setting",
    headerName: "操作",
    cellRenderer: WorkButton,
    sortable: false,
    filter: false,
    maxWidth: 100,
  },
]);

const rowData = ref<Paper[]>([]);

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
  await findByPage();
});
</script>

<template>
  <div v-if="empty == true">
    <el-card style="width: 100%; height: 82vh" shadow="never"
      ><el-empty :image-size="400"
    /></el-card>
  </div>
  <ag-grid-vue
    v-else
    :rowData="rowData"
    :columnDefs="columnDefs"
    style="height: 82vh"
    class="ag-theme-alpine"
    :defaultColDef="defaultColDef"
    rowSelection="multiple"
    animateRows="true"
    @grid-ready="onGridReady"
  >
  </ag-grid-vue>
</template>

<style lang="scss" scoped>
.ag-theme-alpine {
  --ag-foreground-color: rgb(14, 68, 145);
  --ag-header-background-color: rgb(228, 237, 250);
  --ag-row-hover-color: rgb(216, 226, 255);
  --ag-row-border--radius: 10px;
  --ag-border-radius: 10px;
}
</style>
