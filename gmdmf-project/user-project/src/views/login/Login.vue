<script lang="ts" setup>
import { ref, onMounted, onBeforeMount, nextTick } from "vue";
import { useRouter } from "vue-router";
import { ElLoading, ElMessage } from "element-plus";

import dentify from "./CodeTools.vue";
import { loginStudentService, getStudentInfoService } from "@/api/Student.js";
import { loginTeacherService, getTeacherInfoService } from "@/api/Teacher.js";
import { sendEmailCodeService, updatePasswordService } from "@/api/Login";
import { useTokenStore } from "@/stores/token";
import { useUserInfoStore } from "@/stores/userInfo";
import { loginManageService } from "@/api/Manage";

const tokenStore = useTokenStore();
const router = useRouter();
const LoginIndex = ref(0);
const LoginVisible = ref(false);
const PwdVisible = ref(false);
const identifyCode = ref("");
const identifyCodes = ref(
  "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
const userInfoStore = useUserInfoStore();

const index = () => {
  router.push("/");
};

const register = () => {
  router.push("/register");
};

const updatePwd = () => {
  PwdVisible.value = true;
};

const loginDTO = ref({
  name: "",
  email: "",
  username: "",
  password: "",
  rePassword: "",
  code: "",
  rule: "",
});

// 自定义校验规则函数：rule-规则 | value-值 | callback-回调函数
const checkPassword = function (rule, value, callback) {
  if (value !== loginDTO.value.password) {
    callback(new Error("两次输入的密码不一致！"));
  } else {
    callback();
  }
};

const clean = () => {
  loginDTO.value = {
    name: "",
    email: "",
    username: "",
    password: "",
    rePassword: "",
    code: "",
    rule: "",
  };
  text.value = "";
};
const text = ref("");
// 登录验证码校验
const checkCode = () => {
  let item = document.querySelector(".code-text");

  if (text.value.toLowerCase() === identifyCode.value.toLowerCase()) {
    // 验证码正确
    item.classList.add("code-text-color");
  } else {
    // 验证码错误
    item.classList.remove("code-text-color");
  }
};

// 校验是否登录过
const loginPass = (index) => {
  clean();
  if (
    userInfoStore.userInfo.rule == "学生" &&
    localStorage.getItem("token") &&
    index == 1
  ) {
    router.push("/student");
    return true;
  } else if (
    userInfoStore.userInfo.rule == "教师" &&
    localStorage.getItem("token") &&
    index == 2
  ) {
    router.push("/teacher");
    return true;
  } else if (index == 3) {
    location.href = "http://localhost:5175/login";
    return true;
  }
  return false;
};

// 登录
const loginSubmit = async (e) => {
  // 取消默认行为
  e.preventDefault();

  // 1-学生 | 2-教师 | 3-管理
  let result = null;
  if (!document.querySelector(".code-text-color")) {
    ElMessage.info("验证码校验不通过");
    return;
  }

  // 发送请求
  if (LoginIndex.value == 1) {
    // 发送请求
    result = await loginStudentService(loginDTO.value);
    localStorage.setItem("token", result.data.data);
    // 提示框
    ElMessage.success(result.data.message ? result.data.message : "登录成功");
    result = await getStudentInfoService();
    // 保存用户信息
    userInfoStore.setUserInfo(result.data.data);
    // 本地存储 token
    clean();
    // 跳转
    router.push("/student");
  } else if (LoginIndex.value == 2) {
    // 发送请求
    result = await loginTeacherService(loginDTO.value);
    localStorage.setItem("token", result.data.data);
    // 提示框
    ElMessage.success(result.data.message ? result.data.message : "登录成功");
    result = await getTeacherInfoService();
    // 保存用户信息
    userInfoStore.setUserInfo(result.data.data);
    // 本地存储 token
    clean();
    // 跳转
    router.push("/teacher");
  }
};

const loading = ref(true);
let loadingInstance = null;
onMounted(async () => {
  makeCode(identifyCodes.value, 4);
  nextTick(() => {
    setTimeout(() => {
      loadingInstance.close();
      loading.value = false;
      document.body.style.overflow = "visible";
    }, 800);
  });
});

onBeforeMount(() => {
  loadingInstance = ElLoading.service({
    fullscreen: true,
    text: "加载中...",
    background: "rgba(255, 255, 255, 0.7)",
  });
  window.screenTop = 0;
  document.body.style.overflow = "hidden";
});

const showTools = ref(true);

window.addEventListener("resize", function () {
  // 获取屏幕宽度
  const screenWidth = window.innerWidth;
  if (screenWidth < 1100) {
    showTools.value = false;
  } else {
    showTools.value = true;
  }
});

const refreshCode = () => {
  identifyCode.value = "";
  makeCode(identifyCodes.value, 4);
};

//获取验证码的值
const makeCode = (o, l) => {
  for (let i = 0; i < l; i++) {
    //通过循环获取字符串内随机几位
    identifyCode.value +=
      identifyCodes.value[randomNum(0, identifyCodes.value.length)];
  }
};

const randomNum = (min, max) => {
  return Math.floor(Math.random() * (max - min) + min);
};

// 忘记密码
const btnValue = ref(false);
const sendEmailText = ref("Send");

// 发送邮件验证码
const sendEmail = async (e) => {
  if (!loginDTO.value.rule.trim()) {
    ElMessage.error("身份不能为空");
    return;
  }
  if (!loginDTO.value.username.trim()) {
    ElMessage.error("用户名不能为空");
    return;
  }
  if (!loginDTO.value.email.trim()) {
    ElMessage.error("邮箱不能为空");
    return;
  }

  e.preventDefault();
  btnValue.value = true;
  let result = await sendEmailCodeService(loginDTO.value);
  let i = 60;
  const id = setInterval(() => {
    sendEmailText.value = `${i--} 秒`;
    if (i == 0) {
      clearTimeout(id);
      sendEmailText.value = "Send";
      btnValue.value = false;
    }
  }, 1500);
  ElMessage.success(
    result.data.message ? result.data.message : "验证码发送成功"
  );
};

const check = () => {
  if (!loginDTO.value.rule.trim()) {
    ElMessage.error("身份不能为空");
    return true;
  }
  if (!loginDTO.value.username.trim()) {
    ElMessage.error("用户名不能为空");
    return true;
  }
  if (!loginDTO.value.email.trim()) {
    ElMessage.error("邮箱不能为空");
    return true;
  }

  if (!loginDTO.value.password.trim()) {
    ElMessage.error("密码不能为空");
    return true;
  }
  if (!loginDTO.value.rePassword.trim()) {
    ElMessage.error("请确认密码");
    return true;
  }
  return false;
};

// 提交修改
const updatePwdSubmit = async (e) => {
  e.preventDefault();
  if (check()) return;
  let result = await updatePasswordService(loginDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "修改密码成功");
  PwdVisible.value = false;
  clean();
};
</script>

<template>
  <el-card
    v-show="!loading"
    style="
      width: 100vw;
      min-width: 1080px;
      height: 100vh;
      padding: 7.5% 0 0 8%;
      background-color: #f2f6fc;
    "
  >
    <el-container style="">
      <!-- 右中 -->
      <el-main class="login-box">
        <!-- 学生 -->
        <div
          class="card2 wallet2 login"
          @click="
            loginPass(1) ? '' : (LoginVisible = true);
            refreshCode();
            LoginIndex = 1;
          "
        >
          <div class="overlay2"></div>
          <div class="circle2">
            <img
              src="@/assets/学生.png"
              style="z-index: 100; border-radius: 50%; transform: translateZ(0)"
              width="118px"
              height="118px"
              alt=""
            />
          </div>
          <div class="photo2"></div>
          <p>学生</p>
        </div>
        <!-- 教师 -->
        <div
          class="card3 wallet3 login"
          @click="
            loginPass(2) ? '' : (LoginVisible = true);
            refreshCode();
            LoginIndex = 2;
          "
        >
          <div class="overlay3"></div>
          <div class="circle3">
            <img
              src="@/assets/教师.png"
              style="z-index: 100; border-radius: 50%; transform: translateZ(0)"
              width="118px"
              height="118px"
              alt=""
            />
          </div>
          <div class="photo3"></div>
          <p>教师</p>
        </div>
        <!-- 管理 -->
        <div
          class="card1 wallet1 login"
          @click="
            loginPass(3) ? '' : (LoginVisible = true);
            refreshCode();
            LoginIndex = 3;
          "
        >
          <div class="overlay1"></div>
          <div class="circle1">
            <img
              src="@/assets/管理.png"
              style="z-index: 100; border-radius: 50%; transform: translateZ(0)"
              width="118px"
              height="118px"
              alt=""
            />
          </div>
          <div class="photo1"></div>
          <p>管理员</p>
        </div>

        <div class="tools-box" v-show="showTools == true">
          <!-- 首页 -->
          <div class="tools1 wallet4 tools" @click="index">
            <div class="overlay"></div>
            <div class="circle">
              <img
                src="@/assets/首页.png"
                style="
                  z-index: 100;
                  border-radius: 50%;
                  transform: translateZ(0);
                "
                width="70px"
                height="60px"
                alt=""
              />
            </div>
            <p class="txt1">首</p>
            <p class="txt2">页</p>
          </div>
          <!-- 注册 -->
          <div class="tools2 wallet5 tools" @click="register">
            <div class="overlay"></div>
            <div class="circle">
              <img
                src="@/assets/注册.png"
                style="
                  z-index: 100;
                  border-radius: 50%;
                  transform: translateZ(0);
                "
                width="70px"
                height="60px"
                alt=""
              />
            </div>
            <p class="txt1">注</p>
            <p class="txt2">册</p>
          </div>
          <!-- 密码 -->
          <div class="tools3 wallet6 tools" @click="updatePwd">
            <div class="overlay"></div>
            <div class="circle">
              <img
                src="@/assets/忘记密码.png"
                style="
                  z-index: 100;
                  border-radius: 50%;
                  transform: translateZ(0);
                "
                width="70px"
                height="60px"
                alt=""
              />
            </div>
            <p class="txt1">密</p>
            <p class="txt2">码</p>
          </div>
        </div>
      </el-main>
    </el-container>
  </el-card>

  <!-- 登录 -->
  <el-dialog
    style="
      width: 0;
      height: 0;
      border: none;
      background: rgb(122, 122, 122);
      box-shadow: none;
    "
    :show-close="false"
    v-model="LoginVisible"
    width="50%"
  >
    <form class="form">
      <p class="title">登录</p>
      <p class="message">欢迎回来，准备开始今天的学习吧！≧ω≦</p>
      <label>
        <input
          required="true"
          v-model="loginDTO.username"
          placeholder=""
          type="username"
          class="input"
          minlength="2"
          maxlength="20"
        />
        <span>用户名</span>
      </label>
      <label>
        <input
          required
          v-model="loginDTO.password"
          placeholder=""
          type="password"
          class="input"
          minlength="4"
          maxlength="20"
        />
        <span>密码</span>
      </label>
      <label>
        <input
          required
          placeholder=""
          type="code"
          class="input code"
          @input="checkCode"
          v-model="text"
        />
        <span class="code-text" style="color: gray">验证码</span>
        <div
          class="login-code"
          style="
            float: right;
            margin-top: 4px;
            background: rgba(65, 105, 225, 0.7);
          "
          width="100%"
          @click="refreshCode"
        >
          <!--验证码组件-->
          <dentify :identifyCode="identifyCode"></dentify>
        </div>
      </label>
      <button @click="loginSubmit" class="submit">提交</button>
      <p class="register">
        没有账号？快注册一个吧！<a @click="register">注册</a>
      </p>
    </form>
  </el-dialog>

  <!-- 忘记密码 -->
  <el-dialog
    style="
      width: 0;
      height: 0;
      border: none;
      background: rgb(122, 122, 122);
      box-shadow: none;
    "
    :show-close="false"
    v-model="PwdVisible"
    width="50%"
  >
    <form class="form">
      <p class="title">忘记密码</p>
      <p class="message">通过姓名、学号和邮箱来寻回密码</p>
      <label>
        <input
          required
          placeholder=""
          v-model="loginDTO.rule"
          type="text"
          class="input"
        />
        <span>身份</span>
      </label>
      <label>
        <input
          required
          placeholder=""
          v-model="loginDTO.username"
          type="text"
          class="input"
        />
        <span>用户名</span>
      </label>
      <label>
        <input
          required
          placeholder=""
          v-model="loginDTO.email"
          type="text"
          class="input"
        />
        <span>邮箱</span>
      </label>
      <label>
        <input
          required
          placeholder=""
          v-model="loginDTO.password"
          type="password"
          class="input"
        />
        <span>新密码</span>
      </label>
      <label>
        <input
          required
          placeholder=""
          v-model="loginDTO.rePassword"
          type="password"
          class="input"
        />
        <span>确认密码</span>
      </label>
      <label>
        <input
          required
          placeholder=""
          v-model="loginDTO.code"
          type="code"
          class="input code"
        />
        <span>验证码</span>
        <div
          class="login-code"
          style="float: right"
          width="100%"
          @click="refreshCode"
        >
          <button class="Btn" :disabled="btnValue" @click="sendEmail">
            {{ sendEmailText }}
            <!-- <svg class="svg" viewBox="0 0 512 512">
              <path
                d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
              ></path>
            </svg> -->
            <svg
              t="1717811538236"
              class="svg"
              viewBox="0 0 1024 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="4271"
            >
              <path
                d="M915.515273 142.819385 98.213046 458.199122c-46.058539 17.772838-44.90475 43.601756 2.348455 57.622994l197.477685 58.594874 80.292024 238.91085c10.51184 31.277988 37.972822 37.873693 61.462483 14.603752l103.584447-102.611545 204.475018 149.840224c26.565749 19.467242 53.878547 9.222132 61.049613-23.090076l149.210699-672.34491C965.264096 147.505054 946.218922 130.971848 915.515273 142.819385zM791.141174 294.834331l-348.61988 310.610267c-6.268679 5.58499-11.941557 16.652774-12.812263 24.846818l-15.390659 144.697741c-1.728128 16.24808-7.330491 16.918483-12.497501 1.344894l-67.457277-203.338603c-2.638691-7.954906 0.975968-17.705389 8.022355-21.931178l442.114555-265.181253C812.67481 268.984974 815.674251 272.975713 791.141174 294.834331z"
                p-id="4272"
                fill="#ffffff"
              ></path>
            </svg>
          </button>
        </div>
      </label>
      <button @click="updatePwdSubmit" class="submit">提交</button>
      <p class="register">
        没有账号？快注册一个吧！<a @click="register">注册</a>
      </p>
    </form>
  </el-dialog>
</template>

<style lang="scss" scoped>
body {
  background-color: #f2f6fc;
}
.code-text-color {
  color: green !important;
}

.box {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

.right-box {
  height: 80%;
  margin: auto;
  overflow: hidden;
}

.login {
  float: left;
  margin-right: 3.6%;
}

.common-layout {
  margin-top: 60px;
  z-index: 1;
}

.bg {
  position: fixed;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: -100;
  background-color: #e1e6ec;
  background-image: linear-gradient(-180deg, #ffffff 2%, #f1f6fc 96%),
    radial-gradient(
      at 50% 100%,
      rgba(235, 238, 245, 0.2) 0%,
      rgba(228, 231, 237, 0.5) 200%
    );
  background-blend-mode: normal, color-burn;
}

// 管理
.wallet1 {
  --bg-color1: #ceb2fc;
  --bg-color-light1: #f0e7ff;
  --text-color-hover1: #fff;
  --box-shadow-color1: rgba(206, 178, 252, 0.48);
}

.card1 {
  width: 240px;
  height: 405px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  border: 2px solid #d6e4ff;
  box-shadow: 4px 4px 8px 0px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
}

.photo1 {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.9;
  background: #ceb2fc url(@/assets/管理背景.png) no-repeat scroll -16px center;
}

.card1:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0px 6px 12px rgba(221, 214, 255, 1);
}

.card1:hover .overlay1 {
  transform: scale(4) translateZ(0);
}

.card1:hover .circle1 {
  border-color: var(--bg-color-light1);
  background: var(--bg-color1);
}

.card1:hover .circle1:after {
  background: var(--bg-color-light1);
}

.card1:hover p {
  color: var(--text-color-hover1);
}

.card1 p {
  position: absolute;
  top: 4%;
  font-size: 20px;
  color: rgb(105, 76, 152);
  z-index: 99;
  transition: color 0.3s ease-out;
  font-weight: 700;
  font-family: "宋体";
}

.circle1 {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: #fff;
  border: 2px dashed var(--bg-color1);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 20%;
  z-index: 99;
  transition: all 0.3s ease-out;
}

.circle1:after {
  content: "";
  width: 121px;
  height: 121px;
  display: block;
  position: absolute;
  background: var(--bg-color1);
  border-radius: 50%;
  transition: opacity 0.3s ease-out;
}

.overlay1 {
  position: absolute;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: var(--bg-color1);
  top: 20%;
  z-index: 4;
  transition: transform 0.3s ease-out;
}

// 学生
.wallet2 {
  --bg-color2: #95d475;
  --bg-color-light2: #e1f3d8;
  --text-color-hover2: #ffffff;
  --box-shadow-color2: rgba(184, 249, 211, 0.48);
}

.card2 {
  width: 240px;
  height: 405px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  border: 2px solid #e1f3d8;
  box-shadow: 4px 4px 8px 0px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
}

.photo2 {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.9;
  background: #95d475 url(@/assets/学生背景.png) no-repeat scroll -24px center;
}

.card2:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0px 6px 12px rgba(225, 243, 216, 1);
}

.card2:hover .overlay2 {
  transform: scale(4) translateZ(0);
}

.card2:hover .circle2 {
  border-color: var(--bg-color-light2);
  background: var(--bg-color2);
}

.card2:hover .circle2:after {
  background: var(--bg-color-light2);
}

.card2:hover p {
  color: var(--text-color-hover2);
}

.card2 p {
  position: absolute;
  top: 4%;
  font-size: 20px;
  color: rgb(85, 101, 77);
  z-index: 99;
  transition: color 0.3s ease-out;
  font-weight: 700;
  font-family: "宋体";
}

.circle2 {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: #fff;
  border: 2px dashed var(--bg-color2);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 20%;
  z-index: 99;
  transition: all 0.3s ease-out;
}

.circle2:after {
  content: "";
  width: 121px;
  height: 121px;
  display: block;
  position: absolute;
  background: var(--bg-color2);
  border-radius: 50%;
  transition: opacity 0.3s ease-out;
}

.overlay2 {
  position: absolute;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: var(--bg-color2);
  top: 20%;
  z-index: 4;
  transition: transform 0.3s ease-out;
}
// 教师
.wallet3 {
  --bg-color3: #ffcf6f;
  --bg-color-light3: #fff6d8;
  --text-color-hover3: #fff;
  --box-shadow-color3: rgba(255, 215, 97, 0.48);
}

.card3 {
  width: 240px;
  height: 405px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  border: 2px solid #faecd8;
  box-shadow: 4px 4px 8px 0px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
}

.photo3 {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.9;
  background: #ffcf6f url(@/assets/教师背景.png) no-repeat scroll 0px center;
}

.card3:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0px 6px 12px rgba(250, 236, 216, 1);
}

