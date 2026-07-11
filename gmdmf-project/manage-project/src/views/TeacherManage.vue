<script lang="ts" setup>
import ExcelJS from "exceljs";
import { ref, onMounted, onBeforeMount } from "vue";
import { VaFile } from "vuestic-ui/web-components";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import $ from "jquery";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import "ag-grid-enterprise";

// 后端接口
import { findAllSchoolAndDepartmentService } from "@/api/School";

import { adminFindAllMajorService } from "@/api/Major";
import { findByTeamQueryService } from "@/api/Team";
import { findAllTeacherService, updateTeacherListService, deleteTeacherListService } from "@/api/Teacher";
const store = useStore();
interface School {
  id: number;
  name: string;
  departmentVOS: Department[] | null;
}

interface Major {
  id: number;
  name: string;
}

interface Department {
  id: number;
  name: string;
}

interface Teacher {
  id: number;
  name: string;
  sex: string;
  birth: Date;
  username: string;
  school: string;
  department: string;
  phone: string;
  email: string;
  address: string;
  introduce: string;
}

const schoolList = ref<School[]>([]);
const majorList = ref<Major[]>([]);

onMounted(async () => {
  const result = await findAllSchoolAndDepartmentService();
  schoolList.value = result.data.data;
  const majorResult = await adminFindAllMajorService();
  majorList.value = majorResult.data.data;
  const teacherResult = await findAllTeacherService();
  rowData.value = teacherResult.data.data;
});

const getSchoolList = async () => {
  if (schoolList.value == null || schoolList.value.length == 0)
    return ["暂无数据"];
  return schoolList.value.map((item: School) => item.name);
};

const getDepartmentList = async (params) => {
  if (params.data.school == null || schoolList.value.length == 0)
    return ["暂无数据"];
  const departmentList = schoolList.value.filter(
    (item) => item.name == params.data.school
  );

  if (
    departmentList.length == 0 ||
    departmentList[0].departmentVOS == undefined ||
    departmentList[0].departmentVOS == null
  )
    return ["暂无数据"];
  return departmentList[0].departmentVOS.map((item) => item.name);
};

const getMajorList = async () => {
  return majorList.value.map((item) => item.name);
};

const getTeamList = async (params) => {
  const result = await findByTeamQueryService(
    params.data.school,
    params.data.department,
    params.data.major
  );

  if (
    result.data.data == undefined ||
    result.data.data == null ||
    result.data.data.length == 0
  )
    return ["暂无数据"];
  return result.data.data.map((item) => item.name);
};

const rowData = ref<Teacher[]>([]);
const columnDefs = ref([
  {
    field: "id",
    headerName: "学生 ID",
    editable: false,
    hide: true,
  },
  {
    field: "name",
    headerName: "姓名",
    maxWidth: 130,
    cellEditor: "agTextCellEditor",
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
    maxWidth: 130,
    cellEditor: "agTextCellEditor",
    cellEditorParams: {
      maxLength: 30,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("用户名长度应在2~30之间");
        return false;
      } else {
        params.data.username = params.newValue;
        return true;
      }
    },
  },
  {
    field: "sex",
    headerName: "性别",
    maxWidth: 120,
    sortable: false,
    cellEditor: "agSelectCellEditor",
    cellEditorParams: { values: ["男", "女"] },
  },
  {
    field: "birth",
    headerName: "生日",
    editable: false,
  },
  {
    field: "school",
    headerName: "学校",
    editable: (params) => {
      return true;
    },
    cellEditor: "agRichSelectCellEditor",
    cellEditorParams: {
      maxLength: 30,
      values: getSchoolList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
    valueSetter: (params) => {
      if (params.data.school != params.newValue) {
        params.data.department = null;
        params.data.teamName = null;
      }
      params.data.school = params.newValue;
      return true;
    },
  },
  {
    field: "department",
    headerName: "院系",
    cellEditor: "agRichSelectCellEditor",
    editable: (params) => {
      return params.data.school != null;
    },
    cellEditorParams: {
      maxLength: 30,
      values: getDepartmentList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
    valueSetter: (params) => {
      if (params.data.department != params.newValue) {
        params.data.teamName = null;
      }
      params.data.department = params.newValue;
      return true;
    },
  },
  {
    field: "phone",
    headerName: "手机",
    cellDataType: "number",
    cellEditor: "agNumberCellEditor",
    cellEditorParams: {
      preventStepping: true,
    },
    valueSetter: (params) => {
      // 手机号正则校验
      const reg =
        /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
      if (reg.test(String(params.newValue))) {
        params.data.phone = params.newValue;
        return true;
      } else {
        ElMessage.warning("手机号格式不正确");
        return false;
      }
    },
  },
  {
    field: "email",
    headerName: "邮箱",
    cellEditor: "agTextCellEditor",
    cellEditorParams: {
      maxLength: 50,
    },
    valueSetter: (params) => {
      // email 正则校验
      const reg = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
      if (reg.test(String(params.newValue))) {
        params.data.email = params.newValue;
        return true;
      } else {
        ElMessage.warning("邮箱格式不正确");
        return false;
      }
    },
  },
  {
    field: "address",
    headerName: "住址",
    cellEditor: "agTextCellEditor",
    cellEditorParams: {
      maxLength: 50,
      preventStepping: true,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 50 || params.newValue.length < 0) {
        ElMessage.warning("住址长度应在0~50之间");
        return false;
      } else {
        params.data.address = params.newValue;
        return true;
      }
    },
  },
  {
    field: "introduce",
    headerName: "介绍",
    cellEditor: "agTextCellEditor",
    maxWidth: 200,
    cellEditorParams: {
      maxLength: 200,
      preventStepping: true,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 200 || params.newValue.length < 0) {
        ElMessage.warning("介绍长度应在0~200之间");
        return false;
      } else {
        params.data.introduce = params.newValue;
        return true;
      }
    },
  },
]);

