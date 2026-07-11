<script lang="ts" setup>
import { onBeforeMount, onMounted, ref } from "vue";
import { useCourseStore } from "@/stores/course";
import {
  saveAnnouncementService,
  updateAnnouncementService,
  deleteAnnouncementService,
  findByIdAnnouncementService,
  findByCourseIdAnnouncementService,
} from "@/api/Announcement";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import $ from "jquery";

const isNotData = ref(true);
const dialogFormVisible = ref(false);
const title = ref("发布公告");

const announcementDTO = ref({
  id: null,
  courseId: "",
  title: "",
  content: "",
});

const announcementList = ref([
  {
    id: 0,
    courseName: "",
    title: "",
    content: "",
    teacherName: "",
    createTime: "",
  },
]);

const clean = () => {
  announcementDTO.value = {
    id: null,
    courseId: "",
    title: "",
    content: "",
  };
};

const handleSave = async () => {
  title.value == "发布公告" ? "" : clean();
  title.value = "发布公告";
  dialogFormVisible.value = true;
};

const saveAnnouncement = async () => {
  announcementDTO.value.courseId = useCourseStore().course.id;
  const result = await saveAnnouncementService(announcementDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "发布公告成功");
  await findAllAnnouncement();
  dialogFormVisible.value = false;
  clean();
};

const handleDelete = async (id) => {
  ElMessageBox.confirm("确定要删除该公告?", "温馨提示", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const result = await deleteAnnouncementService(id);
      ElMessage.success(
        result.data.message ? result.data.message : "删除公告成功"
      );
      await findAllAnnouncement();
    })
    .catch(() => {
      null;
    });
};

const handleUpdate = async (id) => {
  title.value = "修改公告";
  await findByIdAnnouncement(id);
  dialogFormVisible.value = true;
};

const updateAnnouncement = async () => {
  const result = await updateAnnouncementService(announcementDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "修改公告成功");
  await findAllAnnouncement();
  dialogFormVisible.value = false;
};

const findByIdAnnouncement = async (id) => {
  const result = await findByIdAnnouncementService(id);
  announcementDTO.value = result.data.data;
  announcementDTO.value.courseId = useCourseStore().course.id;
};

const findAllAnnouncement = async () => {
  const courseId = useCourseStore().course.id;
  const result = await findByCourseIdAnnouncementService(courseId);
  announcementList.value = result.data.data;
  if (result.data.data != "查询记录为空") isNotData.value = false;
};

onMounted(async () => {
  await findAllAnnouncement();
  await setTimeout(() => {
    loading.value = true;
    $(".save").animate(
      {
        opacity: 1,
      },
      100,
      "linear"
    );
  }, 500);
});

const saveAnimate = (e) => {
  const save = document.querySelector(".save");
  let x = e.clientX - save.getBoundingClientRect().left;
  let y = e.clientY - save.getBoundingClientRect().top; // 边距 = 相对 - 相对

  $(".svg-1").stop(true, false);
  $(".svg-2").stop(true, false);

  $(".svg-1")
    .css({
      top: y + "px",
      left: x + "px",
      width: "0px",
      height: "0px",
      opacity: 1,
    })
    .animate(
      {
        width: "100%",
        height: "100%",
        opacity: 0,
        top: 0,
        left: 0,
      },
      250,
      "linear",
      function () {
        $(".svg-1").css({
          width: "0",
          height: "0",
          opacity: 1,
        });
      }
    );

  $(".svg-2")
    .css({
      top: y + "px",
      left: x + "px",
      width: "0px",
      height: "0px",
      opacity: 1,
    })
    .animate(
      {
        width: "100%",
        height: "100%",
        opacity: 0,
        top: 0,
        left: 0,
      },
      400,
      "linear",
      function () {
        $(".svg-2").css({
          width: "0",
          height: "0",
          opacity: 1,
        });
        handleSave();
      }
    );
};

const loading = ref(false);
</script>

<template>
  <el-card shadow="never" style="min-width: 520px; height: 82vh">
    <el-dialog v-model="dialogFormVisible" :title="title" width="600">
      <el-form :model="announcementDTO" label-width="auto" style="width: 550px">
        <el-form-item label="标题">
          <el-input
            maxlength="30"
            placeholder="请输入标题..."
            show-word-limit
            v-model="announcementDTO.title"
          />
        </el-form-item>
        <el-form-item label="内容" style="width: 550px">
          <el-input
            v-model="announcementDTO.content"
            maxlength="200"
            style="width: 550px"
            placeholder="请输入公告内容..."
            show-word-limit
            resize="none"
            rows="4"
            type="textarea"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            style="margin-left: 490px"
            type="primary"
            @click="
              title == '修改公告' ? updateAnnouncement() : saveAnnouncement()
            "
            >{{ title == "修改公告" ? "修改" : "发布" }}</el-button
          >
        </el-form-item>
      </el-form>
    </el-dialog>
    <button v-show="loading" class="save" @click="saveAnimate">
      <span class="svg-1"></span>
      <span class="svg-2"></span>
      <el-icon class="search-icon" size="18" color="#fff"><Plus /></el-icon>
    </button>
    <div v-if="isNotData">
      <el-empty :image-size="400" />
    </div>
    <div v-else class="cell-item">
      <el-timeline style="width: 90%; margin-top: 16px; margin-left: 16px">
        <el-timeline-item
          v-for="(announcement, index) in announcementList"
          center
          :timestamp="announcement.createTime"
          placement="top"
          :key="index"
        >
          <el-card class="card-item" shadow="never">
            <el-descriptions
              class="margin-top"
              :title="announcement.title"
              :column="1"
            >
              <template #extra>
                <el-button
                  text
                  bg
                  :icon="Delete"
                  plain
                  circle
                  type="danger"
                  @click="handleDelete(announcement.id)"
                />
                <el-button
                  style="width: 64px"
                  round
                  type="primary"
                  @click="handleUpdate(announcement.id)"
                  ><el-icon><Edit /></el-icon
                ></el-button>
              </template>
              <el-descriptions-item>
                <div style="padding: 0rem 1rem">
                  {{ announcement.content }}
                </div>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.cell-item {
  height: 80vh;
  overflow-y: scroll;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
  padding: 0px;
}

.margin-top {
  margin: 0px;
}

.save {
  position: fixed;
  opacity: 0;
  top: 80px;
  right: 100px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #7077ec;
  border: none;
  box-shadow: 0px 0px 4px 1px rgb(221.7, 222.6, 224.4);
  // 图标居中
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 99;
  transition: all 0.3s;

  .svg-1 {
    position: absolute;
    width: 0px;
    height: 0px;
    background-color: #8086f1;
    border-radius: 50%;
    opacity: 0;
  }

  .svg-2 {
    position: absolute;
    width: 5px;
    height: 5px;
    background-color: #989dfa;
    border-radius: 50%;
    opacity: 0;
  }
}

.save:active {
  // 动画 变小
  transform: scale(0.9);
}
</style>
