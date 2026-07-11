<template>
  <div class="nav-tools">
    <ul class="example-2">
      <li class="icon-content">
        <button @click="goHome" data-social="spotify">
          <div class="filled"></div>
          <svg
            t="1716622991422"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="3503"
            width="20"
            height="20"
          >
            <path
              d="M869.28 123.68H177.2A90.08 90.08 0 0 0 87.2 213.68V570.4h872V213.68a90 90 0 0 0-89.92-90zM87.2 665.04a90.16 90.16 0 0 0 90 90h263.28v105.6H274.96a30.08 30.08 0 1 0 0 60.16h496.56a30.08 30.08 0 1 0 0-60.16H606v-105.6h263.28a90.08 90.08 0 0 0 90-90v-35.04H87.2z"
              fill="#515151"
              p-id="3504"
            ></path>
          </svg>
        </button>
      </li>
      <li class="icon-content">
        <button @click="goCourseAll" data-social="pinterest">
          <div class="filled"></div>
          <svg
            t="1716623114441"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="7880"
            width="20"
            height="20"
          >
            <path
              d="M 162.76 87 h 232.88 a 77.71 77.71 0 0 1 77.64 77.63 V 397.5 a 77.71 77.71 0 0 1 -77.64 77.63 H 162.76 a 77.7 77.7 0 0 1 -77.62 -77.63 V 164.63 A 77.7 77.7 0 0 1 162.76 87"
              fill="#515151"
              p-id="7881"
            ></path>
            <path
              d="M 628.52 552.76 h 232.89 A 77.71 77.71 0 0 1 939 630.38 v 232.88 a 77.71 77.71 0 0 1 -77.63 77.63 H 628.52 a 77.69 77.69 0 0 1 -77.61 -77.63 V 630.38 a 77.69 77.69 0 0 1 77.61 -77.62"
              fill="#515151"
              p-id="7882"
            ></path>
            <path
              d="M 628.52 87 h 232.89 A 77.71 77.71 0 0 1 939 164.63 V 397.5 a 77.71 77.71 0 0 1 -77.63 77.63 H 628.52 a 77.69 77.69 0 0 1 -77.61 -77.63 V 164.63 A 77.69 77.69 0 0 1 628.52 87 M 162.76 552.76 h 232.9 a 77.69 77.69 0 0 1 77.61 77.62 v 232.88 a 77.69 77.69 0 0 1 -77.61 77.63 h -232.9 a 77.7 77.7 0 0 1 -77.62 -77.63 V 630.38 a 77.69 77.69 0 0 1 77.62 -77.62"
              fill="#515151"
              p-id="7883"
            ></path>
          </svg>
        </button>
      </li>
      <li class="icon-content">
        <button @click="copyHref" id="share-btn" data-social="telegram">
          <div class="filled"></div>
          <svg
            t="1716623158910"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="8980"
            width="20"
            height="20"
          >
            <path
              d="M782.791111 643.982222c-47.786667 0-91.022222 22.755556-118.328889 59.164445l-282.168889-147.911111c4.551111-13.653333 4.551111-27.306667 4.551111-40.96 0-15.928889-2.275556-29.582222-6.826666-43.235556L614.4 304.924444c20.48 13.653333 43.235556 22.755556 70.542222 22.755556 65.991111 0 118.328889-52.337778 118.328889-118.328889S750.933333 93.297778 684.942222 93.297778s-118.328889 52.337778-118.328889 118.328889c0 11.377778 2.275556 22.755556 6.826667 34.133333L343.608889 409.6c-27.306667-25.031111-61.44-40.96-102.4-40.96-81.92 0-147.911111 65.991111-147.911111 147.911111s65.991111 147.911111 147.911111 147.911111c40.96 0 77.368889-15.928889 104.675555-43.235555L637.155556 773.688889c0 6.826667-2.275556 13.653333-2.275556 20.48 0 81.92 65.991111 147.911111 147.911111 147.911111s147.911111-65.991111 147.911111-147.911111-65.991111-150.186667-147.911111-150.186667z"
              fill="#515151"
              p-id="8981"
            ></path>
          </svg>
        </button>
      </li>
    </ul>
  </div>
</template>

<script lang="ts" setup>
import { ref, h } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

import "clipboard/dist/clipboard";
import ClipboardJS from "clipboard";

const router = useRouter();

const goHome = () => {
  // 判断是学生还是老师，跳转到对应页面
  router.push("/login");
};

const goCourseAll = () => {
  window.scrollTo({
    top: 600,
    behavior: "smooth",
  });
};

// 将内容复制到剪切板
const copyHref = () => {
  const clipboard = new ClipboardJS("#share-btn", {
    text: function () {
      return window.location.href;
    },
  });

  const m = ElMessage({
    message: h("p", { style: "line-height: 1; font-size: 14px" }, [
      h("strong", { style: "color: teal" }, "当前页面 URL "),
      h("span", null, "已复制到剪切板"),
    ]),
    type: "success",
    grouping: true,
    center: true,
    plain: true,
    duration: 2000,
    repeatNum: 1,
  });

  clipboard.on("success", function (e) {
    m;
    e.clearSelection();
  });
};
</script>

<style lang="css" scoped>
.nav-tools {
  position: fixed;
  top: 29%;
  right: 2%;
  z-index: 100;
}

ul {
  list-style: none;
}

.example-2 {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  align-items: center;
}

.example-2 .icon-content {
  margin: 5px 10px;
  position: relative;
  border-radius: 20%;
  box-shadow: 1px 1px 4px 0px rgb(0 0 0 / 36%);
  cursor: pointer;
}

.example-2 .icon-content button {
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 50px;
  height: 50px;
  border-radius: 20%;
  background-color: #ffff;
  transition: all 0.3s ease-in-out;
  border: 0;
  cursor: pointer;
}

.example-2 .icon-content button:hover {
  color: #ffff;
  transition: all 0.3s ease-in-out;
}

.example-2 .icon-content button:hover .filled {
  height: 100%;
}

.example-2 .icon-content button:hover {
  box-shadow: 3px 2px 16px 0px rgb(0 0 0 / 40%);
}

.example-2 .icon-content button svg {
  position: relative;
  z-index: 1;
  width: 30px;
  height: 30px;
}

.example-2 .icon-content button:hover path {
  fill: #fff;
  transition: all 0.3s ease-in-out;
}

.example-2 .icon-content button:hover {
  color: white;
}

.example-2 .icon-content button .filled {
  position: absolute;
  top: auto;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 0;
  background-color: #000;
  transition: all 0.3s ease-in-out;
}

.example-2 .icon-content button:hover .filled {
  height: 100%;
}

.example-2 .icon-content button[data-social="spotify"] .filled {
  background-color: #1db954;
}

.example-2 .icon-content button[data-social="pinterest"] .filled {
  background-color: #bd081c;
}

.example-2 .icon-content button[data-social="telegram"] .filled {
  background-color: #0088cc;
}
</style>
