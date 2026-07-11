<script lang="ts" setup>
import { useRouter } from "vue-router";
const router = useRouter();
</script>


<template>
  <router-view v-slot="{ Component }" :key="$route.fullPath">
    <transition name="fade" appear>
      <div
        style="
          overflow-y: scroll;
          overflow-x: none;
          -ms-overflow-style: none;
          scrollbar-width: none;
        "
      >
        <component :is="Component" :key="router.currentRoute.value.fullPath" />
      </div>
    </transition>
  </router-view>
</template>

<style lang="scss" scoped>
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
</style>