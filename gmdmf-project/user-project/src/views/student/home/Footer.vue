<script lang="ts" setup>
import { onBeforeMount, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { Check } from "@element-plus/icons-vue";
import {
  getStudentInfoService,
  updateStudentService,
  updatePictureStudentService,
  updatePasswordStudentService,
} from "@/api/Student";
const router = useRouter();
const dialogVisibleForOpinion = ref(false);
const dialogVisibleForUser = ref(false);
const show = ref("0");
import { saveOpinionService } from "@/api/Opinion";
import {
  ElMessage,
  type ComponentSize,
  type FormInstance,
  type FormRules,
} from "element-plus";
const ruleFormRef = ref<FormInstance>();
const token = localStorage.getItem("token");
import { useCourseStore } from "@/stores/course";
import { useUserInfoStore } from "@/stores/userInfo";
import { useTokenStore } from "@/stores/token";
import { usePaperStore } from "@/stores/paper";
import { useNodeStore } from "@/stores/node";
import $ from "jquery";

const goIndex = () => {
  // 使用路由前往首页
  router.push("/");
};

function select(id) {
  show.value = id;
  const btn = document.querySelectorAll("button[class=container]")[id];
  document.querySelector("#active")
    ? (document.querySelector("#active").id = "")
    : "";
  btn.id = "active";
}

interface RuleForm {
  oldPassword: string;
  newPassword: string;
  rePassword: string;
}

interface Student {
  id: number;
  name: string;
  sex: string;
  birth: string;
  address: string;
  phone: string;
  email: string;
  school: string;
  department: string;
  major: string;
  teamName: string;
  number: string;
  username: string;
  picture: string;
  introduce: string;
  rule: string;
}

const studentVO = ref<Student>({
  id: null,
  name: "",
  sex: "",
  birth: "",
  address: "",
  phone: "",
  email: "",
  school: "",
  department: "",
  major: "",
  teamName: "",
  number: "",
  username: "",
  picture: "",
  introduce: "",
  rule: "",
});

const studentDTO = ref<Student>();
const passwordModel = ref<RuleForm>({
  oldPassword: "",
  newPassword: "",
  rePassword: "",
});

const getUserInfo = async () => {
  const result = await getStudentInfoService();
  studentVO.value = result.data.data;
  useUserInfoStore().setUserInfo(studentVO.value);
  dialogVisibleForUser.value = true;
};

const updateStudent = async () => {
  studentDTO.value = studentVO.value;

  const phoneReg = /(^1\d{10}$)|(^[0-9]\d{7}$)/;
  const emailReg = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;

  if (studentDTO.value.phone.trim() == "") {
    ElMessage.warning("手机号码不能为空");
    return;
  }

  if (!phoneReg.test(studentDTO.value.phone)) {
    ElMessage.warning("手机号码格式错误");
    return;
  }

  if (studentDTO.value.email.trim() == "") {
    ElMessage.warning("邮箱不能为空");
    return;
  }

  if (!emailReg.test(studentDTO.value.email)) {
    ElMessage.warning("邮箱格式错误");
    return;
  }

  const result = await updateStudentService(studentDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
  dialogVisibleForUser.value = false;
  studentDTO.value = null;
};

const updatePwd = async () => {
  const result = await updatePasswordStudentService({
    oldPassword: passwordModel.value.oldPassword,
    password: passwordModel.value.newPassword,
  });
  ElMessage.success("修改成功，请重新登录");
  await useCourseStore().removeCourse();
  await useUserInfoStore().removeUserInfo();
  await useTokenStore().removeToken();
  await usePaperStore().removePaper();
  await useNodeStore().removeNode();
  await localStorage.removeItem("token");
  router.push("/login");
};

const checkPassword = function (rule, value, callback) {
  if (passwordModel.value.newPassword != passwordModel.value.rePassword) {
    callback(new Error("两次密码不一致"));
  } else {
    callback();
  }
};

const rulePassword = reactive<FormRules<RuleForm>>({
  oldPassword: [
    { required: true, message: "请输入旧密码", trigger: "blur" },
    { min: 4, max: 30, message: "长度在 4 到 30 个字符", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 4, max: 30, message: "长度在 4 到 30 个字符", trigger: "blur" },
  ],
  rePassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: checkPassword, trigger: "blur" },
    { min: 4, max: 30, message: "长度在 4 到 30 个字符", trigger: "blur" },
  ],
});