const defaultColDef = {
  flex: 1,
  minWidth: 70,
  editable: true,
  cellDataType: true,
  sortable: true,
  filter: true,
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
const paginationPageSize = 10;
const paginationPageSizeSelector = [10, 20, 50, 100];

const tableFile = ref([]);
const gridApi = ref(null);
const columnApi = ref(null);

const autoSizeStrategy = {
  type: "fitCellContents",
};

const onGridReady = async (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

// 删除选中数据
const onRemoveSelected = async () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要删除的数据");
    return true;
  }
  let ids = [];
  selectedData.forEach((item) => {
    ids.push(item.id);
  });
  const result = await deleteTeacherListService(ids);
  ElMessage.success(result.data.message ? result.data.message : "删除成功");
  ids = [];
  const teacherResult = await findAllTeacherService();
  rowData.value = teacherResult.data.data;
};

const checkData = () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要修改的数据");
    throw new Error("请选择要修改的数据");
  }

  selectedData.forEach((item) => {
    if (
      item.name == null ||
      item.name.trim() == "" ||
      item.name == "暂无数据"
    ) {
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
    if (item.username.length > 30) {
      ElMessage.warning("用户名长度不能超过30位");
      throw new Error("用户名长度不能超过30位");
    }

    if (item.sex == null || item.sex.trim() == "") {
      ElMessage.warning("请填写性别");
      throw new Error("请填写性别");
    }
    if (
      item.school == null ||
      item.school.trim() == "" ||
      item.school == "暂无数据"
    ) {
      ElMessage.warning("请填写学校");
      throw new Error("请填写学校");
    }
    if (
      item.department == null ||
      item.department.trim() == "" ||
      item.department == "暂无数据"
    ) {
      ElMessage.warning("请填写院系");
      throw new Error("请填写院系");
    }
    if (item.phone == null || item.phone == "") {
      ElMessage.warning("请填写手机号");
      throw new Error("请填写手机号");
    }
    if (String(item.phone).length != 11) {
      ElMessage.warning("手机号格式不正确");
      throw new Error("手机号格式不正确");
    }
    if (item.email == null || item.email.trim() == "") {
      ElMessage.warning("请填写邮箱");
      throw new Error("请填写邮箱");
    }
    if (item.email.length > 50) {
      ElMessage.warning("邮箱长度不能超过50位");
      throw new Error("邮箱长度不能超过50位");
    }
  });
};

// 修改
const updateStudent = async () => {
  try {
    checkData();
  } catch (e) {
    return;
  }

  let teacherList = [];
  const selectedData = gridApi.value.getSelectedRows();
  selectedData.forEach((item) => {
    teacherList.push({
      id: item.id,
      number: item.number,
      name: item.name,
      sex: item.sex,
      school: item.school,
      department: item.department,
      major: item.major,
      teamName: item.teamName,
      idCard: null,
      phone: item.phone,
      email: item.email,
      address: item.address,
      picture: null,
      introduce: item.introduce,
    });
  });

  const result = await updateTeacherListService(teacherList);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
  teacherList = [];
  const teacherResult = await findAllTeacherService();
  rowData.value = teacherResult.data.data;
}
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
      :autoSizeStrategy="autoSizeStrategy"
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
        @click="updateStudent"
      >
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
