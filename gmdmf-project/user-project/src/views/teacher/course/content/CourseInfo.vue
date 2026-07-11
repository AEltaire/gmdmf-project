<script lang="ts" setup>
import { onMounted, ref, watch } from "vue";
import {
  DocumentAdd,
  DocumentChecked,
  DocumentDelete,
  Refresh,
  HomeFilled,
  UploadFilled,
  List,
  CloseBold,
  Select,
  SemiSelect,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox, genFileId } from "element-plus";
import type { CheckboxValueType, UploadInstance } from "element-plus";
import { findAllMajorService } from "@/api/Major";
import { findByCourseIdService, findByTeacherIdTeamService } from "@/api/Team";
import { findByIdCourseService, updateCourseService } from "@/api/Course";
import { useCourseStore } from "@/stores/course";
const checkedTeamList = ref<CheckboxValueType[]>([]);
const time = ref<[Date, Date]>([new Date(), new Date()]);
const file = ref([]);
const uploadRef = ref<UploadInstance>();
const token = localStorage.getItem("token");
const courseStore = useCourseStore();
const checkAll = ref(false);
const indeterminate = ref(false);
const fileList = ref([]);
const activeName = ref("1");
interface Course {
  id: number;
  name: string;
  majorId: number;
  majorName: string;
  startTime: string;
  endTime: string;
  point: number;
  introduce: string;
  picture: string;
  pub: string;
  teamIds: CheckboxValueType[];
}

// 课程信息表单
const courseDTO = ref<Course>({
  id: null,
  name: "",
  majorId: null,
  majorName: "",
  startTime: "",
  endTime: "",
  point: 1,
  introduce: "",
  picture: "",
  pub: "0",
  teamIds: [],
});
// 专业列表
const majorList = ref([
  {
    id: 0,
    name: "",
  },
]);

// 获取专业列表
const findAllMajor = async () => {
  const result = await findAllMajorService();
  majorList.value = result.data.data;
};

// 班级列表
const teamList = ref([
  {
    id: null,
    name: "",
    departmentId: null,
    majorId: null,
    endTime: "",
  },
]);

// 获取班级列表
const findByTeacherIdTeam = async () => {
  const result = await findByTeacherIdTeamService();
  teamList.value = result.data.data;
};

const findByIdCourse = async (id) => {
  const result = await findByIdCourseService(id);
  
  courseDTO.value = {
    id: result.data.data.id,
    name: result.data.data.name,
    majorId: result.data.data.majorId,
    majorName: result.data.data.majorName,
    startTime: result.data.data.startTime,
    endTime: result.data.data.endTime,
    point: result.data.data.point,
    introduce: result.data.data.introduce,
    picture: result.data.data.picture,
    pub: result.data.data.pub,
    teamIds: [],
  };
  time.value = [
    new Date(result.data.data.startTime),
    new Date(result.data.data.endTime),
  ];
  
};

const findByCourseIdTeam = async (id) => {
  const result = await findByCourseIdService(id);
  checkedTeamList.value = result.data.data.map((item) => item.id);
  courseDTO.value.teamIds = checkedTeamList.value;
};

onMounted(async () => {
  await findAllMajor();
  await findByTeacherIdTeam();
  await findByIdCourse(courseStore.course.id);
  await findByCourseIdTeam(courseStore.course.id);
});

const handleCheckAll = (val: CheckboxValueType) => {
  indeterminate.value = false;
  if (val) {
    checkedTeamList.value = teamList.value.map((_) => _.id);
  } else {
    checkedTeamList.value = [];
  }
};

// 数据填充
const saveCourse = async () => {
  if (checkCourseInfo()) return;

  courseDTO.value.startTime =
    time.value[0].toLocaleDateString() +
    " " +
    time.value[0].toLocaleTimeString();

  courseDTO.value.endTime =
    time.value[1].toLocaleDateString() +
    " " +
    time.value[0].toLocaleTimeString();

  courseDTO.value.pub = "0";
  courseDTO.value.teamIds = checkedTeamList.value;

  if (file.value.length != 0) {
    uploadRef.value!.submit();
  } else {
    courseDTO.value.picture = null;
    await updateCourseInfo();
  }
};

const successUploadFile = async (response) => {
  courseDTO.value.picture = response.data;
  await updateCourseInfo();
};

