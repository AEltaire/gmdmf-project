<script setup lang="ts">
import { ref, watchEffect, onMounted, computed, defineComponent } from "vue";
import {
  findAllApprovalCourseService,
  updateListCourseService,
  deleteCourseListService,
} from "@/api/Course";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { useCourseStore } from "@/stores/course";
const currentPage = ref(1);
const pageSize = ref(1);
const total = ref(0);
const router = useRouter();
const store = useStore();

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
  picture: string;
  exhibition: boolean;
}

const courseVO = ref<Course>({
  id: 0,
  name: "",
  majorName: "",
  teacherName: "",
  point: 0,
  introduce: "",
  startTime: new Date(),
  endTime: new Date(),
  love: 0,
  assess: 0,
  number: 0,
  pub: "",
  picture: null,
  exhibition: false,
});

const courseList = ref<Course[]>([]);

onMounted(async () => {
  findAllCourse();
});

const findAllCourse = async () => {
  const result = await findAllApprovalCourseService();
  courseList.value = result.data.data;
  if (courseList.value != null) {
    courseVO.value = result.data.data[0];
    total.value = result.data.data.length;
  } else {
    courseVO.value = null;
    total.value = 0;
  }
};

const deleteCourse = async (id: number) => {
  const result = await deleteCourseListService(id);
  if (result.data.code == 200) {
    findAllCourse();
  }
};

const onPageChange = () => {
  courseVO.value = courseList.value[currentPage.value - 1];
};

const goVideo = () => {
  useCourseStore().setCourse(courseVO.value);
  router.push("/video");
};
</script>

<template>
  <VaCard style="height: 79vh; position: relative">
    <VaCardContent v-if="total == 0">
      <el-empty :image-size="360" />
    </VaCardContent>
    <VaCardContent v-else>
      <h6 class="va-h6">
        课程信息

        <div class="tools">
          <VaButton
            @click="goVideo"
            style="width: 72px"
            preset="primary"
            class="mr-6 mb-2"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              height="24px"
              viewBox="0 -960 960 960"
              width="24px"
              :fill="store.state.isLightTheme ? '#5f6368' : 'white'"
            >
              <path
                d="m618-358 44-42-84-84q11-17 16.5-36t5.5-40q0-59-41.5-99.5T460-700q-57 0-98.5 40.5T320-560q0 59 41.5 99.5T460-420q21 0 39.5-5.5T536-442l82 84ZM460-480q-33 0-56.5-23.5T380-560q0-33 23.5-56.5T460-640q32 0 56 23.5t24 56.5q0 33-23.5 56.5T460-480ZM160-240q-33 0-56.5-23.5T80-320v-440q0-33 23.5-56.5T160-840h640q33 0 56.5 23.5T880-760v440q0 33-23.5 56.5T800-240H160Zm0-80h640v-440H160v440Zm0 0v-440 440ZM40-120v-80h880v80H40Z"
              />
            </svg>
          </VaButton>
        </div>
      </h6>
      <div class="my-8" style="margin: 20px 0">
        <VaDivider />
      </div>
      <div class="row">
        <div class="flex flex-col md5">
          <div class="img">
            <VaImage
              fit="fill"
              class="w-full md:w-1/2 lg:w-1/3"
              :src="courseVO.picture"
              style="max-height: 64vh"
            />
          </div>
        </div>
        <div class="flex flex-col md7" style="width: 90vw">
          <div
            style="
              width: 100%;
              margin-top: 3px;
              display: flex;
              justify-content: center;
              align-items: center;
            "
          >
            <VaInput
              style="width: 92%"
              v-model="courseVO.name"
              label="课程名"
              readonly
            />
          </div>
          <div
            style="
              width: 100%;
              margin-top: 15px;
              display: flex;
              justify-content: center;
              align-items: center;
            "
          >
            <VaInput
              style="width: 92%"
              v-model="courseVO.teacherName"
              label="教师名"
              readonly
            />
          </div>
          <div
            style="
              width: 100%;
              margin-top: 15px;
              display: flex;
              justify-content: center;
              align-items: center;
            "
          >
            <VaInput
              style="margin-right: 1%; width: 30%"
              v-model="courseVO.majorName"
              label="专业"
              readonly
            />
            <VaInput
              style="margin-right: 1%; width: 30%"
              v-model="courseVO.point"
              label="学分"
              readonly
            />
            <VaInput
              style="width: 30%"
              v-model="courseVO.number"
              label="人数"
              readonly
            />
          </div>
          <div
            style="
              width: 100%;
              margin-top: 15px;
              display: flex;
              justify-content: center;
              align-items: center;
            "
          >
            <VaInput
              style="width: 45.5%; margin-right: 1%"
              label="开课时间"
              readonly
              >{{ courseVO.startTime }}</VaInput
            >
            <VaInput style="width: 45.5%" label="结课时间" readonly>{{
              courseVO.endTime
            }}</VaInput>
          </div>
          <div
            style="
              width: 100%;
              margin-top: 20px;
              display: flex;
              justify-content: center;
              align-items: center;
            "
          >
            <VaTextarea
              style="width: 92%; height: 168px"
              v-model="courseVO.introduce"
              label="课程简介"
              readonly
              :resize="false"
            />
          </div>
        </div>
      </div>
    </VaCardContent>
  </VaCard>
  <VaPagination
    v-model="currentPage"
    :visible-pages="7"
    :total="total"
    :page-size="pageSize"
    boundary-numbers
    class="mb-3 justify-center sm:justify-start page"
    @update:modelValue="onPageChange"
  >
    <template #prevPageLink="{ onClick, disabled }">
      <VaButton
        preset="primary"
        :disabled="disabled"
        aria-label="go prev page"
        @click="onClick"
      >
        Previous
      </VaButton>
    </template>
    <template #nextPageLink="{ onClick, disabled }">
      <VaButton
        preset="primary"
        :disabled="disabled"
        aria-label="go next page"
        @click="onClick"
      >
        Next
      </VaButton>
    </template>
  </VaPagination>
</template>

<style lang="css" scoped>
.va-table-responsive {
  overflow: auto;
}

.page {
  position: relative;
  top: 17px;
}
.img {
  border: 1px solid #dee5f2;
  border-radius: 6px;
  overflow: hidden;
}

.tools {
  position: absolute;
  top: 18px;
  right: 20px;
  float: right;
}
</style>
