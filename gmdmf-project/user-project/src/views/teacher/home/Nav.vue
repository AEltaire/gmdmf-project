<script lang="ts" setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const active = ref("/tc");

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
        <el-menu-item index="/tc">
          <el-icon><DataAnalysis /></el-icon>
          <span>课程 Course</span>
        </el-menu-item>
        <el-menu-item index="/th">
          <el-icon><DocumentCopy /></el-icon>
          <span>题库 Warehouse</span>
        </el-menu-item>
        <el-menu-item index="/tw">
          <el-icon><EditPen /></el-icon>
          <span>作业 Work</span>
        </el-menu-item>
        <el-menu-item index="/te">
          <el-icon><Monitor /></el-icon>
          <span>考试 Test</span>
        </el-menu-item>
        <el-menu-item index="/tt">
          <el-icon><User /></el-icon>
          <span>班级 Team</span>
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
