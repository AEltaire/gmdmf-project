<script setup lang="ts">
import { ref, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useTokenStore } from "@/stores/token";
import { useUserInfoStore } from "@/stores/userInfo";
import { loginManageService, getManageInfoService } from "@/api/Manage";
import { ElMessage } from "element-plus";
import { useForm } from "vuestic-ui";
const router = useRouter();
// 禁止页面滚动
document.body.style.overflow = "hidden";
const ruleList = ref(["管理", "用户"]);
const params = ref({
  username: "",
  password: "",
  rule: "管理",
});

// 登录
const loginManage = async () => {
  if (params.value.rule == "管理") {
    const result = await loginManageService(
      params.value.username,
      params.value.password
    );
    useTokenStore().setToken(result.data.data);
    const res = await getManageInfoService();
    useUserInfoStore().setUserInfo(res.data.data);
    ElMessage.success(result.data.message ? result.data.message : "登录成功");
    router.push("/");
  }
};

onUnmounted(() => {
  document.body.style.overflow = "auto";
  cleanParams();
});

const cleanParams = () => {
  params.value = {
    username: "",
    password: "",
    rule: "管理",
  };
};

const changeRule = () => {
  if (params.value.rule == "用户") {
    location.href = "http://localhost:5173/login";
  }
};

const { isValid } = useForm("myForm");

</script>

<template>
  <VaCard
    style="width: 450px; height: 600px; margin: auto; margin-top: 60px"
    class="card"
    stripe
    stripe-color="primary"
  >
    <VaCardContent>
      <div class="avatar">
        <el-avatar
          :size="100"
          style="border: 3px dashed royalblue; --el-avatar-bg-color: none"
          src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        />
      </div>
      <VaForm ref="myForm" class="from">
        <VaInput
          stateful
          style="width: 380px; margin-bottom: 5px"
          v-model="params.username"
          label="用户名"
          placeholder="Please enter your username"
          :rules="[
            (v) => v.length > 3 || '不能少于 4 位',
            (v) => !!v || '不能为空',
          ]"
        />

        <VaInput
          stateful
          v-model="params.password"
          class="mt-3"
          type="password"
          label="密码"
          style="width: 380px; margin-bottom: 5px"
          placeholder="Please enter your password"
          :rules="[
            (v) => v.length > 3 || '不能少于 4 位',
            ,
            (v) => !!v || '不能为空',
          ]"
        />

        <VaSelect
          v-model="params.rule"
          class="mt-3"
          label="身份"
          :options="ruleList"
          style="width: 380px; margin-bottom: 125px"
          placeholder="Select enter your role"
          @update:modelValue="changeRule"
        />

        <VaButton
          class="btn"
          style="width: 380px"
          :disabled="!isValid"
          @click="loginManage"
        >
          登录
        </VaButton>
      </VaForm>
    </VaCardContent>
  </VaCard>
</template>
<style lang="css" scoped>
.avatar {
  padding-top: 20px;
  margin-bottom: 10px;
  width: 100%;
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  --el-avatar-bg-color: none;
}

.from {
  position: relative;
  padding: 0 16px;
  height: 400px;
}

.from .btn {
  position: absolute;
  bottom: 40px;
  left: 15px;
}
</style>
