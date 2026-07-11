<script lang="ts" setup>
import ExcelJS from "exceljs";
import { ref, onMounted, onBeforeMount } from "vue";
import { VaFile } from "vuestic-ui/web-components";
import { AgGridVue } from "ag-grid-vue3";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import "ag-grid-enterprise";
import $ from "jquery";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import { findAllSchoolService } from "@/api/School";
import { findBySchoolNameDepartmentService } from "@/api/Department";
import { adminFindAllMajorService } from "@/api/Major";
import {
  findAllTeamService,
  saveTeamService,
  updateListTeamService,
  deleteListTeamService,
} from "@/api/Team";

import SchoolDateTimeComponent from "./components/SchoolDateTimeComponent.vue";

const store = useStore();

interface Department {
  id: number;
  name: string;
  schoolId: number;
  schoolName: string;
  exhibition: boolean;
}

const rowSelection = ref(null);

const autoGroupColumnDef = ref(null);
const groupDisplayType = ref(null);

const pagination = true;
const paginationPageSize = 20;
const paginationPageSizeSelector = [20, 50, 100];

const gridApi = ref(null);
const columnApi = ref(null);
const rowData = ref<Department[]>([]);
const showModal = ref(false);

const teamDTO = ref({
  schoolName: null,
  departmentName: null,
  majorName: null,
  name: null,
  endTime: null,
});

const searchSchoolValue = ref("");
const searchDepartmentValue = ref("");
const searchMajorValue = ref("");

const schoolList = ref([]);
const departmentList = ref([]);
const majorList = ref([]);

const getDepartmentList = async (params) => {
  if (params.data.schoolName == null || schoolList.value.length == 0)
    return ["暂无数据"];
  await findAllBySchoolNameDepartment(params.data.schoolName);
  return departmentList.value;
};

const columnDefs = ref([
  {
    field: "schoolId",
    headerName: "学校 ID",
    editable: false,
    hide: true,
  },
  {
    field: "schoolName",
    headerName: "学校",
    rowGroup: true,
    hide: true,
    editable: (params) => {
      return true;
    },
    cellEditor: "agRichSelectCellEditor",
    cellEditorParams: {
      maxLength: 30,
      values: schoolList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
    valueSetter: async (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("学校名长度应在2~30之间");
        return false;
      } else {
        params.data.schoolName = params.newValue;
        params.data.departmentName = null;
        return true;
      }
    },
  },
  {
    field: "schoolExhibition",
    headerName: "可见",
    hide: true,
  },
  {
    field: "departmentId",
    headerName: "院系 ID",
    editable: false,
    hide: true,
  },
  {
    field: "departmentName",
    headerName: "院系",
    rowGroup: true,
    hide: true,
    cellEditor: "agRichSelectCellEditor",
    cellEditorParams: {
      maxLength: 30,
      values: getDepartmentList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("院系名长度应在2~30之间");
        return false;
      } else {
        params.data.departmentName = params.newValue;
        return true;
      }
    },
    editable: (params) => {
      return params.data.schoolName != null;
    },
  },
  {
    field: "departmentExhibition",
    headerName: "可见",
    hide: true,
  },
  {
    field: "majorName",
    headerName: "专业",
    rowGroup: true,
    hide: true,
    cellEditor: "agRichSelectCellEditor",
    cellEditorParams: {
      maxLength: 30,
      values: majorList,
      allowTyping: true,
      filterList: true,
      highlightMatch: true,
      searchType: "match",
    },
    valueSetter: (params) => {
      params.data.majorName = params.newValue;
      return true;
    },
  },
  {
    field: "teamId",
    headerName: "班级 ID",
    editable: false,
    hide: true,
  },
  {
    field: "teamName",
    headerName: "班级",
    editable: true,
    cellEditorParams: {
      maxLength: 30,
    },
    valueSetter: (params) => {
      if (params.newValue.length > 30 || params.newValue.length < 2) {
        ElMessage.warning("班级名长度应在2~30之间");
        return false;
      } else {
        params.data.teamName = params.newValue;
        return true;
      }
    },
  },
  {
    field: "endTime",
    headerName: "结课时间",
    cellEditor: SchoolDateTimeComponent,
    cellEditorPopup: true,
    cellEditorPopupPosition: "under",
    valueSetter: (params) => {
      return true;
    },
  },
]);

