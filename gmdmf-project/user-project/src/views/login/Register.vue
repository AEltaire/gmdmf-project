<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import $ from "jquery";
import { findAllSchoolService } from "@/api/School";
import { findAllBySchoolIdDepartmentService } from "@/api/Department";
import { findAllMajorService } from "@/api/Major";
import { registerTeacherService } from "@/api/Teacher";

import type {
  ComponentSize,
  FormInstance,
  FormRules,
  FormProps,
} from "element-plus";

const router = useRouter();

const goLogin = () => {
  // 使用路由前往首页
  router.push("/login");
};

const labelPosition = ref<FormProps["labelPosition"]>("right");
const teacherDTO = ref({
  id: null,
  name: "",
  sex: "",
  phone: "",
  email: "",
  address: "",
  school: "",
  department: "",
  major: "",
  idCard: "",
  username: "",
  password: "",
  picture: "",
  introduce: "",
});

interface RuleForm {
  name: string;
  sex: string;
  phone: string;
  email: string;
  address: string;
  school: boolean;
  department: string;
  major: string;
  idCard: string;
  username: string;
  password: string;
  picture: string;
  introduce: string;
}

const ruleFormRef = ref<FormInstance>();

const checkIdCard = function (rule, value, callback) {
  const reg =
    /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[X])$)$/;
  if (!reg.test(value)) {
    callback(new Error("身份证格式不正确"));
  } else {
    callback();
  }
};

const checkPhone = function (rule, value, callback) {
  const reg =
    /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
  if (!reg.test(value)) {
    callback(new Error("手机号格式不正确"));
  } else {
    callback();
  }
};

const checkEmail = function (rule, value, callback) {
  const reg = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
  if (!reg.test(value)) {
    callback(new Error("邮箱格式不正确"));
  } else {
    callback();
  }
};

const rules = reactive<FormRules<RuleForm>>({
  name: [
    { required: true, message: "请输入姓名", trigger: "blur" },
    { min: 2, max: 30, message: "长度在 2 到 30 个字符", trigger: "blur" },
  ],
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 2, max: 30, message: "长度在 2 到 30 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 4, max: 30, message: "长度在 4 到 30 个字符", trigger: "blur" },
  ],
  sex: [
    {
      required: true,
      message: "请选择性别",
      trigger: "change",
    },
  ],
  idCard: [
    { required: true, message: "请输入身份证", trigger: "blur" },
    { min: 18, max: 18, message: "身份证长度为 18 位", trigger: "blur" },
    { validator: checkIdCard, message: "身份证格式不正确", trigger: "blur" },
  ],
  phone: [
    { required: true, message: "请输入电话", trigger: "blur" },
    { min: 11, max: 11, message: "手机号长度为 11 位", trigger: "blur" },
    { validator: checkPhone, message: "手机号格式不正确", trigger: "blur" },
  ],
  school: [{ required: true, message: "请输入学校", trigger: "blur" }],
  department: [{ required: true, message: "请输入学院", trigger: "blur" }],
  major: [{ required: true, message: "请输入专业", trigger: "blur" }],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { validator: checkEmail, message: "邮箱格式不正确", trigger: "blur" },
  ],
  address: [{ required: true, message: "请输入住址", trigger: "change" }],
  introduce: [
    { min: 0, max: 200, message: "长度不能超过 200 字符", trigger: "blur" },
  ],
});

const locationOptions = ["男", "女"];
import { Edit, User, School } from "@element-plus/icons-vue";

const active = ref(0);

const nextToSchool = () => {
  window.scrollTo({
    top: 360,
    behavior: "smooth",
  });
  setTimeout(() => {
    $(".school").slideDown(250, "linear", () => {});
  }, 300);
  active.value = 1;
};

const nextToOther = () => {
  window.scrollTo({
    top: 790,
    behavior: "smooth",
  });
  setTimeout(() => {
    $(".other").slideDown(250, "linear", () => {});
  }, 300);
  active.value = 2;
};

import { ElMessage } from "element-plus";
const schoolList = ref<any[]>([]);
const departmentList = ref<any[]>([]);
const majorList = ref<any[]>([]);

const setUp = (formEl) => {
  active.value = 3;

  if (!formEl) return;
  formEl.validate(async (valid) => {
    if (valid) {
      const result = await registerTeacherService(teacherDTO.value);
      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
      setTimeout(() => {
        ElMessage({
          message: "注册成功！马上跳转至登录界面！",
          type: "success",
        });
      }, 800);
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } else {
      ElMessage.error("请查看输入信息是否符合规范！");
    }
  });
};

const handleSelect = async (item) => {
  const departmentResult = await findAllBySchoolIdDepartmentService(item.id);
  departmentList.value = departmentResult.data.data.map((item) => {
    return { value: item.name, id: item.id };
  });
};