.card3:hover .overlay3 {
  transform: scale(4) translateZ(0);
}

.card3:hover .circle3 {
  border-color: var(--bg-color-light3);
  background: var(--bg-color3);
}

.card3:hover .circle3:after {
  background: var(--bg-color-light3);
}

.card3:hover p {
  color: var(--text-color-hover3);
}

.card3 p {
  position: absolute;
  top: 4%;
  font-size: 20px;
  color: rgb(155, 100, 17);
  z-index: 99;
  transition: color 0.3s ease-out;
  font-weight: 700;
  font-family: "宋体";
}

.circle3 {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: #fff;
  border: 2px dashed var(--bg-color3);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 20%;
  z-index: 99;
  transition: all 0.3s ease-out;
}

.circle3:after {
  content: "";
  width: 121px;
  height: 121px;
  display: block;
  position: absolute;
  background: var(--bg-color3);
  border-radius: 50%;
  transition: opacity 0.3s ease-out;
}

.overlay3 {
  position: absolute;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: var(--bg-color3);
  top: 20%;
  z-index: 4;
  transition: transform 0.3s ease-out;
}

// tools
.tools {
  margin-bottom: 20px;
}

.wallet4 {
  --bg-color: #a0cfff;
  --bg-color-light: #d9ecff;
  --text-color-hover: #ffffff;
  --box-shadow-color: #a0cfff;
}

