<script lang="ts" setup>
import ExcelJS from "exceljs";
import { ref, onMounted } from "vue";
import {
  saveOrUpdateMajorListService,
  adminFindAllMajorService,
  deleteMajorListService,
} from "@/api/Major.js";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import $ from "jquery";
import { ElMessage } from "element-plus";
import { useModal } from "vuestic-ui";

import { useStore } from "vuex";
const store = useStore();

const { confirm } = useModal();
interface Major {
  id: number;
  name: string; 
  exhibition: boolean;
}

const rowData = ref<Major[]>([]);

const columnDefs = ref([
  // id 为空则显示默认值
  {
    field: "id",
    headerName: "专业 ID",
    editable: false,
    maxWidth: 160,
  },
  {
    field: "name",
    headerName: "专业名",
    enableCellChangeFlash: true,
    cellEditor: "agTextCellEditor",
    cellEditorParams: {
      maxLength: 30,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("专业名长度应在2~30之间");
        return false;
      } else {
        params.data.name = params.newValue;
        return true;
      }
    },
  },
    {
    field: "exhibition",
    headerName: "可见",
    maxWidth: 120,
  },
]);

const defaultColDef = {
  flex: 1,
  minWidth: 100,
  filter: true,
  editable: true,
  rowDrag: false,
  lockPinned: true,
  cellClass: "lock-pinned",
  cellRenderer: (params) => {
    if (params.value == null) {
      return "< null >";
    } else {
      return params.value;
    }
  },
};

const rowSelection = {
  mode: "multiRow",
};

const pagination = true;
const paginationPageSize = 20;
const paginationPageSizeSelector = [20, 50, 100];

const gridApi = ref(null);
const columnApi = ref(null);

const onGridReady = (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

const selectedRows = ref(null);

// 删除选中数据
const deleteSelected = () => {
  selectedRows.value = gridApi.value.getSelectedRows();
  if (selectedRows.value.length == 0) {
    ElMessage.warning("请选择要删除的数据");
    return true;
  }

  confirm("你确定要删除选中的专业数据吗?").then(async (ok) => {
    ok &&
      (await deleteMajorListService(selectedRows.value).then(async (result) => {
        ElMessage.success(
          result.data.message ? result.data.message : "删除成功"
        );
        await findAllMajor();
      }));
  });
};

const findAllMajor = async () => {
  const result = await adminFindAllMajorService();
  rowData.value = result.data.data;
};

const addMajor = async () => {

  const major = [{
    id: null,
    name: null,
    exhibition: true,
  }];

  gridApi.value.applyTransaction({
    add: major,
  });
};

const checkDTO = () => {
  selectedRows.value = gridApi.value.getSelectedRows();

  if (selectedRows.value.length == 0) {
    ElMessage.warning("请选择要保存的数据");
    return true;
  }

  // 使用 forEach 遍历数组会导致错误，不能使用 return 语句退出
  for (let i = 0; i < selectedRows.value.length; i++) {
    if (
      selectedRows.value[i].name == null ||
      selectedRows.value[i].name.trim() == ""
    ) {
      ElMessage.warning("专业名不能为空");
      return true;
    }
  }

  for (let i = 0; i < selectedRows.value.length; i++) {
    for (let j = 0; j < rowData.value.length; j++) {
      if (
        selectedRows.value[i].id != rowData.value[j].id &&
        rowData.value[j].name.trim() == selectedRows.value[i].name.trim()
      ) {
        ElMessage.warning(`${selectedRows.value[i].name}专业重复`);
        return true;
      }
    }
  }

  return false;
};

const saveOrUpdateMajor = async () => {
  selectedRows.value = gridApi.value.getSelectedRows();
  if (checkDTO()) return;
  const result = await saveOrUpdateMajorListService(selectedRows.value);
  ElMessage.success(result.data.message ? result.data.message : "保存成功");
  await findAllMajor();
};

onMounted(async () => {
  await findAllMajor();
});
</script>

<template>
  <div class="box">
    <ag-grid-vue
      :rowData="rowData"
      :columnDefs="columnDefs"
      style="height: 85vh; width: 100%"
      :loadThemeGoogleFonts="true"
      :defaultColDef="defaultColDef"
      :rowSelection="rowSelection"
      :pagination="pagination"
      :paginationPageSize="paginationPageSize"
      :paginationPageSizeSelector="paginationPageSizeSelector"
      :class="
        store.state.isLightTheme ? 'ag-theme-quartz' : 'ag-theme-quartz-dark'
      "
      @gridReady="onGridReady"
    >
    </ag-grid-vue>
    <div class="upload">
      <VaButton
        :preset="store.state.isLightTheme ? 'primary' : 'secondary'"
        style="margin-right: 6px"
        class="mr-6 mb-2"
        @click="addMajor"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          :fill="store.state.isLightTheme ? '#5f6368' : '#fefefe'"
        >
          <path
            d="M200-120v-640q0-33 23.5-56.5T280-840h240v80H280v518l200-86 200 86v-278h80v400L480-240 200-120Zm80-640h240-240Zm400 160v-80h-80v-80h80v-80h80v80h80v80h-80v80h-80Z"
          />
        </svg>
      </VaButton>
      <VaButton
        preset="secondary"
        style="margin-right: 6px"
        class="mr-6 mb-2"
        @click="deleteSelected"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="M840-680H600v-80h240v80ZM200-120v-640q0-33 23.5-56.5T280-840h240v80H280v518l200-86 200 86v-278h80v400L480-240 200-120Zm80-640h240-240Z"
          />
        </svg>
      </VaButton>
      <VaButton preset="secondary" class="mr-6 mb-2" @click="saveOrUpdateMajor">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="M713-600 600-713l56-57 57 57 141-142 57 57-198 198ZM200-120v-640q0-33 23.5-56.5T280-840h240v80H280v518l200-86 200 86v-278h80v400L480-240 200-120Zm80-640h240-240Z"
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