const defaultColDef = {
  flex: 1,
  minWidth: 80,
  filter: true,
  editable: true,
};

const onGridReady = (params) => {
  gridApi.value = params.api;
  columnApi.value = params.columnApi;
};

const findAllSchool = async () => {
  const result = await findAllSchoolService();
  schoolList.value = result.data.data.map((item) => item.name);
};

const findAllBySchoolNameDepartment = async (schoolName) => {
  const result = await findBySchoolNameDepartmentService(schoolName);
  departmentList.value = result.data.data.map((item) => item.name);
};

const findAllMajor = async () => {
  const result = await adminFindAllMajorService();
  majorList.value = result.data.data.map((item) => item.name);
};

const findAllTeam = async () => {
  const result = await findAllTeamService();
  rowData.value = result.data.data;
  console.log(rowData.value);
};

const addSchool = async () => {
  schoolList.value.push(searchSchoolValue.value);
  teamDTO.value.schoolName = searchSchoolValue.value;
};

const addDepartment = async () => {
  departmentList.value.push(searchDepartmentValue.value);
  teamDTO.value.departmentName = searchDepartmentValue.value;
};

const saveData = async () => {
  if (teamDTO.value.schoolName == null) {
    ElMessage.warning("请输入学校名");
    showModal.value = true;
    return;
  }

  if (teamDTO.value.departmentName == null) {
    ElMessage.warning("请输入院系名");
    showModal.value = true;
    return;
  }

  if (teamDTO.value.majorName != null) {
    if (teamDTO.value.name == null) {
      ElMessage.warning("请输入班级名");
      showModal.value = true;
      return;
    }
    if (teamDTO.value.endTime == null) {
      ElMessage.warning("请输入结课时间");
      showModal.value = true;
      return;
    }
  }

  teamDTO.value.endTime =
    new Date(teamDTO.value.endTime).toLocaleDateString() +
    " " +
    new Date(teamDTO.value.endTime).toLocaleTimeString();
  const result = await saveTeamService(teamDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "添加成功");
  await findAllTeam();
  await findAllSchool();
  showModal.value = false;
  clean();
};

const updateDate = async () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要修改的数据");
    return;
  }

  let teamDTOS = [];
  selectedData.forEach((item) => {
    teamDTOS.push({
      id: item.teamId,
      name: item.teamName,
      schoolId: item.schoolId,
      schoolName: item.schoolName,
      schoolExhibition: item.schoolExhibition,
      departmentId: item.departmentId,
      departmentName: item.departmentName,
      departmentExhibition: item.departmentExhibition,
      majorName: item.majorName,
      endTime: item.endTime,
    });
  });

  console.log(teamDTOS);

  const result = await updateListTeamService(teamDTOS);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
  await findAllSchool();
  await findAllMajor();
  await findAllTeam();
};

const deleteSelected = async () => {
  const selectedData = gridApi.value.getSelectedRows();
  if (selectedData.length == 0) {
    ElMessage.warning("请选择要删除的院系");
    return;
  }

  let ids = [];
  selectedData.forEach((item) => {
    ids.push(item.teamId);
  });

  const result = await deleteListTeamService(ids);
  ElMessage.success(result.data.message ? result.data.message : "删除成功");
  await findAllSchool();
  await findAllMajor();
  await findAllTeam();
};

