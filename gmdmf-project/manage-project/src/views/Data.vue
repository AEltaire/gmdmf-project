<script setup lang="ts">
import { ref, watchEffect, onMounted, nextTick, onUnmounted } from "vue";
import { useBreakpoint } from "vuestic-ui";
import * as echarts from "echarts";
import { computed } from "vue";
import { useColors } from "vuestic-ui";
import { getCountDataService, getSchoolDataService } from "@/api/Data";
import { findAllManageService } from "@/api/Manage";
import {
  sendMessageService,
  findByManageIdMessageService,
} from "@/api/Message";
import { findAllSchoolService } from "@/api/School";
import elementResizeDetectorMaker from "element-resize-detector";
import { Plus } from "@element-plus/icons-vue";
import { useUserInfoStore } from "@/stores/userInfo";
const showLeft = ref(true);
const userInfo = useUserInfoStore().userInfo;
const breakpoints = useBreakpoint();
const isSidebarVisible = ref(breakpoints.mdUp);
const { currentPresetName } = useColors();

const showModal = ref(false);
const listValue = ref("Option 1");

watchEffect(() => {
  isSidebarVisible.value = breakpoints.smUp;
});

const menu = [
  { icon: "add", title: "Add" },
  { icon: "phone", title: "Phone" },
  { icon: "email", title: "Email" },
  { icon: "settings", title: "Settings" },
  { icon: "logout", title: "Logout" },
];

const darkNavbarColors = computed(() => {
  if (currentPresetName.value === "light") {
    return {
      color: "#111111",
      textColor: "#BAFFC5",
    };
  } else {
    return {
      color: "#FBCAF6",
      textColor: "#481269",
    };
  }
});

interface Manage {
  id: number;
  name: string;
  username: string;
  introduce: string;
  picture: string;
}

const manageList = ref<Manage[]>([]);

const countList = ref({
  user: 0,
  course: 0,
  subject: 0,
});

// 获取统计数据
const getCountData = async () => {
  const result = await getCountDataService();
  countList.value.user = result.data.data[0];
  countList.value.course = result.data.data[1];
  countList.value.subject = result.data.data[2];
};

// 获取所有管理
const findAllManage = async () => {
  const result = await findAllManageService();
  manageList.value = result.data.data.filter((item) => item.id !== userInfo.id);
};

const manageVO = ref<Manage>({
  id: 0,
  name: "",
  username: "",
  introduce: "",
  picture: "",
});
const messageList = ref([]);

const MessageDTO = ref({
  content: "",
});

const send = async () => {
  await sendMessageService(manageVO.value.id, MessageDTO.value.content);
  MessageDTO.value.content = "";
  await findByManageIdMessage();
};

const findByManageIdMessage = async () => {
  const result = await findByManageIdMessageService(manageVO.value.id);
  messageList.value = result.data.data;
  nextTick(() => {
    const list = document.querySelector(".scrollbar");
    list.scrollTop = list.scrollHeight;
  });
};

const getSchoolData = async () => {
  const result = await getSchoolDataService();
  const schoolList = await findAllSchoolService();
  y.value = schoolList.data.data.map(item => item.name);
  const x = result.data.data;
  y.value.forEach((item) => {
    student.value.push(x[item][0]);
    teacher.value.push(x[item][1]);
  });
};

const student = ref([]);
const teacher = ref([]);
const y = ref(null);