.tools1 {
  max-width: 320px;
  height: 120px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  align-items: center;
  position: relative;
  border: 1px solid #d9ecff;
  box-shadow: 1px 1px 4px 0px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
  background: rgb(236, 245, 255, 0.3);
}

.tools1:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0px 6px 12px rgba(217, 236, 255, 1);
}

.tools1:hover .overlay {
  transform: scale(4.5) translateZ(0);
}

.tools1:hover .circle {
  border-color: var(--bg-color-light);
  background: var(--bg-color);
}

.tools1:hover .circle:after {
  background: var(--bg-color-light);
}

.tools1:hover p {
  color: var(--text-color-hover);
}

.tools1 .txt1 {
  position: absolute;
  font-size: 96px;
  top: -6%;
  left: 37%;
  font-weight: 700;
  font-family: "宋体";
  color: #a0cfff;
  z-index: 100;
}

.tools1 .txt2 {
  position: absolute;
  font-size: 96px;
  top: 29%;
  left: 56%;
  font-weight: 700;
  font-family: "宋体";
  color: #c6e2ff;
  z-index: 98;
}

.tools1 .circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #fff;
  border: 2px dashed var(--bg-color);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 4%;
  z-index: 99;
  transition: all 0.3s ease-out;
}

.tools1 .circle:after {
  content: "";
  width: 80px;
  height: 80px;
  display: block;
  position: absolute;
  background: var(--bg-color);
  border-radius: 50%;
  transition: opacity 0.3s ease-out;
}

