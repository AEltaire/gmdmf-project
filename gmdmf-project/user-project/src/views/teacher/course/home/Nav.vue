<script lang="ts" setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const active = ref("/tea-course/chapter");

watch(
  () => router.currentRoute.value.path,
  (toPath) => {
    active.value = toPath;
  },
  { immediate: true, deep: true }
);
</script>

<template>
  <el-row class="tac">
    <el-col>
      <el-menu
        :default-active="active"
        class="el-menu-vertical-demo"
        router="true"
      >
        <el-menu-item index="/tea-course/chapter">
          <el-icon><Management /></el-icon>
          <span>章节 Chapter</span>
        </el-menu-item>
        <el-menu-item index="/tea-course/announcement">
          <el-icon><ChatLineSquare /></el-icon>
          <span>公告 Announcement</span>
        </el-menu-item>
        <el-menu-item index="/tea-course/team">
          <el-icon><User /></el-icon>
          <span>班级 Team</span>
        </el-menu-item>
        <el-menu-item index="/tea-course/score">
          <el-icon><Histogram /></el-icon>
          <span>成绩 Score</span>
        </el-menu-item>
        <el-menu-item index="/tea-course/paper">
          <el-icon><Document /></el-icon>
          <span>题集 Paper</span>
        </el-menu-item>
        <el-menu-item index="/tea-course/work">
          <el-icon><EditPen /></el-icon>
          <span>作业 Work</span>
        </el-menu-item>
        <el-menu-item index="/tea-course/test">
          <el-icon><Monitor /></el-icon>
          <span>考试 Test</span>
        </el-menu-item>
      </el-menu>
    </el-col>
  </el-row>
</template>

<style lang="scss" scoped>
.is-active {
  background-color: rgba(64, 158, 255, 0.15);
}
</style>