// 意见反馈 ---------------------------

const option = ref({
  content: null,
  state: "好评",
});

// 意见反馈
const upOpinion = async (e) => {
  e.preventDefault();

  if (!option.value.content) {
    ElMessage.warning("请输入您的意见");
    return;
  }

  document
    .querySelectorAll(".rating input[type=radio]")
    .forEach((input: any) => {
      if (input.checked) {
        option.value.state = input.value;
      }
    });
  document.querySelectorAll(".rating .svg").forEach((svg) => {
    $(svg).css({
      fill: "rgb(182, 175, 175, 0.7)",
    });
  });
  const result = await saveOpinionService(option.value);
  result.data.message
    ? ElMessage.success(result.data.message)
    : ElMessage.success("感谢您的反馈~");
  hideOpinion();
  option.value = {
    content: null,
    state: "好评",
  };
};

const showOpinion = () => {
  dialogVisibleForOpinion.value = true;
  $(".opinion-box").animate(
    {
      opacity: 1,
      right: "2%",
    },
    300
  );
};

const hideOpinion = () => {
  $(".opinion-box").animate(
    {
      opacity: 0,
      right: 0,
    },
    300,
    function () {
      dialogVisibleForOpinion.value = false;
    }
  );
};

// 上传头像 ------------------------------
const isUpdateAvatar = ref(false);
import type { UploadInstance, UploadUserFile } from "element-plus";

const fileList = ref<UploadUserFile[]>([]);
const uploadRef = ref<UploadInstance>();

const submitUpload = async () => {
  uploadRef.value!.submit();
};

const handleSuccess = async (response: any) => {
  await updatePictureStudentService(response.data);
  ElMessage({
    message: "上传成功",
    type: "success",
  });
  await getUserInfo();

  isUpdateAvatar.value = false;
  fileList.value = [];
};
</script>