.tools1 .overlay {
  position: absolute;
  left: 4%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--bg-color);
  z-index: 4;
  transition: transform 0.3s ease-out;
}

/* 2 */

.wallet5 {
  --bg-color: #f89898;
  --bg-color-light: #fcd3d3;
  --text-color-hover: #ffffff;
  --box-shadow-color: #a0cfff;
}

.tools2 {
  max-width: 320px;
  height: 120px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  align-items: center;
  position: relative;
  border: 1px solid #fde2e2;
  box-shadow: 1px 1px 4px 0px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
  background: rgb(254, 240, 240, 0.3);
}

.tools2:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0px 6px 12px rgba(253, 226, 226, 1);
}

.tools2:hover .overlay {
  transform: scale(4.5) translateZ(0);
}

.tools2:hover .circle {
  border-color: var(--bg-color-light);
  background: var(--bg-color);
}

.tools2:hover .circle:after {
  background: var(--bg-color-light);
}

.tools2:hover p {
  color: var(--text-color-hover);
}

.tools2 .txt1 {
  position: absolute;
  font-size: 96px;
  top: -5%;
  left: 12%;
  font-weight: 700;
  font-family: "宋体";
  color: #f89898;
  z-index: 100;
}

.tools2 .txt2 {
  position: absolute;
  font-size: 96px;
  top: 22%;
  left: 33%;
  font-weight: 700;
  font-family: "宋体";
  color: #fcd3d3;
  z-index: 98;
}

