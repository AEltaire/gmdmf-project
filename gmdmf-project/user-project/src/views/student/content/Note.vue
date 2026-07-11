<script lang="ts" setup>
import { ref } from "vue";
import { onBeforeMount, onMounted, onBeforeUnmount, shallowRef } from "vue";
import $ from "jquery";
import {
  deleteByIdNoteService,
  updateByIdNoteService,
  findByStudentIdNoteService,
  findByIdNoteService,
} from "@/api/Note";
import { findByIdChapterService } from "@/api/Chapter";

import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { ElMessage, ElMessageBox } from "element-plus";

interface note {
  id: number;
  studentId: number;
  courseId: number;
  courseName: string;
  chapterTitle: string;
  nodeTitle: string;
  title: string;
  content: string;
  createTime: Date;
}

// 弹框显示
const dialogVisible = ref(false);
const total = ref(0);
const chapterDisabledValue = ref(false);
const chapterInfo = ref("章节");
const nodeDisabledValue = ref(false);
const nodeInfo = ref("节点");
const currentPage = ref(0);
const noteList = ref<note[]>([]);
const noteDTO = ref<note>();
const noteVO = ref<note>();

// 编辑器章节数据
const chapterList = ref([
  {
    id: "",
    title: "",
    nodeVOS: [
      {
        id: "",
        chapterId: "",
        title: "",
        content: "",
        introduce: "",
        createTime: "",
      },
    ],
  },
]);

const nodeList = ref([
  {
    id: "",
    chapterId: "",
    title: "",
    content: "",
    introduce: "",
    createTime: "",
  },
]);

// 动画效果 --------------------------------

const clickNext = () => {
  if (currentPage.value >= total.value) return;
  currentPage.value += 1;
  noteVO.value = noteList.value[currentPage.value - 1];
};

const clickPrev = () => {
  if (currentPage.value <= 1) return;
  currentPage.value -= 1;
  noteVO.value = noteList.value[currentPage.value - 1];
};

const changeChapter = (index) => {
  nodeList.value = chapterList.value[index].nodeVOS;
  noteDTO.value.chapterTitle = chapterList.value[index].title;
  noteDTO.value.nodeTitle = nodeList.value[0].title;
  noteDTO.value.chapterTitle = chapterList.value[index].title;
  noteDTO.value.nodeTitle = nodeList.value[0].title;
};

const changeNode = (index) => {
  noteDTO.value.nodeTitle = nodeList.value[index].title;
  noteDTO.value.nodeTitle = nodeList.value[index].title;
};

// 查询所有
const getNoteList = async () => {
  const result = await findByStudentIdNoteService();
  noteList.value = result.data.data;
  total.value = noteList.value.length;
  if (noteList.value.length > 0) {
    currentPage.value = 1;
    noteVO.value = noteList.value[currentPage.value - 1];
  } else {
    currentPage.value = 0;
  }
};

// 回显数据
const handleEdit = async () => {
  const result = await findByIdNoteService(noteVO.value.id);
  noteDTO.value = result.data.data;
  valueHtml.value = noteDTO.value.content;
  dialogVisible.value = true;
  await getChapterList(noteDTO.value.courseId);
  let nodeTitle = noteDTO.value.nodeTitle;
  for (let i = 0; i < chapterList.value.length; i++) {
    if (chapterList.value[i].title == noteDTO.value.chapterTitle) {
      changeChapter(i);
      noteDTO.value.nodeTitle = nodeTitle;
      return;
    }
  }
};

// 修改数据
const updateNote = async () => {
  noteDTO.value.content = valueHtml.value;
  const result = await updateByIdNoteService(noteDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
  clear();
  let index = currentPage.value - 1;
  await getNoteList();
  noteVO.value = noteList.value[index];
  currentPage.value = index + 1;
  dialogVisible.value = false;
};

// 点击删除
const handleDelete = () => {
  ElMessageBox.confirm("你确定要删除这个笔记吗?", "温馨提示", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const result = await deleteByIdNoteService(noteVO.value.id);
      ElMessage.success(result.data.message ? result.data.message : "删除成功");
      await getNoteList();
    })
    .catch(() => {
      null;
    });
};

// 清空弹框数据
const clear = () => {
  noteDTO.value = {
    id: null,
    studentId: null,
    courseId: null,
    courseName: "",
    chapterTitle: "",
    nodeTitle: "",
    title: "",
    content: "",
    createTime: null,
  };
  valueHtml.value = "<p></p>";
};

// 获取所有章节
const getChapterList = async (id) => {
  const result = await findByIdChapterService(id);

  chapterList.value = result.data.data;
  nodeList.value = chapterList.value[0].nodeVOS;
  if (result.data.data.length == 0) {
    chapterDisabledValue.value = true;
    chapterInfo.value = "暂无章节";
    nodeList.value = [];
    nodeDisabledValue.value = true;
    nodeInfo.value = "暂无节点";
  }
};