<template>
  <!-- 意见反馈 -->
  <div class="opinion-box" v-show="dialogVisibleForOpinion == true">
    <form class="form">
      <div class="title">意见反馈</div>
      <span class="close-opinion" @click="hideOpinion"
        ><svg
          t="1716021820518"
          class="icon"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          p-id="8575"
          width="20"
          height="20"
        >
          <path
            d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z"
            fill="#5090F1"
            p-id="8576"
          ></path>
          <path
            d="M311.585735 246.935972m6.464976 6.464977l452.54834 452.54834q6.464976 6.464976 0 12.929952l-51.71981 51.71981q-6.464976 6.464976-12.929952 0l-452.54834-452.54834q-6.464976-6.464976 0-12.929952l51.71981-51.71981q6.464976-6.464976 12.929952 0Z"
            fill="#FFFFFF"
            p-id="8577"
          ></path>
          <path
            d="M712.414265 246.935972m6.464976 6.464977l51.71981 51.71981q6.464976 6.464976 0 12.929952l-452.54834 452.54834q-6.464976 6.464976-12.929952 0l-51.71981-51.71981q-6.464976-6.464976 0-12.929952l452.54834-452.54834q6.464976-6.464976 12.929952 0Z"
            fill="#FFFFFF"
            p-id="8578"
          ></path></svg
      ></span>
      <textarea
        v-model="option.content"
        placeholder="你想对制作组说..."
      ></textarea>
      <div class="bottom">
        <div class="rating">
          <label for="super-happy" class="lb">
            <input
              @v-model="option.state"
              type="radio"
              name="rating"
              class="super-happy"
              id="super-happy"
              value="好评"
              style="background-color: red"
            />
            <svg class="svg" viewBox="0 0 24 24">
              <path
                d="M12,17.5C14.33,17.5 16.3,16.04 17.11,14H6.89C7.69,16.04 9.67,17.5 12,17.5M8.5,11A1.5,1.5 0 0,0 10,9.5A1.5,1.5 0 0,0 8.5,8A1.5,1.5 0 0,0 7,9.5A1.5,1.5 0 0,0 8.5,11M15.5,11A1.5,1.5 0 0,0 17,9.5A1.5,1.5 0 0,0 15.5,8A1.5,1.5 0 0,0 14,9.5A1.5,1.5 0 0,0 15.5,11M12,20A8,8 0 0,1 4,12A8,8 0 0,1 12,4A8,8 0 0,1 20,12A8,8 0 0,1 12,20M12,2C6.47,2 2,6.5 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"
              ></path>
            </svg>
          </label>
          <label for="neutral" class="lb">
            <input
              @v-model="option.state"
              type="radio"
              name="rating"
              class="neutral"
              id="neutral"
              value="中评"
            />
            <svg
              class="svg"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              version="1.1"
              width="100%"
              height="100%"
              viewBox="0 0 24 24"
            >
              <path
                d="M8.5,11A1.5,1.5 0 0,1 7,9.5A1.5,1.5 0 0,1 8.5,8A1.5,1.5 0 0,1 10,9.5A1.5,1.5 0 0,1 8.5,11M15.5,11A1.5,1.5 0 0,1 14,9.5A1.5,1.5 0 0,1 15.5,8A1.5,1.5 0 0,1 17,9.5A1.5,1.5 0 0,1 15.5,11M12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22C6.47,22 2,17.5 2,12A10,10 0 0,1 12,2M9,14H15A1,1 0 0,1 16,15A1,1 0 0,1 15,16H9A1,1 0 0,1 8,15A1,1 0 0,1 9,14Z"
              ></path>
            </svg>
          </label>
          <label for="super-sad" class="lb">
            <input
              @v-model="option.state"
              type="radio"
              name="rating"
              class="super-sad"
              id="super-sad"
              value="差评"
            />
            <svg class="svg" viewBox="0 0 24 24">
              <path
                d="M12,2C6.47,2 2,6.47 2,12C2,17.53 6.47,22 12,22A10,10 0 0,0 22,12C22,6.47 17.5,2 12,2M12,20A8,8 0 0,1 4,12A8,8 0 0,1 12,4A8,8 0 0,1 20,12A8,8 0 0,1 12,20M16.18,7.76L15.12,8.82L14.06,7.76L13,8.82L14.06,9.88L13,10.94L14.06,12L15.12,10.94L16.18,12L17.24,10.94L16.18,9.88L17.24,8.82L16.18,7.76M7.82,12L8.88,10.94L9.94,12L11,10.94L9.94,9.88L11,8.82L9.94,7.76L8.88,8.82L7.82,7.76L6.76,8.82L7.82,9.88L6.76,10.94L7.82,12M12,14C9.67,14 7.69,15.46 6.89,17.5H17.11C16.31,15.46 14.33,14 12,14Z"
              ></path>
            </svg>
          </label>
        </div>
        <button class="submit" @click="upOpinion">Submit</button>
      </div>
    </form>
  </div>

  <!-- 底部按钮组 -->
  <div class="button-container">
    <!-- 首页 -->
    <button class="button" @click="goIndex">
      <svg
        class="icon"
        stroke="currentColor"
        fill="currentColor"
        stroke-width="0"
        viewBox="0 0 1024 1024"
        height="1em"
        width="1em"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M946.5 505L560.1 118.8l-25.9-25.9a31.5 31.5 0 0 0-44.4 0L77.5 505a63.9 63.9 0 0 0-18.8 46c.4 35.2 29.7 63.3 64.9 63.3h42.5V940h691.8V614.3h43.4c17.1 0 33.2-6.7 45.3-18.8a63.6 63.6 0 0 0 18.7-45.3c0-17-6.7-33.1-18.8-45.2zM568 868H456V664h112v204zm217.9-325.7V868H632V640c0-22.1-17.9-40-40-40H432c-22.1 0-40 17.9-40 40v228H238.1V542.3h-96l370-369.7 23.1 23.1L882 542.3h-96.1z"
        ></path>
      </svg>
    </button>
    <!-- 用户 -->
    <button class="button" @click="getUserInfo">
      <svg
        class="icon"
        stroke="currentColor"
        fill="currentColor"
        stroke-width="0"
        viewBox="0 0 24 24"
        height="1em"
        width="1em"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M12 2.5a5.5 5.5 0 0 1 3.096 10.047 9.005 9.005 0 0 1 5.9 8.181.75.75 0 1 1-1.499.044 7.5 7.5 0 0 0-14.993 0 .75.75 0 0 1-1.5-.045 9.005 9.005 0 0 1 5.9-8.18A5.5 5.5 0 0 1 12 2.5ZM8 8a4 4 0 1 0 8 0 4 4 0 0 0-8 0Z"
        ></path>
      </svg>
    </button>
    <!-- 意见 -->
    <button class="button" @click="showOpinion">
      <svg
        t="1715875309501"
        class="icon"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="2960"
        width="18"
        height="18"
      >
        <path
          d="M329.95328 255.67232 698.1632 255.67232c14.62272 0 26.43968-11.83744 26.43968-26.4192 0-14.58176-11.81696-26.4192-26.43968-26.4192L329.95328 202.83392c-14.60224 0-26.4192 11.83744-26.4192 26.4192C303.53408 243.83488 315.35104 255.67232 329.95328 255.67232L329.95328 255.67232zM329.95328 402.51392l262.43072 0c14.60224 0 26.35776-11.83744 26.35776-26.4192s-11.75552-26.39872-26.35776-26.39872L329.95328 349.696c-14.60224 0-26.4192 11.81696-26.4192 26.39872S315.35104 402.51392 329.95328 402.51392L329.95328 402.51392zM930.01728 319.5904l-66.21184-38.48192L863.80544 82.18624c0-14.76608-11.96032-26.74688-26.70592-26.74688L191.05792 55.43936c-14.7456 0-26.7264 11.96032-26.7264 26.74688l0 198.94272-66.23232 38.48192-52.79744 26.46016 0 30.53568 0 2.78528 0 570.30656 0 0c0 3.62496 0.73728 7.08608 2.08896 10.21952 0.1024 0.26624 0.26624 0.47104 0.38912 0.75776 0.7168 1.51552 1.57696 3.01056 2.60096 4.42368 4.62848 6.41024 11.96032 10.56768 20.3776 10.87488 0.32768 0 0.63488 0.1024 0.96256 0.1024L956.416 976.0768c14.60224 0 26.39872-11.83744 26.39872-26.37824L982.81472 380.19072l0-2.78528 0-28.8768L930.01728 319.5904 930.01728 319.5904zM863.80544 342.2208l62.27968 36.18816-62.27968 36.16768L863.80544 342.2208 863.80544 342.2208zM234.27072 108.2368l559.57504 0c13.12768 0 17.18272 4.01408 17.18272 17.1008l0 319.91808-296.96 172.52352-296.93952-172.52352L217.12896 125.35808C217.14944 112.25088 221.184 108.2368 234.27072 108.2368L234.27072 108.2368zM164.33152 414.55616l-62.27968-36.16768 62.27968-36.18816L164.33152 414.55616 164.33152 414.55616zM930.01728 923.21792 98.11968 923.21792 98.11968 437.20704l402.65728 233.8816c0.36864 0.3072 0.8192 0.4096 1.2288 0.59392 0.59392 0.32768 1.16736 0.53248 1.76128 0.79872 0.98304 0.43008 1.96608 0.79872 2.9696 1.1264 0.65536 0.16384 1.29024 0.26624 1.96608 0.43008 0.96256 0.2048 1.92512 0.36864 2.92864 0.45056 0.8192 0.04096 1.6384 0.04096 2.4576 0.04096s1.6384 0 2.4576-0.04096c0.98304-0.1024 1.9456-0.26624 2.92864-0.45056 0.65536-0.18432 1.31072-0.28672 1.96608-0.43008 1.00352-0.32768 1.98656-0.69632 2.99008-1.1264 0.59392-0.26624 1.16736-0.47104 1.76128-0.79872 0.4096-0.2048 0.83968-0.3072 1.2288-0.59392l402.6368-233.8816L930.05824 923.21792 930.01728 923.21792zM930.01728 923.21792"
          fill="#ffffff"
          p-id="2961"
        ></path>
      </svg>
    </button>
  </div>

  <el-dialog
    style="min-height: 780px; padding: 0; min-width: 760px"
    :show-close="false"
    v-model="dialogVisibleForUser"
    width="50%"
  >
    <el-row>
      <el-col
        :span="7"
        style="background-color: rgb(227, 241, 255, 0.6); height: 100%"
      >
        <div class="common-layout">
          <el-container>
            <!-- 头像 -->
            <el-header style="height: 100%">
              <el-avatar
                @click="isUpdateAvatar = true"
                class="avatar"
                :src="
                  studentVO.picture != null
                    ? studentVO.picture
                    : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                "
              />
            </el-header>
            <!-- 按钮 -->
            <el-main style="min-height: 440px">
              <div class="select">
                <button id="active" class="container" @click="select(0)">
                  <svg
                    t="1716544041685"
                    class="pin"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="3493"
                    width="21"
                    height="21"
                  >
                    <path
                      d="M695.936 652.8l42.88-42.88a328.064 328.064 0 0 0-155.776-89.6 212.608 212.608 0 0 0 119.424-179.2 49.28 49.28 0 0 0 0-17.28C693.76 216.32 614.4 128 507.52 128s-194.56 91.776-195.712 203.904a206.848 206.848 0 0 0 120.448 188.288C282.752 555.904 174.336 697.6 170.24 862.336c-1.024 40.448 59.136 40.448 60.288 0 3.968-158.336 122.752-288 276.992-288.768A262.912 262.912 0 0 1 695.936 652.8zM371.968 332.8c0-182.272 262.912-180.864 271.104 0-8.32 179.2-270.336 181.76-271.104-0.896zM723.2 902.4h-89.6a25.6 25.6 0 0 1-25.6-25.6v-89.6a25.6 25.6 0 0 1 7.552-18.048l166.4-166.4a25.6 25.6 0 0 1 36.224 0l89.6 89.6a25.6 25.6 0 0 1 0 36.224l-166.4 166.4a25.6 25.6 0 0 1-18.176 7.424z m-64-51.2h53.376l140.8-140.8-53.376-53.376-140.8 140.8z"
                      p-id="3494"
                      fill="#2c2c2c"
                    ></path>
                  </svg>
                </button>
                <button class="container" @click="select(1)">
                  <svg
                    t="1716545680314"
                    class="pin"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="4856"
                    width="18"
                    height="18"
                  >
                    <path
                      d="M282.947368 323.368421V265.620211C282.947368 141.069474 385.670737 40.421053 512 40.421053c126.329263 0 229.052632 100.648421 229.052632 225.199158h-80.842106C660.210526 186.071579 594.027789 121.263158 512 121.263158s-148.210526 64.808421-148.210526 144.357053V323.368421H700.631579v80.842105H161.684211v485.052632h700.631578V538.947368h80.842106v431.157895H80.842105V323.368421h202.105263z m247.754106 437.894737l-57.236211-57.128421 410.570105-411.405474 57.236211 57.101474L530.701474 761.263158z"
                      fill="#333333"
                      p-id="4857"
                    ></path>
                  </svg>
                </button>
              </div>
            </el-main>
            <!-- 提交 -->
            <el-footer style="min-height: 120px">
              <div class="dialog-left-footer">
                <el-button
                  type="primary"
                  class="btn"
                  plain
                  round
                  @click="show == '0' ? updateStudent() : updatePwd()"
                  >修改</el-button
                >
              </div>
            </el-footer>
          </el-container>
        </div>
      </el-col>
      <el-col :span="17" style="">
        <div class="common-layout">
          <el-container>
            <!-- 表格 -->
            <el-header style="height: 100%">
              <el-descriptions
                title="用户信息"
                :column="2"
                border="true"
                style="width: 89%; margin: 5%"
              >
                <el-descriptions-item label="姓名" width="160px">
                  {{ studentVO.name }}
                </el-descriptions-item>
                <el-descriptions-item label="性别" width="100px">
                  {{ studentVO.sex }}
                </el-descriptions-item>
                <el-descriptions-item label="用户名">
                  {{ studentVO.username }}
                </el-descriptions-item>
                <el-descriptions-item label="身份"> 学生 </el-descriptions-item>
                <el-descriptions-item label="出生日期">
                  {{ studentVO.birth }}
                </el-descriptions-item>
                <template #extra>
                  <el-button
                    type="info"
                    plain
                    circle
                    @click="dialogVisibleForUser = false"
                  >
                    <el-icon><CloseBold /></el-icon>
                  </el-button>
                </template> </el-descriptions
            ></el-header>
            <!-- 表单 -->
            <div v-if="show == '0'">
              <el-main
                style="height: 100%; border-top: 3px dashed rgb(238, 247, 255)"
              >
                <el-card style="width: 89%; margin: 2% 5%" shadow="never">
                  <el-descriptions :column="2">
                    <el-descriptions-item label="学校">
                      {{ studentVO.school }}</el-descriptions-item
                    >
                    <el-descriptions-item label="院系">
                      {{ studentVO.department }}</el-descriptions-item
                    >
                    <el-descriptions-item label="专业">
                      {{ studentVO.major }}</el-descriptions-item
                    >
                    <el-descriptions-item label="班级">
                      {{ studentVO.teamName }}</el-descriptions-item
                    >
                    <el-descriptions-item label="学号">
                      {{ studentVO.number }}</el-descriptions-item
                    >
                  </el-descriptions>
                </el-card>
              </el-main>
              <el-footer
                style="height: 100%; border-top: 3px dashed rgb(238, 247, 255)"
              >
                <el-form
                  label-width="auto"
                  :model="studentVO"
                  status-icon
                  style="width: 89%; height: 100%; margin: 5%; margin-top: 7%"
                >
                  <el-form-item label="电话">
                    <el-input
                      v-model="studentVO.phone"
                      show-word-limit="true"
                      maxlength="11"
                    />
                  </el-form-item>
                  <el-form-item label="邮箱">
                    <el-input
                      v-model="studentVO.email"
                      show-word-limit="true"
                      maxlength="50"
                    />
                  </el-form-item>
                  <el-form-item label="住址">
                    <el-input
                      v-model="studentVO.address"
                      show-word-limit="true"
                      maxlength="200"
                    />
                  </el-form-item>
                  <el-form-item label="介绍">
                    <el-input
                      v-model="studentVO.introduce"
                      placeholder="用简短的话语介绍一下自己..."
                      class="r_n"
                      show-word-limit="true"
                      maxlength="200"
                      type="textarea"
                    />
                  </el-form-item>
                </el-form>
              </el-footer>
            </div>
            <div v-else>
              <el-main
                style="height: 100%; border-top: 3px dashed rgb(238, 247, 255)"
              >
                <el-form
                  :rules="rulePassword"
                  ref="ruleFormRef"
                  label-width="auto"
                  :model="passwordModel"
                  :inline="true"
                  status-icon
                  style="width: 90%; margin: 5%"
                >
                  <el-form-item
                    prop="oldPassword"
                    label="原密码"
                    style="width: 90%"
                  >
                    <el-input
                      v-model="passwordModel.oldPassword"
                      show-password="true"
                    />
                  </el-form-item>
                  <el-form-item
                    prop="newPassword"
                    label="新密码"
                    style="width: 90%"
                  >
                    <el-input
                      v-model="passwordModel.newPassword"
                      show-password="true"
                    />
                  </el-form-item>
                  <el-form-item
                    prop="rePassword"
                    label="确认密码"
                    style="width: 90%"
                  >
                    <el-input
                      v-model="passwordModel.rePassword"
                      show-password="true"
                    />
                  </el-form-item>
                </el-form>
              </el-main>
            </div>
          </el-container>
        </div>
        <div class="grid-content ep-bg-purple-light"
      /></el-col>
    </el-row>
  </el-dialog>

  <!-- 修改头像 -->
  <el-dialog v-model="isUpdateAvatar" title="头像" width="500">
    <el-upload
      class="upload-demo"
      drag
      v-model:file-list="fileList"
      style="width: 100%; height: 100%"
      action="/api/uploadImg"
      multiple
      ref="uploadRef"
      :auto-upload="false"
      limit="1"
      :on-success="handleSuccess"
      :headers="{ Authorization: token }"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">拖拽上传 or <em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip" style="margin: 12px 2px 20px 4px">
          图片大小请不要超过 10 MB
          <el-button
            size="small"
            style="float: right; cursor: pointer"
            type="success"
            :icon="Check"
            @click="submitUpload"
            text
            bg
          />
        </div>
      </template>
    </el-upload>
  </el-dialog>
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
  box-shadow: 0px 0px 2px 1px rgb(221.7, 222.6, 224.4);
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

