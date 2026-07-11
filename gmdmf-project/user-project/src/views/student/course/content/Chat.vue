<script lang="ts" setup>
import { ElMessage } from "element-plus";
import { nextTick, onMounted, ref } from "vue";
import { useCourseStore } from "@/stores/course";
import { getMyScoreService } from "@/api/Data";
import { getChatService } from "@/api/Chat";
import { marked } from "marked";
const courseId = useCourseStore().course.id;
const chatInput = ref("");
const userChatList = ref<chat[]>([]);

interface chat {
  user: string;
  content: string;
}

const chatOutput: any = ref("");

const getMyScore = async () => {
  const result = await getMyScoreService(null, courseId);
  console.log(result.data.data);

  if (result.data.data.length === 0) {
    chatOutput.value = "您还没有批改完的试卷哦~";
    return;
  }

  let str = result.data.data.join("\n");
  str += "\n这是我所有做过的试卷的成绩汇总, 请给我提供一些建议!";
  const res = await getChatService(str);
  chatOutput.value = res.data.data;
};

const inputState = ref(false);

const sendChat = async () => {
  if (!chatInput.value.trim()) {
    ElMessage.warning("请输入向 AI 提问的内容");
    return;
  }

  if (chatInput.value.length > 200) {
    ElMessage.warning("输入内容过长，请控制在200字以内");
    return;
  }

  inputState.value = true;
  let tempInput = chatInput.value;
  if (chatInput) chatInput.value = "";

  userChatList.value.push({
    user: "me",
    content: tempInput,
  });

  let reg = /我的成绩/;
  if (reg.exec(tempInput)) {
    await getMyScore();
  } else {
    const result = await getChatService(tempInput);
    chatOutput.value = marked.parse(result.data.data);
  }

  userChatList.value.push({
    user: "deepseek",
    content: chatOutput.value,
  });

  inputState.value = false;
  nextTick(() => {
    scrollToBottom();
  });
};

function scrollToBottom() {
  const list = document.querySelector(".scrollbar");
  list.scrollTop = list.scrollHeight;
}
</script>

<template>
  <!-- 内容 -->

  <VaScrollContainer class="max-h-52 scrollbar" vertical>
    <VaList id="list" style="height: 75vh; padding: 5px 10px">
      <VaListLabel v-show="true"> 聊天记录 </VaListLabel>
      <VaListItem
        v-for="(chat, index) in userChatList"
        :key="index"
        style="margin-top: 12px"
      >
        <VaListItemSection v-if="chat.user == 'me'">
          <VaListItemLabel>
            <span class="message my">{{ chat.content }}</span>
          </VaListItemLabel>
        </VaListItemSection>
        <VaListItemSection v-else>
          <VaListItemLabel>
            <span class="message" v-html="chat.content"></span>
          </VaListItemLabel>
        </VaListItemSection>
      </VaListItem>
      <VaListLabel v-show="true"> </VaListLabel>
    </VaList>
  </VaScrollContainer>
  <div class="box">
    <el-row>
      <el-col :span="22" style=""
        ><VaInput
          class="input"
          v-model="chatInput"
          :readonly="inputState"
          max-length="200"
          immediate-validation
          :error="chatInput.length > 200"
          placeholder="请输入向 AI 提问的内容 「200字以内」"
      /></el-col>
      <el-col
        :span="2"
        style="
          padding-left: 6px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          display: flex;
          justify-content: center;
          align-items: center;
        "
      >
        <VaButton
          :disabled="inputState"
          :loading="inputState"
          class="btn"
          style="margin: 0"
          @click="sendChat"
        >
          发送
        </VaButton>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
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

.progress-item {
  padding-top: 6px;
}
#load {
  transition: all 0.5s ease-in-out;
}

.textarea {
  margin-bottom: 2vh;
}

.box {
  position: relative;
  width: 100%;
  height: 5vh;
}

.input {
  width: 100%;
}

.btn {
  width: 100%;
}

.scrollbar {
  // padding 不影响盒子大小
  box-sizing: border-box;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 0.25rem;
  margin-bottom: 2vh;
}
</style>