// 忽略大小写
const createFilter = (queryString: string) => {
  return (restaurant) => {
    return (
      restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0
    );
  };
};

const querySearch = (queryString, cb) => {
  const results = queryString
    ? schoolList.value.filter(createFilter(queryString))
    : schoolList.value;
  cb(results);
};

const querySearch2 = (queryString, cb) => {
  const results = queryString
    ? departmentList.value.filter(createFilter(queryString))
    : departmentList.value;
  cb(results);
};

const querySearch3 = (queryString, cb) => {
  const results = queryString
    ? majorList.value.filter(createFilter(queryString))
    : majorList.value;
  cb(results);
};

const loadAll = async () => {
  const schoolResult = await findAllSchoolService();
  schoolList.value = schoolResult.data.data.map((item) => {
    return { value: item.name, id: item.id };
  });
  const majorResult = await findAllMajorService();
  majorList.value = majorResult.data.data.map((item) => {
    return { value: item.name, id: item.id };
  });
};

onMounted(async () => {
  await loadAll();
});
</script>

<template>
  <el-row :gutter="24" style="width: 100%">
    <el-col :span="4"></el-col>
    <el-col :span="16">
      <el-row>
        <el-col :span="6">
          <div class="common-layout" style="height: 100%">
            <el-container>
              <!-- 头像 -->
              <el-header style="height: 100%">
                <el-avatar
                  class="avatar"
                  src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                />
              </el-header>
              <!-- 按钮 -->
              <el-main style="min-height: 360px">
                <div class="select">
                  <button class="container" @click="goLogin">
                    <svg
                      t="1716697725812"
                      class="icon"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="4343"
                      width="20"
                      height="20"
                    >
                      <path
                        d="M511.338 421.875l-221.281 238.02 64.44 60 110.79-119.221v336.521h92.16v-336.47l110.795 119.17 64.432-60z"
                        fill="#2c2c2c"
                        p-id="4344"
                      ></path>
                      <path
                        d="M882.262 86.803H141.739C63.463 86.803 0.007 150.259 0.007 228.535v566.929c0 78.276 63.456 141.732 141.732 141.732h157.663v-80H141.739c-34.039 0-61.732-27.693-61.732-61.732V228.536c0-34.04 27.693-61.732 61.732-61.732h740.522c34.04 0 61.732 27.693 61.732 61.732v566.929c0 34.039-27.692 61.732-61.732 61.732H724.6v80h157.662c78.276 0 141.732-63.456 141.732-141.732V228.536c0-78.277-63.456-141.733-141.732-141.733z"
                        fill="#2c2c2c"
                        p-id="4345"
                      ></path>
                    </svg>
                  </button>
                  <button class="container" id="active">
                    <svg
                      t="1716697768491"
                      class="icon"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="5534"
                      width="20"
                      height="20"
                    >
                      <path
                        d="M752.779277 614.774544c22.772498 0 41.198849-18.466262 41.198849-41.195779 0-13.119284-6.122673-24.787464-15.663319-32.329517l0.212856-0.25993c-31.702207-24.541861-66.94621-44.657755-105.004413-59.222002 48.439015-41.559066 79.256028-103.114295 79.256028-171.951647 0-125.12133-101.463639-226.585993-226.585993-226.585993-125.123377 0-226.587016 101.464663-226.587016 226.585993 0 68.837352 30.818036 130.392581 79.257051 171.951647-154.571154 59.301823-264.565398 208.763415-264.646242 384.297793l0 0.079821c0.080844 22.689607 18.507195 41.118004 41.198849 41.118004 22.770451 0 41.116981-18.428398 41.196802-41.118004 0-181.727662 147.894851-329.58158 329.58158-329.58158 74.497474 0 143.081036 25.128238 198.294595 66.945187C731.868244 610.484682 741.817205 614.774544 752.779277 614.774544zM526.193284 454.008059c-79.498561 0-144.190342-64.692805-144.190342-144.192389 0-79.498561 64.693828-144.191366 144.190342-144.191366 79.498561 0 144.191366 64.692805 144.191366 144.191366S605.691845 454.008059 526.193284 454.008059zM909.926191 752.851423 848.129453 752.851423l0-61.798785c0.001023-17.057115-13.839719-30.897857-30.897857-30.897857-17.099073 0-30.897857 13.840742-30.897857 30.897857l0 61.798785-61.796738 0c-17.097026 0-30.896834 13.838695-30.896834 30.897857 0 17.057115 13.799808 30.897857 30.896834 30.897857l61.796738 0 0 61.796738c0 17.059162 13.798785 30.895811 30.897857 30.895811 17.058139 0 30.897857-13.836649 30.897857-30.895811l0-61.796738 61.796738 0c17.058139 0 30.897857-13.840742 30.897857-30.897857C940.824049 766.691142 926.985353 752.851423 909.926191 752.851423z"
                        fill="#2c2c2c"
                        p-id="5535"
                      ></path>
                    </svg>
                  </button>
                </div>
              </el-main>
            </el-container>
          </div>
        </el-col>
        <el-col :span="17" style="background-color: rgb(255, 255, 255, 1)">
          <el-steps simple style="width: 100%" :active="active" align-center>
            <el-step title="基本信息" :icon="User" />
            <el-step title="学校信息" :icon="School" />
            <el-step title="其他信息" :icon="Edit" />
          </el-steps>
          <!-- 基础信息 -->
          <div>
            <el-card style="width: 80%; margin: 10%" shadow="never">
              <el-form
                :label-position="labelPosition"
                label-width="auto"
                :model="teacherDTO"
                :inline="true"
                :rules="rules"
                ref="ruleFormRef"
              >
                <el-form-item prop="name" label="姓名" style="width: 56%">
                  <el-input v-model="teacherDTO.name" />
                </el-form-item>
                <el-form-item prop="sex" label="性别" style="width: 30%">
                  <el-segmented
                    v-model="teacherDTO.sex"
                    :options="locationOptions"
                  />
                </el-form-item>
                <el-form-item
                  prop="username"
                  label="用户名"
                  style="width: 100%"
                >
                  <el-input v-model="teacherDTO.username" />
                </el-form-item>
                <el-form-item prop="password" label="密码" style="width: 100%">
                  <el-input type="password" v-model="teacherDTO.password" />
                </el-form-item>
                <el-form-item prop="phone" label="电话" style="width: 100%">
                  <el-input v-model="teacherDTO.phone" />
                </el-form-item>
                <el-form-item prop="idCard" label="身份证" style="width: 100%">
                  <el-input v-model="teacherDTO.idCard" />
                </el-form-item>
              </el-form>
            </el-card>
          </div>
          <el-divider>
            <el-button type="primary" circle @click="nextToSchool">
              <el-icon><ArrowDownBold /></el-icon>
            </el-button>
          </el-divider>
          <!-- 学校信息 -->
          <el-card
            class="school"
            style="width: 80%; margin: 10%"
            shadow="never"
          >
            <el-form
              :label-position="labelPosition"
              label-width="auto"
              :model="teacherDTO"
              :rules="rules"
            >
              <el-form-item prop="school" label="学校">
                <el-autocomplete
                  v-model="teacherDTO.school"
                  :fetch-suggestions="querySearch"
                  clearable
                  class="inline-input w-50"
                  placeholder="请选择学校"
                  @select="handleSelect"
                />
              </el-form-item>
              <el-form-item prop="department" label="院系">
                <el-autocomplete
                  v-model="teacherDTO.department"
                  :fetch-suggestions="querySearch2"
                  clearable
                  class="inline-input w-50"
                  placeholder="请选择院系"
                />
              </el-form-item>
              <el-form-item prop="major" label="专业">
                <el-autocomplete
                  v-model="teacherDTO.major"
                  :fetch-suggestions="querySearch3"
                  clearable
                  class="inline-input w-50"
                  placeholder="请选择专业"
                />
              </el-form-item>
            </el-form>
          </el-card>
          <el-divider class="school">
            <el-button type="primary" circle @click="nextToOther">
              <el-icon><ArrowDownBold /></el-icon>
            </el-button>
          </el-divider>
          <!-- 拓展信息 -->
          <el-card class="other" style="width: 80%; margin: 10%" shadow="never">
            <el-form
              :label-position="labelPosition"
              label-width="auto"
              :model="teacherDTO"
              :rules="rules"
            >
              <el-form-item prop="email" label="邮箱">
                <el-input v-model="teacherDTO.email" />
              </el-form-item>
              <el-form-item prop="address" label="住址">
                <el-input v-model="teacherDTO.address" />
              </el-form-item>
              <el-form-item prop="introduce" label="介绍">
                <el-input
                  v-model="teacherDTO.introduce"
                  maxlength="200"
                  resize="none"
                  placeholder="用简短的话语介绍一下自己..."
                  class="r_n"
                  show-word-limit
                  type="textarea"
                  rows="4"
                />
              </el-form-item>
            </el-form>
          </el-card>
          <el-divider class="other" style="margin-bottom: 50px">
            <el-button
              :plain="true"
              type="success"
              circle
              @click="setUp(ruleFormRef)"
            >
              <el-icon><Select /></el-icon>
            </el-button>
          </el-divider>
        </el-col>
      </el-row>
    </el-col>
    <el-col :span="4"></el-col>
  </el-row>
</template>

<style lang="scss" scoped>
* {
  font-family: "宋体";
}

.school {
  display: none;
}

.other {
  display: none;
}

.common-layout {
  min-height: 100vh;
  background-color: rgb(227, 241, 255, 0.6);
}

.avatar {
  margin: 16%;
  margin-top: 24%;
  width: 64%;
  height: auto;
  border-radius: 20px;
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