// 修改课程信息
const updateCourseInfo = async () => {
  const result = await updateCourseService(courseDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
};

const checkCourseInfo = () => {
  if (courseDTO.value.name.trim() == "") {
    ElMessage.error("课程标题不能为空");
    return true;
  }

  if (courseDTO.value.majorId == null) {
    ElMessage.error("课程专业不能为空");
    return true;
  }

  if (courseDTO.value.point == null) {
    ElMessage.error("课程学分不能为空");
    return true;
  }

  if (checkedTeamList.value.length == 0) {
    ElMessage.error("课程班级不能为空");
    return true;
  }

  // 计算课程时间
  const startTime = new Date(time.value[0]);
  const endTime = new Date(time.value[1]);
  const timeDiff = endTime.getTime() - startTime.getTime();

  if (timeDiff <= 7 * 24 * 60 * 60 * 1000 - 1) {
    ElMessage.error("课程时间不能少于7天");
    return true;
  }

  if (courseDTO.value.introduce == "") {
    ElMessage.error("课程介绍不能为空");
    return true;
  }

  if (file.value == null && courseDTO.value.picture == "") {
    ElMessage.error("请上传图片");
    return true;
  }

  if (
    courseDTO.value.picture == "" &&
    file.value[0].raw.type != "image/png" &&
    file.value[0].raw.type != "image/jpg" &&
    file.value[0].raw.type != "image/jpeg"
  ) {
    ElMessage.warning("请上传一个 PNG/JPG 格式的文件，大小不超过 10 MB");
    fileList.value = [];
    return true;
  }

  return false;
};

watch(checkedTeamList, (val) => {
  if (val.length === 0) {
    checkAll.value = false;
    indeterminate.value = false;
  } else if (val.length === teamList.value.length) {
    checkAll.value = true;
    indeterminate.value = false;
  } else {
    indeterminate.value = true;
  }
});
</script>

<template>
  <el-card
    style="
      height: 82vh;
      overflow-y: scroll;
      overflow-x: none;
      -ms-overflow-style: none;
      scrollbar-width: none;
    "
    shadow="never"
  >
    <div class="intro">
      <h1>
        课程信息
        <span style="float: right; display: flex; align-items: center">
          <el-button
            style="margin: 0"
            type="success"
            circle
            size="small"
            :icon="Select"
            @click="saveCourse"
            class="select"
          />
        </span>
      </h1>
      <!-- 课程信息 -->
      <el-divider style="margin: 20px 0" />
      <div class="content">
        <el-form :model="courseDTO" label-width="auto" style="max-width: 720px">
          <el-form-item class="form-item" label="课程标题">
            <el-input
              class="title-input"
              v-model="courseDTO.name"
              show-word-limit
              maxlength="30"
              disabled
            />
          </el-form-item>
          <el-form-item class="form-item" label="专业名称" prop="region">
            <el-select
              v-model="courseDTO.majorId"
              placeholder="请选择专业"
              class="title-input"
              filterable
              disabled
            >
              <el-option
                :label="major.name"
                :value="major.id"
                v-for="(major, index) in majorList"
                style="padding-left: 10px"
                :key="index"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="form-item" label="课程学分">
            <el-input-number
              class="title-input"
              v-model="courseDTO.point"
              :step="0.5"
              :min="1"
              :max="20"
              disabled
            />
          </el-form-item>

          <el-form-item class="form-item" label="上课班级">
            <el-select
              v-model="checkedTeamList"
              multiple
              clearable
              collapse-tags
              filterable
              placeholder="选择班级"
              popper-class="custom-header"
              :max-collapse-tags="5"
              style="width: 100%"
              class="title-input"
              disabled
            >
              <template #header>
                <el-checkbox
                  v-model="checkAll"
                  :indeterminate="indeterminate"
                  @change="handleCheckAll"
                >
                  全选
                </el-checkbox>
              </template>
              <el-option
                v-for="item in teamList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
                style="padding-left: 12px"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="form-item" label="开课时间">
            <el-date-picker
              v-model="time"
              class="time-input"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            />
          </el-form-item>
          <el-form-item class="form-item" label="课程封面">
            <el-upload
              ref="uploadRef"
              class="upload-input"
              drag
              action="/api/uploadImg"
              multiple
              style="width: 100%"
              v-model:file-list="file"
              :show-file-list="true"
              :auto-upload="false"
              :limit="1"
              :on-success="successUploadFile"
              :headers="{ Authorization: token }"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">拖拽上传 or <em>点击上传</em></div>
              <template #tip>
                <div class="el-upload__tip">
                  jpg/png 文件大小请不要超过 10 MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item
            class="form-item"
            label="课程简介"
          >
            <el-input
              class="textarea-input"
              resize="none"
              rows="10"
              v-model="courseDTO.introduce"
              type="textarea"
              maxlength="1000"
              placeholder="请输入课程简介 [1000字以内]"
              show-word-limit
              input-style="{
                overflow-y: scroll !important;
                overflow-x: none !important;
                -ms-overflow-style: none !important;
                scrollbar-width: none !important;  
              }"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.content {
  margin: auto;
  width: 64%;
}
</style>
