<script lang="ts" setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const active = ref("/stu-course/announcement");

watch(
  () => router.currentRoute.value.path,
  (toPath) => {
    active.value = toPath;
  },
  { immediate: true, deep: true }
);
</script>

<template>
  <el-menu :default-active="active" class="el-menu-vertical-demo" router="true">
    <el-menu-item index="/stu-course/announcement">
      <el-icon><ChatLineSquare /></el-icon>
      <span>公告 Announcement</span>
    </el-menu-item>
    <el-menu-item index="/stu-course/chapter">
      <el-icon><Management /></el-icon>
      <span>章节 Chapter</span>
    </el-menu-item>
    <el-menu-item index="/stu-course/score">
      <el-icon><Histogram /></el-icon>
      <span>成绩 Score</span>
    </el-menu-item>
    <el-menu-item index="/stu-course/work">
      <el-icon><EditPen /></el-icon>
      <span>作业 Work</span>
    </el-menu-item>
    <el-menu-item index="/stu-course/test">
      <el-icon><Monitor /></el-icon>
      <span>考试 Test</span>
    </el-menu-item>
    <el-menu-item index="/stu-course/note">
      <el-icon><Notebook /></el-icon>
      <span>笔记 Note</span>
    </el-menu-item>
    <el-menu-item index="/stu-course/chat">
      <el-icon><ChatDotRound /></el-icon>
      <span>AI Chat</span>
    </el-menu-item>
  </el-menu>
</template>

<style lang="scss" scoped>
.is-active {
  background-color: rgba(64, 158, 255, 0.15);
}
</style>
