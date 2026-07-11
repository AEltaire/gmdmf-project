<script lang="ts" setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const active = ref("/tourist-course/announcement");

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
    <el-menu-item index="/tourist-course/announcement">
      <el-icon><ChatLineSquare /></el-icon>
      <span>公告 Announcement</span>
    </el-menu-item>
    <el-menu-item index="/tourist-course/chapter">
      <el-icon><Management /></el-icon>
      <span>章节 Chapter</span>
    </el-menu-item>
    <el-menu-item index="/tourist-course/note">
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
