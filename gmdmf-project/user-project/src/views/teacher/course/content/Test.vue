<script lang="ts" setup>
import { useRouter } from "vue-router";
const router = useRouter();
import { ref, onMounted, onBeforeMount } from "vue";
import { useCourseStore } from "@/stores/course";
import { findByCourseIdPaperApprovalService } from "@/api/PaperApproval";
import { findByCourseIdService } from "@/api/Team";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-material.css";

import WaitSubmit from "@/components/WaitSubmit.vue";
import ApprovalButton from "@/components/ApprovalButton.vue";

const courseId = useCourseStore().course.id;
const loading = ref(true);
const svg = `
      <path class="path" d="
        M 30 15
        L 28 17
        M 25.61 25.61
        A 15 15, 0, 0, 1, 15 30
        A 15 15, 0, 1, 1, 27.99 7.5
        L 15 15
      " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
    `;

onBeforeMount(() => {
  loading.value = true;
  window.screenTop = 0;
  document.body.style.overflow = "hidden";
});

onMounted(async () => {
  await findByCourseId();
  await findByTeacherIdPaperApproval();
  activeName.value = teamList.value[0].id;
  if (paperApprovalAll.value[activeName.value] == undefined) rowData.value = [];
  else rowData.value = paperApprovalAll.value[activeName.value];
});

const teamList = ref([
  {
    id: null,
    name: "",
  },
]);

const paperApprovalAll = ref([]);

const findByCourseId = async () => {
  const result = await findByCourseIdService(courseId);
  teamList.value = result.data.data;
};

const findByTeacherIdPaperApproval = async () => {
  const result = await findByCourseIdPaperApprovalService(courseId, "考试");
  paperApprovalAll.value = result.data.data;
  await setTimeout(() => {
    loading.value = false;
    document.body.style.overflow = "visible";
  }, 300);
};

const handleTabChange = (pane) => {
  activeName.value = pane.props.name;
  if (paperApprovalAll.value[activeName.value] == undefined) rowData.value = [];
  else rowData.value = paperApprovalAll.value[activeName.value];
};

const activeName = ref("");

const columnDefs = ref([
  { field: "id", hide: true },
  { field: "studentId", hide: true },
  { field: "studentName", headerName: "学生" },
  { field: "paperName", headerName: "试卷" },
  { field: "teamName", headerName: "班级" },
  { field: "score", headerName: "总分" },
  { field: "point", headerName: "得分" },
  {
    field: "updateTime",
    headerName: "提交时间",
    cellRenderer: (params) => {
      if (!params.value) return "未提交";
      return params.value;
    },
  },
  {
    field: "setting",
    headerName: "操作",
    sortable: false,
    filter: false,
    maxWidth: 100,
    cellRenderer: ApprovalButton,
    cellRendererSelector: (params) => {
      if (params.data.updateTime == null) {
        return { component: WaitSubmit };
      }
      return { component: ApprovalButton };
    },
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
</script>

<template>
  <div>
    <div
      style="z-index: 99; width: 100%; height: 82vh"
      v-loading="loading"
      :element-loading-svg="svg"
      class="custom-loading-svg"
      element-loading-svg-view-box="-10, -10, 50, 50"
      :v-loading="loading"
      v-if="loading == true"
    ></div>
    <el-tabs
      v-else
      v-model="activeName"
      type="border-card"
      style="height: 82vh"
      @tab-click="handleTabChange"
    >
      <el-tab-pane
        :name="team.id"
        :label="team.name"
        v-for="(team, index) in teamList"
        :key="index"
      >
        <ag-grid-vue
          :rowData="rowData"
          :columnDefs="columnDefs"
          style="height: 82vh"
          class="ag-theme-material"
          :defaultColDef="defaultColDef"
          rowSelection="multiple"
          animateRows="true"
          @grid-ready="onGridReady"
              overlayNoRowsTemplate="暂无试卷"
        >
        </ag-grid-vue>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
.footer {
  // position: relative;
  margin-top: 20px;
  padding: 14px 0;
  width: 99%;
  height: 33px;
  border: 1px solid #dcdfe6;
  background-color: #fff;
  // box-sizing: border-box;
}

.example-pagination-block {
  float: right;
}
.ag-theme-material {
  --ag-header-height: 40px;
}
</style>
