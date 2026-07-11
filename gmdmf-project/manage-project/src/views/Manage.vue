<script setup lang="ts">
import { ref, watchEffect, onMounted, watch, computed } from "vue";
import { useBreakpoint, VaIcon } from "vuestic-ui";

const breakpoints = useBreakpoint();
const showLeft = ref(true);

import { useTokenStore } from "@/stores/token";
import { useUserInfoStore } from "@/stores/userInfo";
import { useCourseStore } from "@/stores/course";
import { useStore } from "vuex";
import { useColors } from "vuestic-ui";
const { applyPreset, currentPresetName, colors } = useColors();
import { useRouter } from "vue-router";
import { Transition } from "vue";
import { ElMessage } from "element-plus";

const router = useRouter();
const minimized = ref(true);
const isSidebarVisible = ref();
const activeElement = ref();
const store = useStore();

// 切换主题
const switchValue = computed({
  get() {
    return currentPresetName.value;
  },
  set(value) {
    applyPreset(value);
  },
});

const items: any[] = [
  { title: "数据视图", icon: "dashboard" },
  { title: "意见反馈", icon: "chat" },
  { title: "课程审批", icon: "credit_score" },
  { title: "课程管理", icon: "video_settings" },
  { title: "学校管理", icon: "apartment" },
  { title: "专业管理", icon: "bookmarks" },
  { title: "教师管理", icon: "school" },
  { title: "学生管理", icon: "face" },
  { title: "学生注册", icon: "group_add" },
];

const changeRouter = () => {
  if (activeElement.value == "数据视图") {
    router.push("/data");
  } else if (activeElement.value == "课程审批") {
    router.push("/approval");
  } else if (activeElement.value == "课程管理") {
    router.push("/course");
  } else if (activeElement.value == "教师管理") {
    router.push("/teacher");
  } else if (activeElement.value == "学生注册") {
    router.push("/register");
  } else if (activeElement.value == "意见反馈") {
    router.push("/opinion");
  } else if (activeElement.value == "账号设置") {
    router.push("/setting");
  } else if (activeElement.value == "专业管理") {
    router.push("/major");
  } else if (activeElement.value == "学校管理") {
    router.push("/school");
  } else if (activeElement.value == "学生管理") {
    router.push("/student");
  }
};

watch(
  () => router.currentRoute.value.path,
  (toPath) => {
    if (toPath == "/data") {
      activeElement.value = "数据视图";
    } else if (toPath == "/approval") {
      activeElement.value = "课程审批";
    } else if (toPath == "/course") {
      activeElement.value = "课程管理";
    } else if (toPath == "/user") {
      activeElement.value = "教师管理";
    } else if (toPath == "/register") {
      activeElement.value = "学生注册";
    } else if (toPath == "/opinion") {
      activeElement.value = "意见反馈";
    } else if (toPath == "/setting") {
      activeElement.value = "账号设置";
    } else if (toPath == "/major") {
      activeElement.value = "专业管理";
    } else if (toPath == "/school") {
      activeElement.value = "学校管理";
    } else if (toPath == "/student") {
      activeElement.value = "学生管理";
    } 
  },
  { immediate: true, deep: true }
);

const showNav = () => {
  minimized.value = !minimized.value;
  isSidebarVisible.value = !isSidebarVisible.value;
};

window.addEventListener("resize", () => {
  // 判断窗口宽度
  if (window.innerWidth < 1024) {
    showLeft.value = false;
  } else {
    showLeft.value = true;
  }
});

import { SwitchButton } from "@element-plus/icons-vue";

const loginOut = async () => {
  await useCourseStore().removeCourse();
  await useUserInfoStore().removeUserInfo();
  await useTokenStore().removeToken();
  router.push("/login");
  ElMessage.success("退出成功");
};
</script>