// 富文本编辑器
const editorRef = shallowRef();
const valueHtml = ref("<p></p>");
const mode = ref("default");
const toolbarConfig = ref({});
const editorConfig = ref({ placeholder: "请输入内容..." });
const handleCreated = (editor) => {
  editorRef.value = editor;
};

onMounted(async () => {
  await getNoteList();
});

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});
</script>

<template>
  <el-dialog v-model="dialogVisible" :show-close="false" width="60%">
    <div class="my-header" style="margin-bottom: 10px">
      <el-select
        class="note-input"
        v-model="noteDTO.chapterTitle"
        filterable
        :disabled="chapterDisabledValue"
        :placeholder="chapterInfo"
        style="width: 240px; margin-right: 10px"
        @change="changeNode"
      >
        <el-option
          v-for="(node, index) in chapterList"
          :key="index"
          :label="node.title"
          :value="index"
        />
      </el-select>
      <el-select
        class="note-input"
        v-model="noteDTO.nodeTitle"
        filterable
        :disabled="nodeDisabledValue"
        :placeholder="nodeInfo"
        style="width: 240px; margin-right: 10px"
        @change="changeNode"
      >
        <el-option
          v-for="(node, index) in nodeList"
          :key="node.id"
          :label="node.title"
          :value="index"
        />
      </el-select>

      <div class="dialog-button">
        <el-button style="width: 80px" type="primary" @click="updateNote" round
          ><el-icon><Edit /></el-icon
        ></el-button>
        <el-button
          @click="
            dialogVisible = false;
            clear();
          "
          circle
          ><el-icon><Close /></el-icon
        ></el-button>
      </div>
    </div>
    <div>
      <el-input
        class="note-input"
        v-model="noteDTO.title"
        style="width: 100%; margin-bottom: 10px"
        maxlength="30"
        placeholder="标题"
        show-word-limit
        type="text"
      />
    </div>
    <div class="editor-box" contentEditable="{false}">
      <Toolbar
        id="toolbar"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        :mode="mode"
      />
      <Editor
        id="editor"
        style="
          height: 320px;
          overflow-y: none;
          overflow-x: none;
          -ms-overflow-style: none;
          scrollbar-width: none;
        "
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        :mode="mode"
        @onCreated="handleCreated"
      />
    </div>
  </el-dialog>

  <div class="note">
    <button class="prev" @click="clickPrev">
      <el-icon size="20" style="color: royalblue"><ArrowLeftBold /></el-icon>
    </button>
    <button class="next" @click="clickNext">
      <el-icon size="20" style="color: royalblue"><ArrowRightBold /></el-icon>
    </button>
    <el-empty
      v-if="total == 0"
      :image-size="240"
      style="width: 100%; align-items: center; height: 100%"
    />
    <div v-else class="note-all" style="z-index: 2; top: 0%; left: 9%">
      <el-card
        shadow="never"
        class="note-content"
        style="height: 72vh; border-radius: 10px; border: none"
      >
        <div class="note-header">
          <button class="edit" @click="handleEdit">
            <div class="svg-wrapper-1">
              <div class="svg-wrapper">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  width="20"
                  height="20"
                >
                  <path fill="none" d="M0 0h24v24H0z"></path>
                  <path
                    fill="currentColor"
                    d="M1.946 9.315c-.522-.174-.527-.455.01-.634l19.087-6.362c.529-.176.832.12.684.638l-5.454 19.086c-.15.529-.455.547-.679.045L12 14l6-8-8 6-8.054-2.685z"
                  ></path>
                </svg>
              </div>
            </div>
            <span id="title"> {{ noteVO.title }} </span>
          </button>
          <button class="delete" @click="handleDelete">
            <svg viewBox="0 0 448 512" class="svgIcon">
              <path
                d="M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z"
              ></path>
            </svg>
          </button>
          <p class="note-intro">
            <span id="intro" style="line-height: 1.5rem; margin-right: 1.5rem">
              {{ noteVO.courseName }} &nbsp;&nbsp;&gt;&gt;&nbsp;&nbsp;
              {{ noteVO.chapterTitle }} &nbsp;&nbsp;&gt;&gt;&nbsp;&nbsp;
              {{ noteVO.nodeTitle }}
            </span>
          </p>
        </div>
        <div id="content" class="note-bottom" v-html="noteVO.content"></div>
      </el-card>
    </div>
    <div
      class="note-two note-all"
      style="z-index: 1; top: 3%; left: 11%; opacity: 0.6"
    ></div>
    <div class="page">
      <div class="page-tool">{{ currentPage }} / {{ total }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.editor-box {
  margin: 10px 0;
  width: 100%;
  height: 100%;
  padding: 0 4px;
  border: 2px solid #6b7dd7;
  border-radius: 6px;
  box-sizing: border-box;
  z-index: 99;

  #toolbar {
    width: 100%;
    border-radius: 6px 6px 0 0;
    border-bottom: 2px dashed #6b7dd7;
  }

  #editor {
    width: 100%;
    border-top: none;
    border-radius: 0 0 4px 4px;
  }
}

