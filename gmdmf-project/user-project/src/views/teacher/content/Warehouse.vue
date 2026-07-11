<script lang="ts" setup>
import { computed, onBeforeMount, onMounted, ref } from "vue";
import { ElMessage, type ComponentSize } from "element-plus";

import {
  saveOrUpdateWarehouseService,
  findByTeacherIdWarehouseService,
  findByPageWarehouseService,
} from "@/api/Warehouse";
import { useRouter } from "vue-router";
import { useWarehouseStore } from "@/stores/warehouse";
import { Edit } from "@element-plus/icons-vue";
import { useUserInfoStore } from "@/stores/userInfo";
const router = useRouter();
const warehouseStore = useWarehouseStore();
const userInfoStore = useUserInfoStore();
const dialogFormVisible = ref(false);
const total = ref(0);
const warehouseQuery = ref({
  currentPage: 1,
  pageSize: 4,
  school: "",
  name: "",
});

const warehouseDTO = ref({
  id: null,
  name: "",
});

const warehouseList = ref([
  {
    id: 0,
    name: "",
    school: "",
    teacherName: "",
    createTime: "",
  },
]);

onMounted(async () => {
  $(".contactButton").animate(
    {
      opacity: 1,
      marginTop: -20,
    },
    1000
  );
  await warehouseFindByPage();
});

const getWarehouseList = async () => {
  const result = await findByTeacherIdWarehouseService();
  warehouseList.value = result.data.data;
};

const goWarehouse = (id) => {
  warehouseStore.setWarehouse(id);
  router.push({
    path: "/t-warehouse",
  });
};

const warehouseFindByPage = async () => {
  const result = await findByPageWarehouseService(warehouseQuery.value);
  warehouseList.value = result.data.data.list;
};

const addWarehouse = async () => {
  dialogFormVisible.value = true;
  title.value = "添加题库";
  warehouseDTO.value = { id: null, name: "" };
};

const updateWarehouse = (id, name) => {
  dialogFormVisible.value = true;
  title.value = "修改题库";
  warehouseDTO.value = { id, name };
};

const submitWarehouse = async () => {
  if (warehouseDTO.value.name.trim() == "") {
    ElMessage.error("题库名不能为空");
    return;
  }
  const result = await saveOrUpdateWarehouseService(warehouseDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "操作成功");
  await getWarehouseList();
  dialogFormVisible.value = false;
  warehouseDTO.value = { id: null, name: "" };
};

const title = ref("添加题库");
</script>

<template>
  <el-dialog v-model="dialogFormVisible" :title="title" width="500">
    <el-input
      v-model="warehouseDTO.name"
      style="width: 100%"
      maxlength="30"
      placeholder="请输入题库名，题库创建后不支持删除操作"
      show-word-limit
      type="text"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitWarehouse">
          {{ title == "添加题库" ? "创建" : "修改" }}
        </el-button>
      </div>
    </template>
  </el-dialog>
  <button class="contactButton" @click="addWarehouse">
    添加题库
    <div class="iconButton">
      <svg
        t="1715927486406"
        class="icon"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="3601"
        width="16"
        height="16"
      >
        <path
          d="M455.111111 170.666667h113.777778v682.666666H455.111111z"
          fill="#ffffff"
          p-id="3602"
        ></path>
        <path
          d="M853.333333 455.111111v113.777778H170.666667V455.111111z"
          fill="#ffffff"
          p-id="3603"
        ></path>
      </svg>
    </div>
  </button>

  <el-card
    style="
      min-width: 720px;
      height: 82vh;
      position: relative;
      overflow-y: scroll;
      overflow-x: none;
      -ms-overflow-style: none;
      scrollbar-width: none;
    "
    shadow="never"
  >
    <div v-if="warehouseList.length == 0">
      <el-empty :image-size="400" />
    </div>
    <el-card
      style="width: 100%; margin-bottom: 10px"
      shadow="never"
      v-for="(warehouse, index) in warehouseList"
      :key="index"
    >
      <el-descriptions class="margin-top" :title="warehouse.name" :column="3">
        <template #extra>
          <el-button
            size="24"
            :icon="Edit"
            circle
            @click="updateWarehouse(warehouse.id, warehouse.name)"
          />
          <el-button type="primary" @click="goWarehouse(warehouse.id)"
            >进入题库</el-button
          >
        </template>
        <el-descriptions-item label="教师名">{{
          warehouse.teacherName
        }}</el-descriptions-item>
        <el-descriptions-item label="学校">
          {{ warehouse.school }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          warehouse.createTime
        }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-card>
</template>

<style lang="scss" scoped>
.example-pagination-block {
  float: right;
  margin-bottom: 16px;
}

.contactButton {
  position: fixed;
  margin-top: 20px;
  background: #7079f0;
  color: white;
  font-family: inherit;
  padding: 0;
  padding-left: 1em;
  padding-right: 3em;
  margin-left: 21px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  letter-spacing: 0.05em;
  box-shadow: inset 0 0 1.6em -0.6em #714da6;
  overflow: hidden;
  height: 32px;
  z-index: 99;
  opacity: 0;
}

.iconButton {
  position: absolute;
  margin-left: 1em;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2em;
  width: 2em;
  border-radius: 6px;
  box-shadow: 0.1em 0.1em 0.6em 0.2em #7a8cf3;
  right: 0.3em;
  transition: all 0.3s;
  margin-top: -19px;
}

.contactButton:hover {
  transform: translate(-0.05em, -0.05em);
  box-shadow: 0.15em 0.15em #5566c2;
}

.contactButton:active {
  transform: translate(0.05em, 0.05em);
  box-shadow: 0.05em 0.05em #5566c2;
}
</style>