onMounted(async () => {
  await getSchoolData();
  var myChart = echarts.init(document.getElementById("dataChart"));
  var option;
  // 绘制图表

  option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        // Use axis to trigger tooltip
        type: "shadow", // 'shadow' as default; can also be 'line' or 'shadow'
      },
    },
    legend: {},
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "value",
    },
    yAxis: {
      type: "category",
      data: y.value,
      inverse: true,
      max: 10
    },
    series: [
      {
        name: "学生",
        type: "bar",
        stack: "total",
        label: {
          show: true,
        },
        emphasis: {
          focus: "series",
        },
        data: student.value,
      },
      {
        name: "教师",
        type: "bar",
        stack: "total",
        label: {
          show: true,
        },
        emphasis: {
          focus: "series",
        },
        data: teacher.value,
      },
    ],
  };
  option && myChart.setOption(option);
  window.addEventListener("resize", () => {
    showLeft.value = window.innerWidth > 768;
    myChart.resize();
  });

  let erd: any = elementResizeDetectorMaker();
  erd.listenTo(document.getElementById("dataChart"), (element) => {
    nextTick(() => {
      echarts.init(document.getElementById("dataChart")).resize();
    });
  });

  await getCountData();
  await findAllManage();
});

var websocket = null;

//判断当前浏览器是否支持 WebSocket
const clickSend = async (mange) => {
  manageVO.value = mange;
  await findByManageIdMessage();
  if ("WebSocket" in window) {
    //连接WebSocket节点
    websocket = new WebSocket(
      `ws://localhost:8080/ws/${userInfo.id}/${manageVO.value.id}`
    );
    websocket.onerror = function () {
      setMessageInnerHTML("error");
    };

    // 连接成功建立的回调方法
    websocket.onopen = function () {
      setMessageInnerHTML("连接成功");
    };

    // 接收到消息的回调方法
    websocket.onmessage = function (event) {
      setMessageInnerHTML(event.data);
    };

    // 连接关闭的回调方法
    websocket.onclose = function () {
      setMessageInnerHTML("close");
    };
  } else {
    alert("Not support websocket");
  }
  showModal.value = true;
  nextTick(() => {
    const list = document.querySelector(".scrollbar");
    list.scrollTop = list.scrollHeight;
  });
};

// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
  websocket?.close();
};

// 将消息显示在网页上
function setMessageInnerHTML(message) {
  if (message != "连接成功" && message != "close") {
    messageList.value = JSON.parse(message);
    nextTick(() => {
      const list = document.querySelector(".scrollbar");
      list.scrollTop = list.scrollHeight;
    });
  }
}

onUnmounted(() => {
  websocket?.close();
});
</script>

