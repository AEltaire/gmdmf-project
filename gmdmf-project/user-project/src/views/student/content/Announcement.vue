<script lang="ts" setup>
import { onBeforeMount, onMounted, ref } from "vue";
import { findByStudentIdAnnouncementService } from "@/api/Announcement";

onMounted(async () => {
  await getAnnouncementList();
});

const getAnnouncementList = async () => {
  const result = await findByStudentIdAnnouncementService();
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
      <el-empty
      v-if="announcementList.length == 0"
      :image-size="400"
      style="width: 100%; align-items: center; height: 100%"
    />
   <div v-else class="cell-item" shadow="never">
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
