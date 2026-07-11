<script lang="ts" setup>
import ExcelJS from "exceljs";
import { ref, onMounted, nextTick } from "vue";
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
import { findAllSchoolService, findAllSchoolAndDepartmentService } from "@/api/School";

import { adminFindAllMajorService } from "@/api/Major";
import { findByTeamQueryService } from "@/api/Team";
import { registerStudentService } from "@/api/Student";

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

interface Student {
  school: string;
  department: string;
  major: string;
  team: string;
  number: string;
  name: string;
  sex: string;
  idCard: string;
  phone: string;
  email: string;
}

const schoolList = ref<School[]>([]);
const majorList = ref<Major[]>([]);

onMounted(async () => {
  const result = await findAllSchoolAndDepartmentService();
  schoolList.value = result.data.data;
  const majorResult = await adminFindAllMajorService();
  majorList.value = majorResult.data.data;
});

const getSchoolList = async () => {
  if (schoolList.value == null || schoolList.value.length == 0) return ["暂无数据"];
  return schoolList.value.map((item: School) => item.name);
};

const getDepartmentList = async (params) => {
  if (params.data.school == null || schoolList.value.length == 0) return ["暂无数据"];
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

const rowData = ref<Student[]>([]);
const columnDefs = ref([
  {
    field: "number",
    headerName: "学号",
    cellEditor: "agTextCellEditor",
    cellEditorParams: {
      preventStepping: true,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 20 || params.newValue.length < 2) {
        ElMessage.warning("学号长度应在2~20之间");
        return false;
      } else {
        params.data.number = params.newValue;
        return true;
      }
    },
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
    field: "sex",
    headerName: "性别",
    maxWidth: 120,
    sortable: false,
    cellEditor: "agSelectCellEditor",
    cellEditorParams: { values: ["男", "女"] },
  },
  {
    field: "school",
    headerName: "学校",
    editable: (params) => {
      params.data.department = null;
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
        params.data.team = null;
      }
      params.data.department = params.newValue;
      return true;
    },
  },
  {
    field: "major",
    headerName: "专业",
    cellEditor: "agRichSelectCellEditor",
    cellEditorParams: {
      maxLength: 30,
      values: getMajorList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
    valueSetter: (params) => {
      if (params.data.major != params.newValue) {
        params.data.team = null;
      }
      params.data.major = params.newValue;
      return true;
    },
  },
  {
    field: "team",
    headerName: "班级",
    editable: (params) => {
      return params.data.department != null && params.data.major != null;
    },
    cellEditor: "agRichSelectCellEditor",
    cellEditorParams: {
      maxLength: 30,
      values: getTeamList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
  },
  {
    field: "idCard",
    headerName: "身份证",
    cellEditor: "agTextCellEditor",
    cellEditorParams: {
      maxLength: 18,
      preventStepping: true,
    },
    valueSetter: (params) => {
      // 身份证正则校验
      const reg = /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
      if (reg.test(String(params.newValue))) {
        params.data.idCard = params.newValue;
        return true;
      } else {
        ElMessage.warning("身份证格式不正确");
        return false;
      }
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
      const reg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
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
]);

const defaultColDef = {
  flex: 1,
  minWidth: 70,
  filter: true,
  editable: true,
  cellDataType: true,
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

const onGridReady = async (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

const changeFile = async (files: VaFile[]) => {
  let studentRegisterList: Student[] = [];

  if (files != null || files != undefined) {
    const file: any = files;

    let reader = new FileReader();
    reader.readAsArrayBuffer(file);
    const workbook = new ExcelJS.Workbook();

    reader.onload = async function () {
      const content: any = reader.result;
      await workbook.xlsx.load(content);
      let worksheet = workbook.getWorksheet(1);

      try {
        worksheet.eachRow({ includeEmpty: true }, function (row, rowNumber) {
          // 如果第一行是表头，则跳过
          if (row.values[1] != "学校") {
            let student = {
              school: row.values[1],
              department: row.values[2],
              major: row.values[3],
              team: row.values[4],
              number: row.values[5],
              name: row.values[6],
              sex: row.values[7],
              idCard: row.values[8],
              phone: row.values[9],
              email: row.values[10],
            };
            if (student.email == "[object Object]") {
              throw new Error("邮箱不能为超链接");
            }
            studentRegisterList.push(student);
          }
        });
        rowData.value = studentRegisterList;
      } catch (error) {
        if (error.message == "邮箱不能为超链接") {
          ElMessage.error(error.message);
        } else {
          ElMessage.error("请检查文件格式是否正确");
        }
      }
    };
  }
};

const saveStudentRegister = async (files: VaFile[]) => {
  await changeFile(files);
};

// 添加数据
const addItems = () => {
  const newItems = [
    {
      school: null,
      department: null,
      major: null,
      team: null,
      number: null,
      name: null,
      sex: null,
      idCard: null,
      phone: null,
      email: null,
    },
  ];
  gridApi.value.applyTransaction({
    add: newItems,
  });
};

// 删除选中数据
const onRemoveSelected = () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要删除的数据");
    return true;
  }
  gridApi.value.applyTransaction({
    remove: selectedData,
  });
};

const checkData = () => {
  gridApi.value.forEachNode((rowNode) => {
    if (rowNode.data.number == null || rowNode.data.number == "") {
      throw new Error("请填写学号");
    }
    if (rowNode.data.number.length > 30) {
      throw new Error("学号长度不能超过30位");
    }
    if (
      rowNode.data.name == null ||
      rowNode.data.name.trim() == "" ||
      rowNode.data.name == "暂无数据"
    ) {
      throw new Error("请填写姓名");
    }
    if (rowNode.data.name.length > 30) {
      throw new Error("姓名长度不能超过30位");
    }

    if (rowNode.data.sex == null || rowNode.data.sex.trim() == "") {
      throw new Error("请填写性别");
    }
    if (
      rowNode.data.school == null ||
      rowNode.data.school.trim() == "" ||
      rowNode.data.school == "暂无数据"
    ) {
      throw new Error("请填写学校");
    }
    if (
      rowNode.data.department == null ||
      rowNode.data.department.trim() == "" ||
      rowNode.data.department == "暂无数据"
    ) {
      throw new Error("请填写院系");
    }
    if (
      rowNode.data.major == null ||
      rowNode.data.major.trim() == "" ||
      rowNode.data.major == "暂无数据"
    ) {
      throw new Error("请填写专业");
    }
    if (
      rowNode.data.team == null ||
      rowNode.data.team.trim() == "" ||
      rowNode.data.team == "暂无数据"
    ) {
      throw new Error("请填写班级");
    }
    if (rowNode.data.idCard == null || rowNode.data.idCard == "") {
      throw new Error("请填写身份证号");
    }
    let reg = /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
    if (!reg.test(String(rowNode.data.idCard))) {
      throw new Error("身份证号格式不正确");
    }
    if (rowNode.data.phone == null || rowNode.data.phone == "") {
      throw new Error("请填写手机号");
    }

    console.log(rowNode.data.phone);

    let regPhone = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
    if (!regPhone.test(String(rowNode.data.phone))) {
      throw new Error("手机号格式不正确");
    }

    if (rowNode.data.email == null || rowNode.data.email.trim() == "") {
      throw new Error("请填写邮箱");
    }
    reg = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
    if (!reg.test(String(rowNode.data.email))) {
      ElMessage.warning("");
      throw new Error("邮箱格式不正确");
    }
    if (rowNode.data.email.length > 50) {
      throw new Error("邮箱长度不能超过50位");
    }
  });
};

// 注册
const submitRegister = async () => {
  try {
    checkData();
  } catch (error) {
    ElMessage.warning(error.message);
    return;
  }

  let studentList = [];
  gridApi.value.forEachNode((rowNode) => {
    studentList.push({
      id: null,
      number: String(rowNode.data.number),
      name: rowNode.data.name,
      sex: rowNode.data.sex,
      school: rowNode.data.school,
      department: rowNode.data.department,
      major: rowNode.data.major,
      teamName: rowNode.data.team,
      idCard: String(rowNode.data.idCard),
      phone: String(rowNode.data.phone),
      email: rowNode.data.email,
      address: null,
      picture: null,
      introduce: null,
    });
  });

  const result = await registerStudentService(studentList);
  ElMessage.success(result.data.message ? result.data.message : "注册成功");
  rowData.value = [];
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
      :class="store.state.isLightTheme ? 'ag-theme-quartz' : 'ag-theme-quartz-dark'"
      @gridReady="onGridReady"
    >
    </ag-grid-vue>
    <div class="BtnTools">
      <VaFileUpload
        v-model="tableFile"
        undo
        type="single"
        hideFileList
        file-types="xlsx"
        @update:modelValue="saveStudentRegister"
        style="float: left"
      >
        <template #default>
          <VaButton
            :preset="store.state.isLightTheme ? 'primary' : 'secondary'"
            style="margin-right: 6px"
            class="mr-6 mb-2"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              height="24px"
              viewBox="0 -960 960 960"
              width="24px"
              :fill="store.state.isLightTheme ? '#5f6368' : '#fefefe'"
            >
              <path
                d="M440-320v-326L336-542l-56-58 200-200 200 200-56 58-104-104v326h-80ZM240-160q-33 0-56.5-23.5T160-240v-120h80v120h480v-120h80v120q0 33-23.5 56.5T720-160H240Z"
              />
            </svg>
          </VaButton>
        </template>
      </VaFileUpload>
      <VaButton
        preset="secondary"
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
      <VaButton preset="secondary" class="mr-6 mb-2" @click="submitRegister">
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