.tools2 .circle {
  margin-left: 4%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #fff;
  border: 2px dashed var(--bg-color);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  right: 4%;
  z-index: 99;
  transition: all 0.3s ease-out;
}

.tools2 .circle:after {
  content: "";
  width: 80px;
  height: 80px;
  display: block;
  position: absolute;
  background: var(--bg-color);
  border-radius: 50%;
  transition: opacity 0.3s ease-out;
}

.tools2 .overlay {
  position: absolute;
  right: 4%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--bg-color);
  z-index: 4;
  transition: transform 0.3s ease-out;
}

/* 3 */

.wallet6 {
  --bg-color: #eebe77;
  --bg-color-light: #f8e3c5;
  --text-color-hover: #ffffff;
  --box-shadow-color: #a0cfff;
}

.tools3 {
  max-width: 320px;
  height: 120px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  align-items: center;
  position: relative;
  border: 1px solid #faecd8;
  box-shadow: 1px 1px 4px 0px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
  background: rgba(253, 246, 236, 0.3);
}

.tools3:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0px 6px 12px rgba(250, 236, 216, 1);
}

.tools3:hover .overlay {
  transform: scale(4.5) translateZ(0);
}

.tools3:hover .circle {
  border-color: var(--bg-color-light);
  background: var(--bg-color);
}

