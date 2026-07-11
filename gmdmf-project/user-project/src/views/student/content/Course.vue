<script lang="ts" setup>
import { ref, onMounted, onBeforeMount } from "vue";
import { useRouter } from "vue-router";
import { findByStudentIdCourseService } from "@/api/Course";
import { useCourseStore } from "@/stores/course";
import { findAllLovesService, deleteLovesService } from "@/api/Loves";
import { findAllTouristService, deleteTouristService } from "@/api/Tourist";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const courseStore = useCourseStore();
const group = ref("课程");
const showDelete = ref(false);

const courseList = ref([
  {
    id: 1,
    name: "Java编程入门",
    majorName: "软件技术",
    teacherName: "李四",
    startTime: "2024/03/01",
    endTime: "2025/03/01",
    point: 4,
    introduce: "Java编程入门",
    picture: "",
    number: 0,
    assess: 5,
    love: 0,
    pub: true,
  },
]);

const goCourse = (val) => {
  courseStore.setCourse({ id: val });
  if (group.value == "游客") {
    router.push({ path: "/tourist-course" });
  } else {
    router.push({ path: "/stu-course" });
  }
};

const getCourseList = async () => {
  const result = await findByStudentIdCourseService();
  courseList.value = result.data.data;
};

const findAllLoves = async () => {
  const result = await findAllLovesService();
  courseList.value = result.data.data;
};

const findAllTourist = async () => {
  const result = await findAllTouristService();
  courseList.value = result.data.data;
};

const changeGroup = async () => {
  if (group.value == "课程") {
    await getCourseList();
  } else if (group.value == "游客") {
    await findAllTourist();
  } else if (group.value == "喜爱") {
    await findAllLoves();
  }
  showDelete.value = group.value == "喜爱" || group.value == "游客";
};

const deleteCourse = async (id) => {
  ElMessageBox.confirm("确定要取消吗?", "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "稍等",
    type: "warning",
  })
    .then(async () => {
      if (group.value == "游客") {
        const result = await deleteTouristService(id);
        ElMessage.success(
          result.data.message ? result.data.message : "退出学习"
        );
      } else if (group.value == "喜爱") {
        const result = await deleteLovesService(id);
        ElMessage.success(
          result.data.message ? result.data.message : "取消喜欢"
        );
      }
      changeGroup();
    })
    .catch(() => {
      null;
    });
};

onMounted(async () => {
  changeGroup();
});
</script>

<template>
  <el-card shadow="never" style="width: 100%; min-width: 800px; height: 83vh">
    <div v-if="courseList.length == 0" style="height: 75vh">
      <el-empty :image-size="400" />
    </div>
    <el-carousel
      v-else
      height="75vh"
      type="card"
      :loop="false"
      :autoplay="false"
      initial-index="1"
      style="margin: auto"
    >
      <el-carousel-item v-for="course in courseList" :key="course.id">
        <div class="project-info">
          <div class="img" style="align-items: center; justify-content: center">
            <img
              style="
                min-width: 100%;
                width: 100%;
                height: 100%;
                border-radius: 10px;
              "
              :src="course.picture"
              alt=""
            />
          </div>
          <button
            v-show="showDelete"
            class="del"
            @click="deleteCourse(course.id)"
          >
            <el-icon>
              <CloseBold />
            </el-icon>
          </button>
          <el-descriptions class="margin-top" :title="course.name" :column="1">
            <template #extra>
              <el-tag size="small">开课中</el-tag>
            </template>
            <el-descriptions-item label="讲师">{{
              course.teacherName
            }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{
              course.majorName
            }}</el-descriptions-item>
            <el-descriptions-item label="时间"
              >{{ course.startTime }} -
              {{ course.endTime }}</el-descriptions-item
            >
            <el-descriptions-item>
              <span
                style="background: #aedeff; color: #205af0"
                class="project-type"
              >
                <el-icon size="14px" class="project-type-icon"
                  ><Reading />
                </el-icon>
                <span class="project-type-span">{{ course.point }}</span>
              </span>
              <span
                style="
                  background-color: rgba(165, 96, 247, 0.43);
                  color: rgb(85, 27, 177);
                "
                class="project-type"
              >
                <el-icon size="14px" class="project-type-icon"
                  ><Star
                /></el-icon>
                <span class="project-type-span">{{ course.assess }}</span>
              </span>
              <span
                style="background: #b2b2fd; color: #1a41cd"
                class="project-type"
              >
                <el-icon size="14px" class="project-type-icon"
                  ><User
                /></el-icon>
                <span class="project-type-span">{{ course.number }}</span>
              </span>
            </el-descriptions-item>
          </el-descriptions>
          <button class="study" @click="goCourse(course.id)">学习</button>
        </div>
      </el-carousel-item>
    </el-carousel>
    <VaButtonToggle
      preset="primary"
      class="button-group"
      v-model="group"
      :options="[
        { label: '喜爱', value: '喜爱' },
        { label: '游客', value: '游客' },
        { label: '课程', value: '课程' },
      ]"
      @update:modelValue="changeGroup"
    />
  </el-card>
