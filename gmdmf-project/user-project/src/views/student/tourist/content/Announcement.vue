<script lang="ts" setup>
import { onBeforeMount, onMounted, ref } from "vue";
import { findByCourseIdAnnouncementService } from "@/api/Announcement";
import { useCourseStore } from "@/stores/course";

onMounted(async () => {
  await getAnnouncementList();
});

const getAnnouncementList = async () => {
  const result = await findByCourseIdAnnouncementService(
    useCourseStore().course.id
  );
  announcementList.value = result.data.data;
};

const announcementList = ref([
  {
    id: "",
    courseName: "",
    title: "",
    content: "",
    teacherName: "",
    createTime: "",
  },
]);
</script>

<template>
  <div class="cell-item"  style="height: 82vh;">
    <el-timeline style="width: 84%; height: 100%; margin-top: 16px">
      <el-timeline-item
        center
        :timestamp="announcement.createTime"
        placement="top"
        v-for="(announcement, index) in announcementList"
        :key="index"
      >
        <el-card shadow="never">
          <h4 style="margin-top: 0.5rem">{{ announcement.title }}</h4>
          <p style="margin-top: 0.7rem">
            <span style="margin-right: 1rem"
              ><el-tag type="primary" size="small">{{
                announcement.courseName
              }}</el-tag></span
            >
            <span style="margin-right: 2rem">{{
              announcement.teacherName
            }}</span>
            <span style="color: #91979e">{{ announcement.createTime }}</span>
          </p>
          <div class="demo-collapse" style="margin-top: 1rem">
            <el-collapse>
              <el-collapse-item title="公告内容" name="1">
                <div style="padding: 0rem 2rem">
                  {{ announcement.content }}
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<style lang="scss" scoped>
.cell-item {
    padding-left: 10px;
  min-width: 400px;
  max-height: 600px;
  overflow-y: scroll;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