const clean = () => {
  searchSchoolValue.value = "";
  searchDepartmentValue.value = "";
  searchMajorValue.value = "";
  teamDTO.value = {
    schoolName: null,
    departmentName: null,
    majorName: null,
    name: null,
    endTime: null,
  };
};

onMounted(async () => {
  // await findAllSchoolAndDepartment();
  await findAllSchool();
  await findAllMajor();
  await findAllTeam();
});

onBeforeMount(() => {
  autoGroupColumnDef.value = {
    minWidth: 200,
    headerName: "学校",
  };
  rowSelection.value = {
    mode: "multiRow",
    groupSelects: "descendants",
  };
});
</script>

<template>
  <VaModal
    v-model="showModal"
    ok-text="确定"
    cancel-text="取消"
    @ok="saveData"
    @beforeClose="clean"
  >
    <h5 class="va-h5">添加院系</h5>
    <br />
    <VaSelect
      v-model="teamDTO.schoolName"
      v-model:search="searchSchoolValue"
      class="col-span-1"
      label="学校"
      placeholder="请输入学校名"
      :options="schoolList"
      autocomplete
      allow-create
      @create-new="addSchool"
      @clear="clean"
      @update:modelValue="findAllBySchoolNameDepartment(teamDTO.schoolName)"
      selectedTopShown
      noOptionsText="暂无数据"
      style="width: 100%; margin-bottom: 12px"
    />
    <VaSelect
      v-model="teamDTO.departmentName"
      v-model:search="searchDepartmentValue"
      class="col-span-1"
      label="院系"
      placeholder="请输入院系名"
      :options="departmentList"
      autocomplete
      allow-create
      @create-new="addDepartment"
      @clear="clean"
      selectedTopShown
      noOptionsText="暂无数据"
      style="width: 100%; margin-bottom: 12px"
    />
    <VaSelect
      v-model="teamDTO.majorName"
      v-model:search="searchMajorValue"
      class="col-span-1"
      label="专业"
      placeholder="请输入专业名"
      :options="majorList"
      autocomplete
      allow-create
      @create-new="addSchool"
      @clear="clean"
      selectedTopShown
      noOptionsText="暂无数据"
      style="width: 100%; margin-bottom: 12px"
    />
    <VaInput
      style="width: 100%; margin-bottom: 10px"
      v-model="teamDTO.name"
      placeholder="请输入班级名"
      label="班级"
    />

    <VaDateInput
      v-model="teamDTO.endTime"
      label="结课日期"
      placeholder="请输入日期"
      clearable
      style="margin: 10px 0; margin-right: 1vw"
    />
    <VaTimeInput
      v-model="teamDTO.endTime"
      label="结课时间"
      placeholder="请输入时间"
      class="w-28"
      style="margin: 10px 0"
    />
  </VaModal>
  <div class="box">
    <ag-grid-vue
      :rowData="rowData"
      :columnDefs="columnDefs"
      style="height: 85vh; width: 100%"
      :defaultColDef="defaultColDef"
      :autoGroupColumnDef="autoGroupColumnDef"
      :groupDisplayType="groupDisplayType"
      :loadThemeGoogleFonts="true"
      :rowSelection="rowSelection"
      :suppressAggFuncInHeader="true"
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
        @click="showModal = true"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          :fill="store.state.isLightTheme ? '#5f6368' : '#fefefe'"
        >
          <path
            d="M120-320v-80h280v80H120Zm0-160v-80h440v80H120Zm0-160v-80h440v80H120Zm520 480v-160H480v-80h160v-160h80v160h160v80H720v160h-80Z"
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
            d="m576-80-56-56 104-104-104-104 56-56 104 104 104-104 56 56-104 104 104 104-56 56-104-104L576-80ZM120-320v-80h280v80H120Zm0-160v-80h440v80H120Zm0-160v-80h440v80H120Z"
          />
        </svg>
      </VaButton>
      <VaButton preset="secondary" class="mr-6 mb-2" @click="updateDate">
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