</template>

<style lang="scss" scoped>
.button-group {
  float: right;
}
// 卡片
.project-info {
  position: relative;
  align-items: center;
  justify-content: center;
  justify-items: center;
  margin-left: 17%;
  margin-top: 1.8%;
  width: 67%;
  min-width: 240px;
  height: 98%;
  border: 2.4px solid rgb(162, 155, 254, 0.8);
  border-radius: 16px;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 2px 4px 0px;
  background-color: #fff;
}

.margin-top {
  position: absolute;
  top: 35%;
  width: 82%;
  margin: 10% 0% 0% 10%;
}

// 上课照片
.img {
  position: absolute;
  margin: 6% 8%;
  width: 84%;
  height: 30%;
  background: white;
  border: 3px dashed rgb(162, 155, 254, 0.6);
  border-radius: 12px;
}

// 课程详情
.project-type {
  display: inline-block;
  margin-top: 10px;
  margin-right: 8px;
  width: auto;
  // height: auto;
  height: 24px;
  font-weight: bold;
  padding: 0 0.8rem;
  border-radius: 15px;
  font-size: 14px;
  letter-spacing: -0.6px;
  align-items: center;
  line-height: 10px;
}

.project-type-icon {
  float: left;
  margin-right: 6px;
  margin-top: 5px;
}

.project-type-span {
  display: inline-block;
  height: 16px;
  font-size: 13px;
  line-height: 25px;
}

.project-content {
  position: absolute;
  margin: 0;
  padding: 0;
  width: 84%;
  height: 42%;
  top: 38%;
  margin-left: 8%;
  // background-color: blue;
}

// 表格
.table {
  width: 84%;
  // border: 1px solid black;
}

.table tr,
th {
  height: 25px;
  // background-color: pink;
  text-align: left;
  font-size: 18px;
}

.table th {
  height: 30px;
}

.table td {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgb(62, 61, 61);
  font-family: "宋体";
  font-size: 16px;
}

.intro {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  margin: 0.2rem;
  font-size: 14px;
  width: 100%;
  line-height: 1.25rem;
}

// 走马灯
.el-carousel__item:nth-child(2n) {
  border-radius: 16px;
}

.el-carousel__item:nth-child(2n + 1) {
  border-radius: 16px;
}

.carousel-item {
  // color: #475669;
  opacity: 0.75;
  margin: 0;
  text-align: center;
}

// 退出
.del {
  position: absolute;
  border: none;
  outline: none;
  top: 6%;
  right: 2%;
  width: 36px;
  height: 24px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 8px;
  background-color: rgb(255, 56, 86, 1);
  letter-spacing: 2px;
  transition: all 0.3s ease;
  box-shadow: rgb(242, 126, 144) 0px 3.6px 0px 0px;
  color: hsl(0, 0%, 100%);
  cursor: pointer;
  z-index: 100;
}

.del:active {
  background-color: rgb(255, 56, 86);
  /*50, 168, 80*/
  box-shadow: rgb(201, 46, 70) 0px 0px 0px 0px;
  transform: translateY(5px);
  transition: 200ms;
}

// 学习
.study {
  position: absolute;
  margin: 0% 8%;
  bottom: 10%;
  width: 84%;
  height: 36px;
  color: #fff;
  border: none;
  border-radius: 5px;
  outline: none;
  font-size: 12px;
  letter-spacing: 2px;
  font-weight: 700;
  box-shadow: 0px 6px 0px 0px #a29bfe;
  background-color: #6c5ce7;
  transition: all ease 0.1s;
  cursor: pointer;
}

.study:active {
  transform: translateY(5px);
  box-shadow: 0px 0px 0px 0px #a29bfe;
}

.el-carousel__button {
  background-color: #000 !important;
}

.el-carousel__arrow {
  background-color: #000 !important;
}

.el-carousel__arrow i {
  color: #fff !important;
}

.el-carousel__indicators {
  width: 100%;
}

.el-carousel__indicator {
  width: 10%;
}
</style>