<template>
  <VaModal
    v-model="showModal"
    hide-default-actions
    @beforeClose="websocket?.close()"
  >
    <template #header>
      <VaList>
        <VaListItem class="list__item">
          <VaListItemSection avatar>
            <VaAvatar>
              <img :src="manageVO.picture"  />
            </VaAvatar>
          </VaListItemSection>

          <VaListItemSection>
            <VaListItemLabel>
              {{ manageVO.name }}
            </VaListItemLabel>

            <VaListItemLabel caption>
              {{ manageVO.introduce }}
            </VaListItemLabel>
          </VaListItemSection>
        </VaListItem>
      </VaList>
    </template>
    <VaDivider style="margin: 16px 0" />
    <VaScrollContainer class="max-h-52 scrollbar" vertical>
      <VaList id="list" style="height: 50vh; padding: 2px 2px">
        <VaListLabel v-show="!messageList.length"> 暂无数据 </VaListLabel>
        <VaListItem
          v-for="(message, index) in messageList"
          :key="index"
          class="list__item"
          style="margin-top: 12px"
        >
          <VaListItemSection>
            <VaListItemLabel>
              <span
                :class="message.come == userInfo.id ? 'message my' : 'message'"
                >{{ message.content }}</span
              >
            </VaListItemLabel>
          </VaListItemSection>
        </VaListItem>
      </VaList>
    </VaScrollContainer>
    <template #footer>
      <VaInput
        v-model="MessageDTO.content"
        placeholder="请输入内容..."
        style="width: 88%; font-size: 13px; margin-top: 20px"
      />
      <VaButton
        style="width: 10%; margin-left: 2%; margin-top: 20px"
        @click="send"
      >
        发送
      </VaButton>
    </template>
  </VaModal>
  <el-row>
    <el-col :span="18" style="padding: 0 10px">
      <VaCard style="margin-bottom: 20px; height: 68vh">
        <VaCardContent>
          <div
            v-on-echart-resize
            id="dataChart"
            style="width: 100%; height: 65vh"
          ></div>
        </VaCardContent>
      </VaCard>
      <el-row style="margin-bottom: 20px">
        <el-col :span="8" style="padding-right: 10px">
          <VaCard color="#c6e2ff">
            <VaCardContent>
              <div class="row">
                <div class="item">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    height="60px"
                    viewBox="0 -960 960 960"
                    width="60px"
                    fill="#409EFF"
                  >
                    <path
                      d="M411-480q-28 0-46-21t-13-49l12-72q8-43 40.5-70.5T480-720q44 0 76.5 27.5T597-622l12 72q5 28-13 49t-46 21H411Zm24-80h91l-8-49q-2-14-13-22.5t-25-8.5q-14 0-24.5 8.5T443-609l-8 49ZM124-441q-23 1-39.5-9T63-481q-2-9-1-18t5-17q0 1-1-4-2-2-10-24-2-12 3-23t13-19l2-2q2-19 15.5-32t33.5-13q3 0 19 4l3-1q5-5 13-7.5t17-2.5q11 0 19.5 3.5T208-626q1 0 1.5.5t1.5.5q14 1 24.5 8.5T251-596q2 7 1.5 13.5T250-570q0 1 1 4 7 7 11 15.5t4 17.5q0 4-6 21-1 2 0 4l2 16q0 21-17.5 36T202-441h-78Zm676 1q-33 0-56.5-23.5T720-520q0-12 3.5-22.5T733-563l-28-25q-10-8-3.5-20t18.5-12h80q33 0 56.5 23.5T880-540v20q0 33-23.5 56.5T800-440ZM0-240v-63q0-44 44.5-70.5T160-400q13 0 25 .5t23 2.5q-14 20-21 43t-7 49v65H0Zm240 0v-65q0-65 66.5-105T480-450q108 0 174 40t66 105v65H240Zm560-160q72 0 116 26.5t44 70.5v63H780v-65q0-26-6.5-49T754-397q11-2 22.5-2.5t23.5-.5Zm-320 30q-57 0-102 15t-53 35h311q-9-20-53.5-35T480-370Zm0 50Zm1-280Z"
                    />
                  </svg>
                </div>
                <div class="item" style="padding-left: 20px">
                  <p class="info" style="color: #409eff">用户数</p>
                  <p class="number" style="color: #409eff">
                    {{ countList.user }}
                  </p>
                </div>
              </div>
            </VaCardContent>
          </VaCard></el-col
        >
        <el-col :span="8" style="padding: 0 5px">
          <VaCard color="#f8e3c5">
            <VaCardContent>
              <div class="row">
                <div class="item">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    height="60px"
                    width="60px"
                    viewBox="0 -960 960 960"
                    fill="#E6A23C"
                  >
                    <path
                      d="m411-320 223-143q9-6 9-17t-9-17L411-640q-10-7-20.5-1T380-623v286q0 12 10.5 18t20.5-1ZM160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h640q33 0 56.5 23.5T880-720v480q0 33-23.5 56.5T800-160H160Zm0-80h640v-480H160v480Zm0 0v-480 480Z"
                    />
                  </svg>
                </div>
                <div class="item" style="margin-left: 20px">
                  <p class="info" style="color: #e6a23c">课程数</p>
                  <p class="number" style="color: #e6a23c">
                    {{ countList.course }}
                  </p>
                </div>
              </div>
            </VaCardContent>
          </VaCard>
        </el-col>
        <el-col :span="8" style="padding-left: 10px">
          <VaCard color="#fcd3d3">
            <VaCardContent>
              <div class="row">
                <div class="item">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    height="58px"
                    width="58px"
                    viewBox="0 -960 960 960"
                    fill="#F56C6C"
                  >
                    <path
                      d="M440-400h80q17 0 28.5-11.5T560-440q0-17-11.5-28.5T520-480h-80q-17 0-28.5 11.5T400-440q0 17 11.5 28.5T440-400Zm0-120h240q17 0 28.5-11.5T720-560q0-17-11.5-28.5T680-600H440q-17 0-28.5 11.5T400-560q0 17 11.5 28.5T440-520Zm0-120h240q17 0 28.5-11.5T720-680q0-17-11.5-28.5T680-720H440q-17 0-28.5 11.5T400-680q0 17 11.5 28.5T440-640ZM320-240q-33 0-56.5-23.5T240-320v-480q0-33 23.5-56.5T320-880h480q33 0 56.5 23.5T880-800v480q0 33-23.5 56.5T800-240H320Zm0-80h480v-480H320v480ZM160-80q-33 0-56.5-23.5T80-160v-520q0-17 11.5-28.5T120-720q17 0 28.5 11.5T160-680v520h520q17 0 28.5 11.5T720-120q0 17-11.5 28.5T680-80H160Zm160-720v480-480Z"
                    />
                  </svg>
                </div>
                <div class="item" style="margin-left: 20px">
                  <p class="info" style="color: #f56c6c">题目数</p>
                  <p class="number" style="color: #f56c6c">
                    {{ countList.subject }}
                  </p>
                </div>
              </div>
            </VaCardContent>
          </VaCard></el-col
        >
      </el-row>
    </el-col>
    <el-col :span="6" style="padding: 0 10px">
      <VaCard square outlined color="#6988e5" gradient class="opinion card">
        <VaCardTitle>管理员</VaCardTitle>
        <VaCardContent>
          <VaList>
            <VaListItem
              v-for="(manage, index) in manageList"
              :key="index"
              class="list__item"
            >
              <VaListItemSection avatar>
                <VaAvatar>
                  <img :src="manage.picture" />
                </VaAvatar>
              </VaListItemSection>

              <VaListItemSection>
                <VaListItemLabel>
                  {{ manage.name }}
                </VaListItemLabel>

                <VaListItemLabel caption>
                  {{ manage.introduce }}
                </VaListItemLabel>
              </VaListItemSection>

              <VaListItemSection
                @click="clickSend(manage)"
                style="cursor: pointer"
                icon
              >
                <VaIcon name="forum" color="background-element" />
              </VaListItemSection>
            </VaListItem>
          </VaList>
        </VaCardContent>
      </VaCard>
    </el-col>
  </el-row>
</template>

<style lang="css" scoped>
.scrollbar {
  overflow-y: scroll;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.message {
  display: inline-block;
  padding: 8px 12px;
  border-radius: 5px;
  font-size: 13px;
  color: #000;
  background-color: #dee5f2;
}
.my {
  float: right;
  background-color: rgb(148.6, 212.3, 117.1);
}
.va-navbar {
  --va-navbar-padding-y: 0.9rem;
}
.card {
  margin-bottom: 20px;
  --va-card-padding: 20px 20px 5px 20px;
}

.list__item + .list__item {
  margin-top: 20px;
}

.add {
  position: absolute;
  margin: 0;
  --va-button-content-px: 0px;
  --va-button-content-py: 0px;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  right: 10px;
  bottom: 10px;
}

.va-card__content {
  padding: 15px;
  padding-bottom: 10px;
}

.item .number {
  width: 100%;
  height: 45px;
  line-height: 25px;
  font-size: 24px;
  font-weight: 700;
  color: #67c23a;
}
.item .info {
  margin-top: 8px;
  height: 13px;
  font-size: 10px;
  font-weight: 700;
  color: #67c23a;
  /* background-color: pink; */
}

.opinion {
  height: 83vh;
  border-radius: 5px !important;
}
</style>