.tools3:hover .circle:after {
  background: var(--bg-color-light);
}

.tools3:hover p {
  color: var(--text-color-hover);
}

.tools3 .txt1 {
  position: absolute;
  font-size: 96px;
  top: -4%;
  left: 37%;
  font-weight: 700;
  font-family: "宋体";
  color: #eebe77;
  z-index: 100;
}

.tools3 .txt2 {
  position: absolute;
  font-size: 94px;
  top: 25%;
  left: 60%;
  font-weight: 700;
  font-family: "宋体";
  color: #f8e3c5;
  z-index: 98;
}

.tools3 .circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #fff;
  border: 2px dashed var(--bg-color);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 4%;
  z-index: 99;
  transition: all 0.3s ease-out;
}

.tools3 .circle:after {
  content: "";
  width: 80px;
  height: 80px;
  display: block;
  position: absolute;
  background: var(--bg-color);
  border-radius: 50%;
  transition: opacity 0.3s ease-out;
}

.tools3 .overlay {
  position: absolute;
  left: 4%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--bg-color);
  z-index: 4;
  transition: transform 0.3s ease-out;
}

// 海浪
.hai-lang {
  position: absolute;
  bottom: 0%;
  width: 100%;
  height: 200px;
  z-index: -99;
  opacity: 0.3;
}

