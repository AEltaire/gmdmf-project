<script lang="ts" setup>
import { findByTeacherIdCourseService, saveCourseService } from "@/api/Course";
import { ref, onMounted, onBeforeMount } from "vue";
import { Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useCourseStore } from "@/stores/course";
import { useRouter } from "vue-router";
import { uploadImgService } from "@/api/Upload";
import { findAllMajorService } from "@/api/Major";

const router = useRouter();
const courseQuery = ref({
  currentPage: 1,
  pageSize: 3,
});

const courseDTO = ref({
  name: "",
  majorId: "",
  startTime: "",
  endTime: "",
  point: 1,
  introduce: "",
  picture: "",
  pub: "",
});

onMounted(async () => {
  await CourseFindByTeacherId();
  await findAllMajor();
});

const courseList = ref([
  {
    id: 0,
    name: "",
    majorName: "",
    teacherName: "",
    startTime: "",
    endTime: "",
    point: 0,
    introduce: "",
    picture: "",
    number: 0,
    assess: 0,
    love: 0,
    pub: "",
  },
]);

const majorList = ref([
  {
    id: 0,
    name: "",
  },
]);

const total = ref(0);

const handleCurrentChange = (val) => {
  courseQuery.value.currentPage = val;
  CourseFindByTeacherId();
};

const CourseFindByTeacherId = async () => {
  const result = await findByTeacherIdCourseService(courseQuery.value);
  courseList.value = result.data.data.list;
  total.value = result.data.data.total;
};

const findAllMajor = async () => {
  const result = await findAllMajorService();
  majorList.value = result.data.data;
};

const goCourse = (id) => {
  useCourseStore().setCourse({ id });
  router.push("/tea-course");
};

const createCourse = () => {
  useCourseStore().removeCourse();
  router.push("/t-course");
};
</script>

<template>
  <el-card
    style="min-width: 720px; height: 82vh; position: relative"
    shadow="never"
  >
    <div v-if="courseList.length == 0">
      <el-empty :image-size="400" />
    </div>
      <el-card
      v-else
        style="width: 100%; height: 100%; margin-bottom: 10px"
        shadow="never"
        v-for="course in courseList"
        :key="course.id"
      >
        <el-descriptions class="margin-top" :title="course.name" :column="3">
          <template #extra>
            <el-button
              type="primary"
              style="width: 72px"
              @click="goCourse(course.id)"
              round
              ><el-icon size="16"><Search /></el-icon
            ></el-button>
          </template>
          <el-descriptions-item label="教师名">{{
            course.teacherName
          }}</el-descriptions-item>
          <el-descriptions-item label="专业">
            {{ course.majorName }}
          </el-descriptions-item>
          <el-descriptions-item label="课程状态"
            ><el-tag size="small" style="margin-bottom: 5px">{{
              course.pub
            }}</el-tag></el-descriptions-item
          >

          <el-descriptions-item label="开课时间"
            >{{ course.startTime }} - {{ course.endTime }}</el-descriptions-item
          >
          <el-descriptions-item label="累计学习人数">{{
            course.number
          }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <div class="tools">
        <!-- 添加课程 -->
        <button class="contactButton" @click="createCourse">
          添加课程
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
        <div class="example-pagination-block" :hide-on-single-page="true">
          <el-pagination
            layout="prev, pager, next"
            class="page"
            :total="total"
            :page-size="3"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
  </el-card>
</template>

<style lang="scss" scoped>
.example-pagination-block {
  float: right;
  margin-bottom: 16px;
}

.contactButton {
  float: left;
  background: #7079f0;
  color: white;
  font-family: inherit;
  margin-left: 1px;
  padding: 0;
  padding-left: 1em;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
  box-shadow: inset 0 0 1.6em -0.6em #714da6;
  overflow: hidden;
  position: relative;
  height: 32px;
  padding-right: 3em;
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

.tools {
  position: absolute;
  width: 100%;
  padding-right: 32px;
  bottom: -6px;
}
</style>