.icon {
  font-size: 20px;
}

// 意见反馈
.opinion-box {
  position: fixed;
  bottom: 4%;
  right: 0;
  z-index: 99;
  opacity: 0;
}

.form {
  position: relative;
  align-items: flex-start;
  flex-direction: column;
  gap: 10px;
  width: 350px;
  background-color: white;
  border: 2px solid rgb(134, 162, 246);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 15px 30px -10px rgba(27, 26, 26, 0.315);
}

.form .title {
  position: relative;
  margin-bottom: 16px;
  color: royalblue;
  font-size: 20px;
  font-weight: 700;
  line-height: 20px;
  align-items: center;
  justify-content: center;
}

.form .close-opinion {
  position: absolute;
  top: 4%;
  right: 4%;
  cursor: pointer;
}

.form textarea {
  margin-bottom: 12px;
  border-radius: 8px;
  height: 100px;
  width: 90%;
  resize: none;
  outline: 0;
  padding: 10px 12px;
  border: 1px solid rgb(219, 213, 213);
}

.form .bottom {
  height: 32px;
  width: 100%;
}

.form .submit {
  float: right;
  margin-right: 4px;
  padding: 10px;
  outline: 0;
  border: 0;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background-color: royalblue;
  color: #fff;
  cursor: pointer;
}