.path-0 {
  animation: pathAnim-0 12s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}

@keyframes pathAnim-0 {
  0% {
    d: path(
      "M 0,400 L 0,150 C 146,170.13333333333333 292,190.26666666666668 461,190 C 630,189.73333333333332 822,169.06666666666666 989,159 C 1156,148.93333333333334 1298,149.46666666666667 1440,150 L 1440,400 L 0,400 Z"
    );
  }
  25% {
    d: path(
      "M 0,400 L 0,150 C 135.59999999999997,175.73333333333335 271.19999999999993,201.46666666666667 456,204 C 640.8000000000001,206.53333333333333 874.8,185.86666666666665 1047,173 C 1219.2,160.13333333333335 1329.6,155.06666666666666 1440,150 L 1440,400 L 0,400 Z"
    );
  }
  50% {
    d: path(
      "M 0,400 L 0,150 C 203.86666666666667,156.13333333333333 407.73333333333335,162.26666666666668 540,153 C 672.2666666666667,143.73333333333332 732.9333333333333,119.06666666666668 871,116 C 1009.0666666666667,112.93333333333332 1224.5333333333333,131.46666666666667 1440,150 L 1440,400 L 0,400 Z"
    );
  }
  75% {
    d: path(
      "M 0,400 L 0,150 C 173.59999999999997,122 347.19999999999993,94 511,105 C 674.8000000000001,116 828.8,166 982,180 C 1135.2,194 1287.6,172 1440,150 L 1440,400 L 0,400 Z"
    );
  }
  100% {
    d: path(
      "M 0,400 L 0,150 C 146,170.13333333333333 292,190.26666666666668 461,190 C 630,189.73333333333332 822,169.06666666666666 989,159 C 1156,148.93333333333334 1298,149.46666666666667 1440,150 L 1440,400 L 0,400 Z"
    );
  }
}

