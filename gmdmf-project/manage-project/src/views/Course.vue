<script setup lang="ts">
import {
  ref,
  watchEffect,
  onMounted,
  computed,
  defineComponent,
  reactive,
  Component,
} from "vue";
import { useForm } from "vuestic-ui";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import $ from "jquery";
import { useModal } from "vuestic-ui";
import {
  findAllCourseService,
  updateListCourseService,
  deleteCourseListService,
} from "@/api/Course";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import "ag-grid-enterprise";

import CourseDateTimeComponent from "./components/CourseDateTimeComponent.vue";

import { adminFindAllMajorService } from "@/api/Major";

const store = useStore();
const total = ref(10);

interface Course {
  id: number;
  name: string;
  majorName: string;
  teacherName: string;
  point: number;
  introduce: string;
  startTime: Date;
  endTime: Date;
  love: number;
  assess: number;
  number: number;
  pub: string;
  exhibition: boolean;
}

const pagination = true;
const paginationPageSize = 10;
const paginationPageSizeSelector = [10, 20, 50, 100];

const gridApi = ref(null);
const columnApi = ref(null);

const rowSelection = {
  mode: "multiRow",
};

const autoSizeStrategy = {
  type: "fitCellContents",
};

const rowData = ref<Course[]>([]);

const defaultColDef = {
  flex: 1,
  filter: true,
  editable: true,
  cellRenderer: (params) => {
    if (params.value == null) {
      return "< null >";
    } else {
      return params.value;
    }
  },
};

const columnDefs = ref([
  {
    field: "id",
    headerName: "课程 ID",
    editable: false,
    minWidth: 120,
  },
  {
    field: "name",
    headerName: "课程名",
    cellEditor: "agTextCellEditor",
    cellEditorParams: { maxLength: 30 },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("课程名长度应在2~30之间");
        return false;
      } else {
        params.data.name = params.newValue;
        return true;
      }
    },
  },
  { field: "teacherName", headerName: "教师名", editable: false },
  {
    field: "majorName",
    headerName: "专业",
    editable: false,
  },
  {
    field: "point",
    headerName: "学分",
    cellEditor: "agNumberCellEditor",
    cellEditorParams: {
      min: 1,
      max: 20,
      step: 0.5,
      precision: 1,
    },
    valueSetter: (params) => {
      if (params.newValue < 1 || params.newValue > 20) {
        ElMessage.warning("学分应在1~20之间");
        return false;
      } else {
        params.data.point = params.newValue;
        return true;
      }
    },
  },
  {
    field: "startTime",
    headerName: "开课时间",
    editable: true,
    cellType: "Date",
    cellEditor: CourseDateTimeComponent,
    cellEditorPopup: true,
    cellEditorPopupPosition: "under",
    valueGetter: (params) => {
      return params.data.startTime;
    },
    valueSetter: (params) => {
      return true;
    },
  },
  {
    field: "endTime",
    headerName: "结课时间",
    editable: true,
    cellType: "Date",
    cellEditor: CourseDateTimeComponent,
    cellEditorPopup: true,
    cellEditorPopupPosition: "under",
    valueGetter: (params) => {
      return params.data.endTime;
    },
    valueSetter: (params) => {
      return true;
    },
  },
  {
    field: "introduce",
    headerName: "课程介绍",
    minWidth: 400,
    cellEditor: "agTextCellEditor",
    cellEditorParams: { maxLength: 200 },
    valueSetter: (params) => {
      if (params.newValue.length > 200 || params.newValue.length < 0) {
        ElMessage.warning("课程介绍长度应在200之内");
        return false;
      } else {
        params.data.introduce = params.newValue;
        return true;
      }
    },
  },
  { field: "love", headerName: "喜爱", editable: false },
  { field: "assess", headerName: "评分", editable: false },
  { field: "number", headerName: "人数", editable: false },
  {
    field: "pub",
    headerName: "状态",
    cellEditor: "agSelectCellEditor",
    cellEditorParams: { values: ["私有", "审批", "公开"] },
  },
  { field: "exhibition", headerName: "可见" },
]);

const onGridReady = (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

onMounted(async () => {
  findAllCourse();
  const majorResult = await adminFindAllMajorService();
});

const findAllCourse = async () => {
  const result = await findAllCourseService();
  rowData.value = result.data.data;
  total.value = result.data.data.length;
};

const updateCourse = async () => {
  const selectedRows = gridApi.value.getSelectedRows();
  if (selectedRows.length === 0) {
    ElMessage.warning("请选择要修改的课程");
    return;
  }
  const result = await updateListCourseService(selectedRows);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
  await findAllCourse();
};

const deleteCourse = async () => {
  const selectedRows = gridApi.value.getSelectedRows();
  if (selectedRows.length === 0) {
    ElMessage.warning("请选择要删除的课程");
    return;
  }

  const ids = selectedRows.map((item) => item.id);
  await deleteCourseListService(ids);
};
</script>

<template>
  <div class="box">
    <ag-grid-vue
      :rowData="rowData"
      :columnDefs="columnDefs"
      style="height: 85vh; width: 100%"
      :loadThemeGoogleFonts="true"
      :defaultColDef="defaultColDef"
      :pagination="pagination"
      :paginationPageSize="paginationPageSize"
      :paginationPageSizeSelector="paginationPageSizeSelector"
      :autoSizeStrategy="autoSizeStrategy"
      :rowSelection="rowSelection"
      rowMultiSelectWithClick="true"
      :class="
        store.state.isLightTheme ? 'ag-theme-quartz' : 'ag-theme-quartz-dark'
      "
      @gridReady="onGridReady"
    >
    </ag-grid-vue>
    <div class="upload">
      <VaButton
        preset="primary"
        style="margin-right: 6px"
        class="mr-6 mb-2"
        @click="updateCourse"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="M120-320v-80h320v80H120Zm0-160v-80h480v80H120Zm0-160v-80h480v80H120Zm534 440L512-342l56-56 86 84 170-170 56 58-226 226Z"
          />
        </svg>
      </VaButton>
      <VaButton
        preset="secondary"
        style="margin-right: 6px"
        class="mr-6 mb-2"
        @click="deleteCourse"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="m576-80-56-56 104-104-104-104 56-56 104 104 104-104 56 56-104 104 104 104-56 56-104-104L576-80ZM120-320v-80h280v80H120Zm0-160v-80h440v80H120Zm0-160v-80h440v80H120Z"
          />
        </svg>
      </VaButton>
    </div>
  </div>
</template>

<style scoped>
.box {
  position: relative;
}

.upload {
  float: left;
  position: absolute;
  bottom: 0px;
  left: 6px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  --va-button-content-px: 10px;
  --va-button-content-py: 6px;
}

.ag-theme-quartz {
  --ag-foreground-color: rgb(14, 68, 145);
  --ag-header-background-color: rgb(228, 237, 250);
  --ag-row-hover-color: rgb(216, 226, 255);
  border-radius: 0.375rem;
  box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0, 0, 0.12);
  --ag-row-border--radius: 0.375rem;
  --ag-border-radius: 0.2rem;
}
</style>
