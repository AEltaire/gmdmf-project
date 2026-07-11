<script lang="ts" setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const active = ref("/sc");

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
    <el-menu-item index="/sc">
      <el-icon><Management /></el-icon>
      <span>课程 Course</span>
    </el-menu-item>
    <el-menu-item index="/sa">
      <el-icon><ChatLineSquare /></el-icon>
      <span>公告 Announcement</span>
    </el-menu-item>
    <el-menu-item index="/sw">
      <el-icon><EditPen /></el-icon>
      <span>作业 Work</span>
    </el-menu-item>
    <el-menu-item index="/st">
      <el-icon><Monitor /></el-icon>
      <span>考试 Test</span>
    </el-menu-item>
    <el-menu-item index="/sn">
      <el-icon><Notebook /></el-icon>
      <span>笔记 Note</span>
    </el-menu-item>
  </el-menu>
</template>
<style lang="scss" scoped>
.is-active {
  background-color: rgba(64, 158, 255, 0.15);
}
</style>