// 忘记密码
.form {
  position: fixed;
  top: 18%;
  left: 50%;
  margin-left: -162px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 345px;
  background-color: #fff;
  padding: 20px;
  border-radius: 20px;
  border: 2px solid rgb(65, 105, 225);
}

.title {
  font-size: 28px;
  color: royalblue;
  font-weight: 600;
  letter-spacing: -1px;
  position: relative;
  display: flex;
  align-items: center;
  padding-left: 10%;
  font-family: "宋体";
  margin-bottom: 8px;
}

.title::before,
.title::after {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  border-radius: 50%;
  left: 0px;
  background-color: royalblue;
}

.title::before {
  width: 18px;
  height: 18px;
  background-color: royalblue;
}

.title::after {
  width: 18px;
  height: 18px;
  animation: pulse 1s linear infinite;
}

.message,
.register {
  color: rgba(88, 87, 87, 0.822);
  font-size: 14px;
}

.register {
  text-align: center;
}

.register a {
  color: royalblue;
  cursor: pointer;
}

.register a:hover {
  text-decoration: underline royalblue;
}

.form label {
  position: relative;
}

.form label .input {
  width: 300px;
  padding: 10px 10px 20px 10px;
  outline: 0;
  border: 1px solid rgba(105, 105, 105, 0.397);
  border-radius: 10px;
}

.form label .input + span {
  position: absolute;
  left: 10px;
  top: 15px;
  color: grey;
  font-size: 0.9em;
  cursor: text;
  transition: 0.3s ease;
}

.form label .input:focus + span,
.form label .input:invalid + span {
  top: 30px;
  font-size: 0.7em;
  font-weight: 600;
}

.form label .input:valid + span {
  top: 30px;
  font-size: 0.7em;
  font-weight: 600;
}

.form label .input:focus + span,
.form label .input:valid + span {
  top: 30px;
  font-size: 0.7em;
  font-weight: 600;
}

.form label .input:valid + span {
  color: green;
}

.form label .input:placeholder-shown + span {
  top: 15px;
  font-size: 0.9em;
}

.submit {
  border: none;
  outline: none;
  background-color: royalblue;
  width: 300px;
  padding: 10px;
  margin-top: 10px;
  border-radius: 10px;
  color: #fff;
  font-size: 16px;
  transform: 0.3s ease;
  cursor: pointer;
}

.submit:hover {
  background-color: rgb(56, 90, 194);
}

@keyframes pulse {
  from {
    transform: scale(0.9);
    opacity: 1;
  }

  to {
    transform: scale(1.8);
    opacity: 0;
  }
}

.code {
  width: 195px !important;
}

.Btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-top: 1px;
  width: 90px;
  height: 46.4px;
  border: none;
  padding: 0px 20px;
  background-color: rgba(72, 117, 252, 0.95);
  color: white;
  font-weight: 500;
  cursor: pointer;
  border-radius: 10px;
  transition-duration: 0.3s;
  font-size: 14px;
}

.svg {
  width: 20px;
  position: absolute;
  right: 0;
  margin-right: 12px;
  fill: white;
  transition-duration: 0.3s;
}

.Btn:hover {
  color: transparent;
}

.Btn:hover svg {
  right: 40%;
  margin: 0;
  padding: 0;
  border: none;
  transition-duration: 0.3s;
}

.Btn:active {
  transform: translate(3px, 3px);
  transition-duration: 0.3s;
  box-shadow: 2px 2px 0px rgba(65, 105, 225, 1);
}
</style>
