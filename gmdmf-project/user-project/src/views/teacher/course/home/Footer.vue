<script lang="ts" setup>
import { ref } from "vue";
import { useCourseStore } from "@/stores/course";
import { useRouter } from "vue-router";

const router = useRouter();

// 返回
const back = () => {
  useCourseStore().removeCourse();
  router.push("/teacher");
};

// 课程信息
const toCourse = () => {
  router.push("/tea-course/courseInfo");
};

// 资源
const toResource = () => {
  router.push("/tea-course/resource");
};
</script>

<template>
  <!-- 底部按钮组 -->
  <div class="button-container">
    <!-- 返回 -->
    <button class="button" @click="back">
      <svg
        t="1715878497069"
        class="icon"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="5065"
        width="19"
        height="19"
      >
        <path
          d="M596.114286 237.714286H201.142857l111.542857-111.542857-51.2-51.2-175.542857 175.542857c-7.314286 7.314286-10.971429 16.457143-10.971428 25.6 0 9.142857 3.657143 18.285714 10.971428 25.6l175.542857 175.542857 51.2-51.2-115.2-115.2h398.628572C751.542857 310.857143 877.714286 437.028571 877.714286 592.457143v3.657143C877.714286 751.542857 751.542857 877.714286 596.114286 877.714286H73.142857v73.142857h522.971429C791.771429 950.857143 950.857143 791.771429 950.857143 596.114286v-3.657143C950.857143 396.8 791.771429 237.714286 596.114286 237.714286z"
          p-id="5066"
          fill="#ffffff"
        ></path>
      </svg>
    </button>
    <!-- 申请 -->
    <button class="button" @click="toCourse">
      <el-icon size="21"><DataAnalysis /></el-icon>
    </button>
    <!-- 帮助 -->
    <button class="button" @click="toResource">
      <el-icon size="21"><UploadFilled /></el-icon>
      <!-- <span>资料 Resource</span> -->
    </button>
  </div>
</template>

<style lang="scss" scoped>
.button-container {
  display: flex;
  background-color: rgb(73, 164, 238);
  margin-left: 4%;
  // margin-top: 10%;
  width: 180px;
  height: 40px;
  align-items: center;
  justify-content: space-around;
  border-radius: 10px;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 5px 5px,
    rgba(27, 133, 219, 0.1) 4px 4px 4px;
}

.button {
  outline: 0 !important;
  border: 0 !important;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all ease-in-out 0.3s;
  cursor: pointer;
}

.button:hover {
  transform: translateY(-3px);
}

.rating {
  display: flex;
  flex-direction: row-reverse;
  gap: 0.3rem;
  --stroke: #666;
  --fill: #ffc73a;
}

.rating input {
  appearance: unset;
}

.rating label {
  cursor: pointer;
}

.rating svg {
  width: 1.5rem;
  height: 1.5rem;
  overflow: visible;
  fill: transparent;
  stroke: var(--stroke);
  stroke-linejoin: bevel;
  stroke-dasharray: 12;
  animation: idle 4s linear infinite;
  transition: stroke 0.2s, fill 0.5s;
}

@keyframes idle {
  from {
    stroke-dashoffset: 24;
  }
}

.rating label:hover svg {
  stroke: var(--fill);
}

.rating input:checked ~ label svg {
  transition: 0s;
  animation: idle 4s linear infinite, yippee 0.75s backwards;
  fill: var(--fill);
  stroke: var(--fill);
  stroke-opacity: 0;
  stroke-dasharray: 0;
  stroke-linejoin: miter;
  stroke-width: 8px;
}

@keyframes yippee {
  0% {
    transform: scale(1);
    fill: var(--fill);
    fill-opacity: 0;
    stroke-opacity: 1;
    stroke: var(--stroke);
    stroke-dasharray: 10;
    stroke-width: 1px;
    stroke-linejoin: bevel;
  }

  30% {
    transform: scale(0);
    fill: var(--fill);
    fill-opacity: 0;
    stroke-opacity: 1;
    stroke: var(--stroke);
    stroke-dasharray: 10;
    stroke-width: 1px;
    stroke-linejoin: bevel;
  }

  30.1% {
    stroke: var(--fill);
    stroke-dasharray: 0;
    stroke-linejoin: miter;
    stroke-width: 8px;
  }

  60% {
    transform: scale(1.2);
    fill: var(--fill);
  }
}

#info-box {
  position: fixed;
  top: 5%;
  right: 2%;
  width: 280px;
  height: 100px;
  border-radius: 10px;
  background-color: #b0abab;
  z-index: 99;
}

#info-box span {
  float: left;
  margin: 4px 12px;
  width: 80px;
  height: 20px;
  border-radius: 10px;
  line-height: 20px;
}

#info-box button {
  float: right;
  // background: none;
  margin: 4px;
  padding: 4px;
  width: 20px;
  height: 20px;
  border-radius: 10px;
  line-height: 10px;
  border: 1px solid #fff;
}

.star {
  position: relative;
  padding: 10px 22px;
  border-radius: 16px;
  border: none;
  color: #fff;
  cursor: pointer;
  background-color: #7d2ae8;
  transition: all 0.2s ease;
  font-size: 12px;
  font-family: "宋体";
}

.star:active {
  transform: scale(0.96);
}

.star:before,
.star:after {
  position: absolute;
  content: "";
  width: 150%;
  left: 50%;
  height: 100%;
  transform: translateX(-50%);
  z-index: -1000;
  background-repeat: no-repeat;
}

.star:hover:before {
  top: -70%;
  background-image: radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, transparent 20%, #7d2ae8 20%, transparent 30%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, transparent 10%, #7d2ae8 15%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%);
  background-size: 10% 10%, 20% 20%, 15% 15%, 20% 20%, 18% 18%, 10% 10%, 15% 15%,
    10% 10%, 18% 18%;
  background-position: 50% 120%;
  animation: greentopBubbles 0.6s ease;
  z-index: 1;
}

@keyframes greentopBubbles {
  0% {
    background-position: 5% 90%, 10% 90%, 10% 90%, 15% 90%, 25% 90%, 25% 90%,
      40% 90%, 55% 90%, 70% 90%;
  }

  50% {
    background-position: 0% 80%, 0% 20%, 10% 40%, 20% 0%, 30% 30%, 22% 50%,
      50% 50%, 65% 20%, 90% 30%;
  }

  100% {
    background-position: 0% 70%, 0% 10%, 10% 30%, 20% -10%, 30% 20%, 22% 40%,
      50% 40%, 65% 10%, 90% 20%;
    background-size: 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%;
  }
}

.star:hover::after {
  bottom: -70%;
  background-image: radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, transparent 10%, #7d2ae8 15%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%),
    radial-gradient(circle, #7d2ae8 20%, transparent 20%);
  background-size: 15% 15%, 20% 20%, 18% 18%, 20% 20%, 15% 15%, 20% 20%, 18% 18%;
  background-position: 50% 0%;
  animation: greenbottomBubbles 0.6s ease;
  z-index: 1;
}

@keyframes greenbottomBubbles {
  0% {
    background-position: 10% -10%, 30% 10%, 55% -10%, 70% -10%, 85% -10%,
      70% -10%, 70% 0%;
  }

  50% {
    background-position: 0% 80%, 20% 80%, 45% 60%, 60% 100%, 75% 70%, 95% 60%,
      105% 0%;
  }

  100% {
    background-position: 0% 90%, 20% 90%, 45% 70%, 60% 110%, 75% 80%, 95% 70%,
      110% 10%;
    background-size: 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%;
  }
}

.svg {
  float: left;
}
</style>
