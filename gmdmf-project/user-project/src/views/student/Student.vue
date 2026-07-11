<script lang="ts" setup>
import Content from "@/views/student/home/Content.vue";
import NavVue from "@/views/student/home/Nav.vue";
import Footer from "@/views/student/home/Footer.vue";

import { onMounted, ref } from "vue";
import { useCourseStore } from "@/stores/course";
import { useUserInfoStore } from "@/stores/userInfo";
import { useTokenStore } from "@/stores/token";
import { usePaperStore } from "@/stores/paper";
import { useNodeStore } from "@/stores/node";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const router = useRouter();

const loginOut = async () => {
  await useCourseStore().removeCourse();
  await useUserInfoStore().removeUserInfo();
  await useTokenStore().removeToken();
  await usePaperStore().removePaper();
  await useNodeStore().removeNode();
  await localStorage.removeItem("token");
  router.push("/login");
  ElMessage.success("退出成功");
};
</script>

<template>
  <el-card class="card" style="border-radius: 20px">
    <div class="common-layout">
      <el-container>
        <el-aside width="210px" style="height: 94vh">
          <div class="header">
            <el-button class="button" @click="loginOut" type="danger" text>
              <el-icon size="20"><SwitchButton /></el-icon>
            </el-button>
            <div class="avatar-box">
              <el-avatar
                class="avatar"
                :src="
                  useUserInfoStore().userInfo.picture
                    ? useUserInfoStore().userInfo.picture
                    : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                "
              />
            </div>
          </div>
          <div class="main">
            <NavVue />
          </div>
          <div class="footer">
            <Footer />
          </div>
        </el-aside>
        <el-main class="right">
          <el-container>
            <el-main style="height: 100%">
              <Content />
            </el-main>
          </el-container>
        </el-main>
      </el-container>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.card {
  --el-card-padding: 0;
  --va-card-padding: 0;
  width: 96vw;
  height: 94vh;
  margin: 20px 0 0 20px;
}

.header {
  position: relative;
  width: 100%;
  height: 24vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.main {
  height: 60vh;
  width: 100%;
}

.footer {
  height: 10vh;
  padding-top: 5px;
  display: flex;
  justify-content: center;
  margin-right: 5px;
}

.avatar-box {
  display: flex;
  justify-content: center;
  align-items: center;
}

// 设置头像样式
.avatar {
  margin-top: 12px;
  width: 100px;
  height: 100px;
  border: 2px dashed rgb(159.5, 206.5, 255);
}

.right {
  border-left: 2px dashed #a59d9d;
  border-radius: 0px 20px 20px 0px;
  background-color: rgba(236, 245, 255, 0.5);
}

.fgx {
  border-top: 2px dashed #d0cbcb;
}

// 通知
.button {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 36px;
  height: 36px;
  align-items: center;
  justify-content: center;
  background-color: rgb(254, 240.3, 240.3);
  border-radius: 50%;
  cursor: pointer;
  transition-duration: 0.3s;
  box-shadow: 1px 1px 3px 1px rgba(0, 0, 0, 0.15);
  border: none;
}

.button .bell {
  width: 16px;
}
.bell {
  width: 16px;
}

.bell path {
  fill: white;
}

.button:hover {
  background-color: rgb(56, 56, 56);
}

.button:hover .bell {
  animation: bellRing 0.9s both;
}

@keyframes bellRing {
  0%,
  100% {
    transform-origin: top;
  }

  15% {
    transform: rotateZ(10deg);
  }

  30% {
    transform: rotateZ(-10deg);
  }

  45% {
    transform: rotateZ(5deg);
  }

  60% {
    transform: rotateZ(-5deg);
  }

  75% {
    transform: rotateZ(2deg);
  }
}

.button:active {
  transform: scale(0.8);
}
</style>
