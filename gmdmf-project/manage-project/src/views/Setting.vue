<script lang="ts" setup>
import ExcelJS from "exceljs";
import { ref, onMounted } from "vue";
import { useUserInfoStore } from "@/stores/userInfo";
import { VaFile } from "vuestic-ui/web-components";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import $ from "jquery";
import {
  findAllManageService,
  saveOrUpdateManageService,
  deleteManageService,
} from "@/api/Manage";
import { ElMessage } from "element-plus";
import { useStore } from "vuex";
import UploadButton from "./components/UploadButton.vue";
const store = useStore();
const userInfoStore = useUserInfoStore();

const rowSelection = {
  mode: "single",
};

const pagination = true;
const paginationPageSize = 10;
const paginationPageSizeSelector = [10, 50, 100];
const gridApi = ref(null);
const columnApi = ref(null);

const rowData = ref([]);

const columnDefs = ref([
  {
    field: "id",
    headerName: "管理员 ID",
    editable: false,
  },
  {
    field: "name",
    headerName: "姓名",
    cellEditorParams: {
      maxLength: 30,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("名字长度应在2~30之间");
        return false;
      } else {
        params.data.name = params.newValue;
        return true;
      }
    },
  },
  {
    field: "username",
    headerName: "用户名",
    cellEditorParams: {
      maxLength: 20,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 20 || params.newValue.length < 2) {
        ElMessage.warning("用户名长度应在2~20之间");
        return false;
      } else {
        params.data.username = params.newValue;
        return true;
      }
    },
  },
  {
    field: "password",
    headerName: "密码",
    cellEditorParams: {
      maxLength: 30,
    },
    cellRenderer: (params) => {
      if (params.value == null) {
        return "************";
      } else {
        return params.value;
      }
    },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("密码长度应在2~30之间");
        return false;
      } else {
        params.data.password = params.newValue;
        return true;
      }
    },
  },
  {
    field: "introduce",
    headerName: "个人简介",
  },
  {
    field: "picture",
    headerName: "头像",
    cellEditor: UploadButton,
    // 弹出编辑器，默认出现在单元格上方
    cellEditorPopup: true,
    // 设置弹出编辑器位置 under 下方
    cellEditorPopupPosition: "under",
    valueGetter: (params) => {
      return params.data.picture;
    },
    valueSetter: async (params) => {
      if (params.data.picture == null || params.data.picture.trim() == "") return false;
      
      const manageDTO = {
        id: params.data.id,
        picture: params.data.picture,
      };
      await saveOrUpdateManageService(manageDTO);
      await findAllManage();
      return true;
    },
  },
  { field: "rule", headerName: "身份", editable: false },
]);

const defaultColDef = {
  flex: 1,
  minWidth: 80,
  filter: true,
  cellDataType: true,
  sortable: true,
  cellRenderer: (params) => {
    if (params.value == null) {
      return "< null >";
    } else {
      return params.value;
    }
  },
  editable: (params) => {
    return (
      userInfoStore.userInfo.id == params.data.id ||
      userInfoStore.userInfo.id == 1
    );
  },
};

const onGridReady = async (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

onMounted(async () => {
  await findAllManage();
});

const findAllManage = async () => {
  const result = await findAllManageService();
  rowData.value = result.data.data;
};

// 添加数据
const addItems = () => {
  const newItems = [
    {
      id: null,
      name: null,
      username: null,
      password: "",
      picture: null,
      rule: "管理",
    },
  ];
  gridApi.value.applyTransaction({
    add: newItems,
  });
};

// 删除选中数据
const onRemoveSelected = async () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要删除的数据");
    return true;
  }
  if (selectedData[0].id) {
    const result = await deleteManageService(selectedData[0].id);
    ElMessage.success(result.data.message ? result.data.message : "删除成功");
  } else {
    gridApi.value.applyTransaction({
      remove: selectedData,
    });
  }
  await findAllManage();
};

