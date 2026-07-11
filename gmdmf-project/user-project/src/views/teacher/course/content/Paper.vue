<script lang="ts" setup>
import { nextTick, onBeforeMount, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox, type ComponentSize } from "element-plus";
import {
  savePaperService,
  findByPaperQueryService,
  deletePaperService,
} from "@/api/Paper";
import { useRouter } from "vue-router";
import { Bottom, Delete } from "@element-plus/icons-vue";
import { useCourseStore } from "@/stores/course";
import { usePaperStore } from "@/stores/paper";

const router = useRouter();
const courseStore = useCourseStore();
const paperStore = usePaperStore();
const courseId = useCourseStore().course.id;

const paperQuery = ref({
  currentPage: 1,
  pageSize: 4,
  id: "",
  type: "",
  courseId: courseStore.course.id,
  title: "",
  startTime: "",
  notEndTime: false,
});

const paperList = ref([
  {
    id: null,
    type: "",
    school: "",
    teacherName: "",
    courseName: "",
    title: "",
    sum: "",
    startTime: "",
    endTime: "",
    createTime: "",
  },
]);

const total = ref(0);

const findByPage = async () => {
  paperQuery.value.courseId = courseStore.course.id;
  const result = await findByPaperQueryService(paperQuery.value);
  paperList.value = result.data.data;
  total.value = result.data.data.length;
};

const clickDelete = (id) => {
  ElMessageBox.confirm("你确定要删除这个题集吗?", "温馨提示", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      await deletePaperService(id);
      await findByPage();
      ElMessage.success("删除成功");
    })
    .catch(() => {
      null;
    });
};

onMounted(async () => {
  $(".contactButton")
    .css({
      opacity: 0,
    })
    .animate(
      {
        opacity: 1,
        duration: 1000,
      },
      1000
    );
  await findByPage();
});

const goToPaper = (id) => {
  paperStore.setPaper(id);
  router.push("/tc-paper");
};

const dialogFormVisible = ref(false);

const paper = ref({
  title: "",
  type: "",
  startTime: null,
  endTime: null,
  courseId: null,
});
const time = ref<[Date, Date]>([new Date(), new Date()]);

const showForm = () => {
  close();
  dialogFormVisible.value = true;
};

const close = () => {
  paper.value = {
    title: "",
    type: "",
    startTime: null,
    endTime: null,
    courseId: null,
  };
};

const savePaper = async () => {
  if (paper.value.title.trim() == "") {
    ElMessage.error("请输入题集名称");
    return;
  }

  if (paper.value.type.trim() == "") {
    ElMessage.error("请选择题集类型");
    return;
  }

  if (time.value[1].valueOf() - time.value[0].valueOf() < 1000 * 3600) {
    ElMessage.error("起止时间不能小于1小时");
    return;
  }

  paper.value.startTime = time.value[0].toLocaleString();
  paper.value.endTime = time.value[1].toLocaleString();
  paper.value.courseId = courseId;
  const result = await savePaperService(paper.value);
  ElMessage.success(result.data.message ? result.data.message : "创建成功");
  dialogFormVisible.value = false;
  paper.value = {
    title: "",
    type: "",
    startTime: null,
    endTime: null,
    courseId: null,
  };

  await findByPage();
  goToPaper(paperList.value[0].id);
};
</script>

<template>
  <el-dialog v-model="dialogFormVisible" title="添加题集" width="800">
    <el-form :model="paper">
      <el-form-item label="标题">
        <el-input
          style="width: 720px"
          v-model="paper.title"
          autocomplete="off"
          maxlength="30"
          placeholder="请输入标题"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="类型">
        <el-select
          style="width: 720px"
          v-model="paper.type"
          placeholder="请选择类型"
        >
          <el-option label="作业" value="作业" />
          <el-option label="考试" value="考试" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间">
        <div class="block">
          <el-date-picker
            v-model="time"
            style="width: 720px"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="savePaper"> 提交 </el-button>
      </div>
    </template>
  </el-dialog>
  <el-card
    style="
      min-width: 500px;
      height: 80vh;
      position: relative;
      overflow-y: scroll;
      overflow-x: none;
      -ms-overflow-style: none;
      scrollbar-width: none;
      position: relative;
    "
    shadow="never"
  >
    <div v-if="total == 0">
      <el-empty :image-size="400" />
    </div>
    <el-card
      v-else
      style="margin-bottom: 10px; min-width: 500px"
      shadow="never"
      v-for="(paper, index) in paperList"
      :key="index"
    >
      <el-descriptions class="margin-top" :column="3">
        <template #title>
          <span
            style="
              display: inline-block;
              height: 20px;
              line-height: 20px;
              margin-right: 16px;
              padding-top: 4px;
            "
            >{{ paper.title }}</span
          >
          <el-tag style="margin-bottom: 3px" type="primary" size="small">{{
            paper.type
          }}</el-tag>
        </template>
        <template #extra>
          <el-button
            text
            bg
            :icon="Delete"
            plain
            circle
            type="danger"
            @click="clickDelete(paper.id)"
          />
          <el-button
            style="width: 64px"
            round
            type="primary"
            @click="goToPaper(paper.id)"
            ><el-icon><Edit /></el-icon
          ></el-button>
        </template>
        <el-descriptions-item label="课程">{{
          paper.courseName
        }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{
          paper.startTime
        }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{
          paper.endTime
        }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-card>
  <span>
    <button class="contactButton" @click="showForm">
      添加题集
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
      </div></button
  ></span>
</template>

<style lang="scss" scoped>
.example-pagination-block {
  float: right;
  margin-bottom: 16px;
}

.contactButton {
  position: fixed;
  display: flex;
  height: 32px;
  margin-left: 30px;
  padding: 0;
  padding-left: 1.2em;
  padding-right: 3.2em;
  font-size: 12px;
  font-weight: 500;
  border-radius: 0 0 7px 7px;
  border: none;
  cursor: pointer;
  letter-spacing: 0.05em;
  align-items: center;
  background: #7079f0;
  color: white;
  box-shadow: inset 0 0 1.6em -0.6em #714da6;
  overflow: hidden;
}

.iconButton {
  margin-left: 1em;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2em;
  width: 2em;
  border-radius: 6px;
  box-shadow: 0.1em 0.1em 0.6em 0.2em #7a8cf3;
  right: 0.3em;
  transition: all 0.3s;
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