.rating {
  float: left;
  border: 2px dashed rgb(134, 162, 246);
  /* background-color: rgba(0, 0, 0, 0.3); */
  padding: 0.4rem 0.4rem 0.1rem 0.4rem;
  border-radius: 2.2rem;

  input {
    cursor: pointer;
  }

  label {
    cursor: pointer;
  }
}

.rating .svg {
  fill: rgb(182, 175, 175, 0.7);
  height: 1.5rem;
  width: 1.7rem;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

#radios label {
  position: relative;
}

.super-happy,
[type="radio"] {
  position: absolute;
  opacity: 0;
}

.input,
[type="radio"] + svg {
  -webkit-transition: all 0.2s;
  transition: all 0.2s;
}

.super-happy[class="super-happy"]:hover + svg,
.super-happy[class="super-happy"]:checked + svg,
.super-happy[class="super-happy"]:focus + svg {
  fill: rgb(0, 204, 79);
  transition: 1.5s;
}

.neutral[class="neutral"]:hover + svg,
.neutral[class="neutral"]:checked + svg,
.neutral[class="neutral"]:focus + svg {
  fill: rgb(232, 214, 0);
  transition: 1.5s;
}

.super-sad[class="super-sad"]:hover + svg,
.super-sad[class="super-sad"]:checked + svg,
.super-sad[class="super-sad"]:focus + svg {
  fill: rgb(239, 42, 16);
  transition: 1.5s;
}

::v-deep .r_n {
  .el-textarea__inner {
    resize: none;
    height: 100px;
  }
}

.avatar {
  margin: 16%;
  margin-top: 24%;
  width: 64%;
  height: auto;
  border-radius: 20px;
  cursor: pointer;
  box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.09);
}

.select {
  margin: 16%;
  width: 64%;
  height: 40px;
}

.pin {
  margin-right: 4px;
}

.container {
  margin-left: 2px;
  margin-bottom: 20px;
  width: 98%;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  font-size: 13px;
  line-height: 50px;
  border: 0px;
  background-color: rgba(206, 221, 235, 0.5);
}

.container:hover {
  background-color: rgba(206, 221, 235, 0.5);
}

.container:active {
  transform: scale(0.9);
}

#active {
  background-color: rgb(199, 223, 248, 1);
  box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.09);
}

.btn {
  margin: 16%;
  // margin-top: 64%;
  width: 64%;
}
</style>