const submitManage = async () => {
  try {
    checkData();
  } catch (e) {
    return;
  }
  const selectedData = gridApi.value.getSelectedRows();
  const manageDTO = {
    id: selectedData[0].id,
    name: selectedData[0].name,
    username: selectedData[0].username,
    password: selectedData[0].password,
    introduce: selectedData[0].introduce,
    picture: selectedData[0].picture,
  };
  const result = await saveOrUpdateManageService(manageDTO);
  ElMessage.success(result.data.message ? result.data.message : "保存成功");
  await findAllManage();
};

const checkData = () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要保存的数据");
    throw new Error("请选择要保存的数据");
  }
  selectedData.forEach((item) => {
    if (item.name == null || item.name == "") {
      ElMessage.warning("请填写姓名");
      throw new Error("请填写姓名");
    }
    if (item.name.length > 30) {
      ElMessage.warning("姓名长度不能超过30位");
      throw new Error("姓名长度不能超过30位");
    }
    if (
      item.username == null ||
      item.username.trim() == "" ||
      item.username == "暂无数据"
    ) {
      ElMessage.warning("请填写用户名");
      throw new Error("请填写用户名");
    }
    if (item.username.length > 20) {
      ElMessage.warning("用户名长度不能超过20位");
      throw new Error("用户名长度不能超过20位");
    }
    if (
      item.id == null &&
      (item.password == null || item.password.trim() == "")
    ) {
      ElMessage.warning("请填写密码");
      throw new Error("请填写密码");
    }

    if (item.id == null && item.password.length > 30) {
      ElMessage.warning("密码长度不能超过30位");
      throw new Error("密码长度不能超过30位");
    }
  });
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
    <div class="BtnTools">
      <VaButton
        preset="primary"
        style="margin-right: 6px"
        class="mr-6 mb-2"
        @click="addItems"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="M720-400v-120H600v-80h120v-120h80v120h120v80H800v120h-80Zm-360-80q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM40-160v-112q0-34 17.5-62.5T104-378q62-31 126-46.5T360-440q66 0 130 15.5T616-378q29 15 46.5 43.5T680-272v112H40Zm80-80h480v-32q0-11-5.5-20T580-306q-54-27-109-40.5T360-360q-56 0-111 13.5T140-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T440-640q0-33-23.5-56.5T360-720q-33 0-56.5 23.5T280-640q0 33 23.5 56.5T360-560Zm0-80Zm0 400Z"
          />
        </svg>
      </VaButton>
      <VaButton
        preset="secondary"
        style="margin-right: 6px"
        class="mr-6 mb-2"
        @click="onRemoveSelected"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="m696-440-56-56 83-84-83-83 56-57 84 84 83-84 57 57-84 83 84 84-57 56-83-83-84 83Zm-336-40q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM40-160v-112q0-34 17.5-62.5T104-378q62-31 126-46.5T360-440q66 0 130 15.5T616-378q29 15 46.5 43.5T680-272v112H40Zm80-80h480v-32q0-11-5.5-20T580-306q-54-27-109-40.5T360-360q-56 0-111 13.5T140-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T440-640q0-33-23.5-56.5T360-720q-33 0-56.5 23.5T280-640q0 33 23.5 56.5T360-560Zm0-80Zm0 400Z"
          />
        </svg>
      </VaButton>
      <VaButton preset="secondary" class="mr-6 mb-2" @click="submitManage">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="#5f6368"
        >
          <path
            d="M80-160v-112q0-33 17-62t47-44q51-26 115-44t141-18q30 0 58.5 3t55.5 9l-70 70q-11-2-21.5-2H400q-71 0-127.5 17T180-306q-9 5-14.5 14t-5.5 20v32h250l80 80H80Zm542 16L484-282l56-56 82 82 202-202 56 56-258 258ZM400-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47Zm10 240Zm-10-320q33 0 56.5-23.5T480-640q0-33-23.5-56.5T400-720q-33 0-56.5 23.5T320-640q0 33 23.5 56.5T400-560Zm0-80Z"
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
.BtnTools {
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