.note-input {
  --el-color-primary: royalblue;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 2px solid royalblue;
  border-right: 2px solid royalblue;
  --el-input-focus-border-color: royalblue;
  --el-input-placeholder-color: #a8abb2;
  --el-input-text-color: #606266;
  --el-input-icon-color: white;
  border-radius: 6px;
  box-sizing: border-box;
  margin-bottom: 5px;
}

.dialog-button {
  position: absolute;
  top: 20px;
  right: 20px;
}

// 分页
.page {
  position: absolute;
  width: 100%;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;

  .page-tool {
    border-radius: 6px;
    width: 80px;
    height: 24px;
    text-align: center;
    line-height: 24px;
    font-weight: 700;
    font-size: 16px;
    color: royalblue;
  }
}

.note {
  position: relative;
  width: 100%;
  min-width: 500px;
  height: 82vh;
  // background-color: red;

  .note-all {
    position: absolute;
    width: 80%;
    height: 75vh;
    background-color: #fff;
    border: 2px solid royalblue;
    border-radius: 10px;
    top: -2%;
  }

  .prev {
    position: absolute;
    top: 48%;
    width: 36px;
    height: 36px;
    // 图标居中
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    background-color: #f9eac9;
    border: 2px solid royalblue;
    cursor: pointer;
  }

  .prev:hover {
    //变小
    transform: scale(0.9);
    transition-duration: 0.25s;
  }

  .next {
    position: absolute;
    top: 48%;
    right: 0;
    width: 36px;
    height: 36px;
    // 图标居中
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    background-color: #f9eac9;
    border: 2px solid royalblue;
    cursor: pointer;
  }

  .next:hover {
    //变小
    transform: scale(0.9);
    transition-duration: 0.25s;
  }
}

.note-content {
  width: 100%;
  height: 100%;
}

.note-header {
  width: 100%;
  height: 100px;
}

.note-bottom {
  padding: 10px 5px;
  width: 100%;
  border-top: 2px dashed royalblue;
  // border-bottom: 2px dashed royalblue;
  font-size: 14px;
  line-height: 1.5rem;
  overflow-y: none;
  scrollbar-width: none;
  -webkit-box-sizing: border-box;
}

// 删除
.delete {
  float: right;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: rgb(20, 20, 20);
  border: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.164);
  cursor: pointer;
  transition-duration: 0.3s;
  overflow: hidden;
  position: relative;
}

.svgIcon {
  width: 12px;
  transition-duration: 0.3s;
}

.svgIcon path {
  fill: white;
}

.delete:hover {
  width: 100px;
  border-radius: 50px;
  transition-duration: 0.3s;
  background-color: rgb(255, 69, 69);
  align-items: center;
}

.delete:hover .svgIcon {
  width: 30px;
  transition-duration: 0.3s;
  transform: translateY(60%);
}

.delete::before {
  position: absolute;
  top: -25px;
  content: "Delete";
  color: white;
  transition-duration: 0.3s;
  font-size: 2px;
}

.delete:hover::before {
  font-size: 10px;
  opacity: 1;
  transform: translateY(30px);
  transition-duration: 0.3s;
}

.delete:active {
  letter-spacing: 3px;
  background-color: rgb(255, 69, 69);
  color: hsl(0, 0%, 100%);
  box-shadow: rgb(255, 69, 69) 0px 0px 0px 0px;
  transform: translateY(2px);
  transition: 100ms;
}

// 修改
.edit {
  float: left;
  font-family: inherit;
  font-size: 14px;
  background: linear-gradient(to bottom, #66a6ff 0%, #66a6ff 100%);
  color: white;
  padding: 6px 14px 6px 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 25px;
  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.16);
  transition: all 0.3s;
}

.edit:hover {
  cursor: pointer;
  transform: translateY(-3px);
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.2);
}

.edit:active {
  transform: scale(0.95);
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);
}

.edit span {
  display: block;
  margin-left: 0.2em;
  transition: all 0.3s;
}

.edit svg {
  width: 10px;
  height: 10px;
  fill: white;
  transition: all 0.3s;
}

.edit .svg-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  margin-right: 0.5em;
  transition: all 0.3s;
}

.edit:hover .svg-wrapper {
  background-color: rgba(255, 255, 255, 0.5);
}

.edit:hover svg {
  transform: rotate(45deg);
}

.note-intro {
  width: 100%;
  float: left;
  margin: 16px 0px;
  padding: 2px 5px;
  -webkit-box-sizing: border-box;
  font-size: 13px;
  color: rgb(106, 105, 105);
}
</style>