<template>
  <VaLayout
    :top="{ fixed: true, order: 2 }"
    :left="{
      fixed: true,
      absolute: breakpoints.smDown,
      order: 1,
      overlay: breakpoints.smDown && isSidebarVisible,
    }"
    @left-overlay-click="isSidebarVisible = false"
  >
    <template #top>
      <VaNavbar class="navbar" shadowed>
        <template #left>
          <VaButton
            preset="secondary"
            :icon="isSidebarVisible ? 'menu_open' : 'menu'"
            @click="showNav"
            :disabled="!showLeft"
          />
        </template>

        <template #right>
          <VaSwitch
            color="#ffd300"
            off-color="#5123a1"
            v-model="switchValue"
            true-value="light"
            false-value="dark"
            style="--va-switch-checker-background-color: #252723"
            @click="store.commit('changeTheme')"
          >
            <template #innerLabel>
              <div class="va-text-center">
                <VaIcon
                  :name="store.state.isLightTheme ? 'light_mode' : 'dark_mode'"
                />
              </div>
            </template>
          </VaSwitch>
          <el-button class="button" @click="loginOut" type="danger" text>
            <el-icon size="20"><SwitchButton /></el-icon>
          </el-button>
        </template>
      </VaNavbar>
    </template>

    <template #left>
      <VaSidebar
        :minimized="minimized"
        minimized-width="64px"
        v-model="showLeft"
        @click="changeRouter"
        :active="activeElement"
        style="--va-sidebar-menu-overflow-x: hidden"
        class="absolute left-0 left-auto"
      >
        <VaAccordion>
          <template v-for="item in items">
            <VaCollapse
              v-if="item.children"
              :key="item.title + 'collapse'"
              body-color="#00000022"
            >
              <template #header="{ value: isCollapsed }">
                <VaSidebarItem
                  :active="
                    item.children.some((child) => child.title === activeElement)
                  "
                >
                  <VaSidebarItemContent>
                    <VaIcon :name="item.icon" />
                    <VaSidebarItemTitle>{{ item.title }}</VaSidebarItemTitle>
                    <VaSpacer />
                    <VaIcon
                      :name="isCollapsed ? 'va-arrow-up' : 'va-arrow-down'"
                    />
                  </VaSidebarItemContent>
                </VaSidebarItem>
              </template>

              <template #body>
                <VaSidebarItem
                  v-for="child in item.children"
                  :key="child.title"
                  :active="child.title === activeElement"
                  @click="activeElement = child.title"
                >
                  <VaSidebarItemContent>
                    <VaIcon :name="child.icon" />
                    <VaSidebarItemTitle> {{ child.title }}</VaSidebarItemTitle>
                  </VaSidebarItemContent>
                </VaSidebarItem>
              </template>
            </VaCollapse>

            <VaSidebarItem
              v-else
              :key="item.title + 'item'"
              :active="item.title === activeElement"
              @click="activeElement = item.title"
            >
              <VaSidebarItemContent>
                <VaIcon :name="item.icon" />
                <VaSidebarItemTitle>{{ item.title }}</VaSidebarItemTitle>
              </VaSidebarItemContent>
            </VaSidebarItem>
          </template>
        </VaAccordion>

        <VaSpacer />

        <VaSidebarItem
          :active="'账号设置' === activeElement"
          @click="activeElement = '账号设置'"
        >
          <VaSidebarItemContent>
            <VaIcon name="settings" />
            <VaSidebarItemTitle>账号设置</VaSidebarItemTitle>
          </VaSidebarItemContent>
        </VaSidebarItem>
      </VaSidebar>
    </template>

    <template #content>
      <router-view v-slot="{ Component }" :key="$route.fullPath">
        <transition name="fade" appear>
          <main class="main">
            <component
              :is="Component"
              :key="router.currentRoute.value.fullPath"
            />
          </main>
        </transition>
      </router-view>
    </template>
  </VaLayout>
</template>

<style lang="css" scoped>
.navbar {
  --va-layout-scroll-padding: 0.5rem;
}

.va-navbar {
  --va-navbar-padding-y: 0.9rem;
}

.va-layout-area {
  overflow-y: scroll;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.main {
  height: 100%;
  padding: 20px 20px;
  padding-bottom: 0;
}

/* fade-enter: DOM 进入初始状态 */
.fade-enter-from {
  transform: translateX(-20px);
  opacity: 0;
}

/* fade-enter-active: 过渡动画 */
.fade-enter-active {
  transition: all 0.5s ease-out;
}

/* fade-enter-to: DOM 进入结束状态 */
.fade-enter-to {
  transform: translateX(0px);
  opacity: 1;
}

.fade-leave-from {
  transform: translateX(0px);
  opacity: 1;
}

.fade-leave-active {
  transition: all 0.5s ease-out;
}

.fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}

.button {
  margin-left: 15px;
  width: 31.5px;
  height: 31.5px;
  align-items: center;
  justify-content: center;
  background-color: rgb(254, 240.3, 240.3);
  border-radius: 50%;
  cursor: pointer;
  transition-duration: 0.3s;
  box-shadow: 1px 1px 2px 1px rgba(0, 0, 0, 0.12);
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
