<script lang="ts" setup>
import { computed, ref, onMounted, onBeforeMount } from "vue";
import { findByTeacherIdTeamService, findByTeamIdService } from "@/api/Team";
import { AgGridVue } from "ag-grid-vue3";
// 视图样式
import "ag-grid-community/styles/ag-grid.css";
// 主题样式，通过 class 类快速使用设计好的样式
import "ag-grid-community/styles/ag-theme-alpine.css";
import "ag-grid-community/styles/ag-theme-material.css";
import "ag-grid-community/styles/ag-theme-balham.css";

import ScoreButton from "@/components/ScoreButton.vue"

interface Student {
  id: number;
  name: string;
  sex: string;
  major: string;
  number: number;
  phone: string;
}

const teamList = ref([
  {
    id: null,
    name: "",
  },
]);

onMounted(async () => {
  await findByTeacherIdTeam();
  if (teamList.value.length > 0) {
    activeName.value = teamList.value[0].id;
    await findByTeamId();
  }
});

const findByTeacherIdTeam = async () => {
  const result = await findByTeacherIdTeamService();
  teamList.value = result.data.data;
};

const findByTeamId = async () => {
  const result = await findByTeamIdService(activeName.value);
  rowData.value = result.data.data;
};

const activeName = ref("");
const handleTabChange = async (pane) => {
  activeName.value = pane.props.name;
  await findByTeamId();
};

// -------------------------------

// 列标题
const columnDefs = ref([
  { field: "number", headerName: "学号" },
  { field: "name", headerName: "姓名" },
  { field: "sex", headerName: "性别" },
  { field: "major", headerName: "专业" },
  { field: "phone", headerName: "电话" },
  { field: "email", headerName: "邮箱" },
    {
    field: "setting",
    headerName: "操作",
    cellRenderer: ScoreButton,
    sortable: false,
    filter: false,
    maxWidth: 100,
  },
]);

// 行数据
const rowData = ref<Student[]>([
  // 通过和 fieId 一一对应，展示数据、类似字典
]);

// 使用默认列设置，来节省 columnDefs 的书写，比如每列都开启排序、过滤功能
const defaultColDef = {
  flex: 1,
  sortable: true,
  filter: true,
};

// 保存网格视图 API
const gridApi = ref(null);
const columnApi = ref(null);

// 初始化完成事件
const onGridReady = (params) => {
  // 将 API 保存，方便后续使用
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};
</script>

<template>
  <el-tabs
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
        style="width: 100%; height: 100vh"
        class="ag-theme-material"
        :defaultColDef="defaultColDef"
        animateRows="true"
        @grid-ready="onGridReady"
      >
      </ag-grid-vue>
    </el-tab-pane>
  </el-tabs>
</template>

<style lang="scss" scoped>
.ag-theme-material {
  --ag-header-height: 40px;
}
</style>
