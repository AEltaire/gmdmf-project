<script lang="ts" setup>
import $ from "jquery";
import {
  nextTick,
  onMounted,
  ref,
  watch,
  onBeforeUnmount,
  shallowRef,
} from "vue";
import { CloseBold } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { findByIdChapterService } from "@/api/Chapter";
import { saveNoteService } from "@/api/Note";
import { useCourseStore } from "@/stores/course";
import { ElMessage, ElMessageBox, genFileId } from "element-plus";
import { _throttle } from "@/utils/throttle";
import { ElNotification } from "element-plus";
import type { CheckboxValueType, UploadInstance } from "element-plus";
import { findByIdCourseService } from "@/api/Course";
import { uploadImgService } from "@/api/Upload";
import { findAllMajorService } from "@/api/Major";
import { findByTeacherIdTeamService, findByCourseIdService } from "@/api/Team";
import { useNodeStore } from "@/stores/node";

const isNewCourse = ref(false);
const courseStore = useCourseStore();
const checkAll = ref(false);
const indeterminate = ref(false);
const checkedTeamList = ref<CheckboxValueType[]>([]);
const URL = ref(null);
const router = useRouter();
const nodeStore = useNodeStore();
const activeName = ref(null);
const oldActiveName = ref(0);
const oldIndex = ref(null);
const isCourseInfo = ref(false);
const time = ref<[Date, Date]>([new Date(), new Date()]);

interface Course {
  id: number;
  name: string;
  majorName: string;
  startTime: string;
  endTime: string;
  point: number;
  introduce: string;
  picture: string;
  pub: string;
  teamIds: CheckboxValueType[];
}

// 课程信息表单
const courseVO = ref<Course>({
  id: null,
  name: "",
  majorName: "",
  startTime: "",
  endTime: "",
  point: 1,
  introduce: "",
  picture: "",
  pub: "0",
  teamIds: [],
});

interface Chapter {
  id: number;
  title: string;
  nodeVOS: Array<NodeList>;
}

interface NodeList {
  id: number;
  chapterId: number;
  title: string;
  content: string;
  introduce: string;
  size: string;
  color: string;
  hollow: boolean;
  type: string;
}

// 章节列表
const chapterList = ref<Chapter[]>([]);
const nodeList = ref<Node[]>();

// 章节表单
const chapterDTO = ref({
  id: null,
  title: "",
  courseId: null,
});

interface Node {
  id: number;
  chapterId: number;
  title: string;
  introduce: string;
  content: string;
}

// 节点
const nodeVO = ref<Node>({
  id: null,
  chapterId: null,
  title: "",
  introduce: "",
  content: "",
});
const nodeDTO = ref<Node>();

// 专业列表
const majorList = ref([
  {
    id: 0,
    name: "",
  },
]);

// 获取专业列表
const findAllMajor = async () => {
  const result = await findAllMajorService();
  majorList.value = result.data.data;
};

// 班级列表
const teamList = ref([
  {
    id: null,
    name: "",
    departmentId: null,
    majorId: null,
    endTime: "",
  },
]);

// 获取班级列表
const findByTeacherIdTeam = async () => {
  const result = await findByTeacherIdTeamService();
  teamList.value = result.data.data;
};

// 获取课程信息
const findByIdCourse = async (id) => {
  const result = await findByIdCourseService(id);

  courseVO.value = {
    id: result.data.data.id,
    name: result.data.data.name,
    majorName: result.data.data.majorName,
    startTime: result.data.data.startTime,
    endTime: result.data.data.endTime,
    point: result.data.data.point,
    introduce: result.data.data.introduce,
    picture: result.data.data.picture,
    pub: result.data.data.pub,
    teamIds: [],
  };
  time.value = [
    new Date(result.data.data.startTime),
    new Date(result.data.data.endTime),
  ];
};

const findByCourseIdTeam = async (id) => {
  const result = await findByCourseIdService(id);
  checkedTeamList.value = result.data.data.map((item) => item.name);
  courseVO.value.teamIds = checkedTeamList.value;
};

onBeforeUnmount(() => {
  // nodeStore.removeNode();
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

onMounted(async () => {
  if (
    useCourseStore().course.id != null ||
    useCourseStore().course.id != undefined
  ) {
    await getChapterList(courseStore.course.id);
    await findByIdCourse(useCourseStore().course.id);
    await findByCourseIdTeam(useCourseStore().course.id);
    isNewCourse.value = false;
    isCourseInfo.value = true;
    if (nodeStore.node.node != null) {
      activeName.value = nodeStore.node.index + 1;
      handleClick(nodeStore.node.node, nodeStore.node.index, nodeStore.node.i);
    }
  } else {
    isCourseInfo.value = false;
  }
  await findAllMajor();
  if (isCourseInfo.value == false) {
    showCoursePaper();
  }
  await findByTeacherIdTeam();
  await getNoteChapterList(courseStore.course.id);
});

// 按钮控制 -----------------------------------------------

// 返回首页
const backHome = () => {
  router.push("/stu-course/chapter");
};

const courseIntro = () => {
  isCourseInfo.value = !isCourseInfo.value;
  nextTick(() => {
    showCoursePaper();
  });
};

const showCoursePaper = () => {
  $(".table").animate(
    {
      marginTop: "0px",
      opacity: 1,
    },
    1000
  );

  $(".table-3")
    .css({
      bottom: "-80px",
      rotate: "6deg",
      left: "25%",
      opacity: 0.5,
    })
    .animate(
      {
        opacity: 0.84,
        rotate: "12deg",
        left: "30%",
      },
      700,
      "linear"
    );

  $(".table-2")
    .css({
      bottom: "-120px",
      rotate: "-6deg",
      opacity: 0.5,
      left: "15%",
    })
    .animate(
      {
        rotate: "-12deg",
        opacity: 0.96,
        left: "12%",
      },
      700,
      "linear"
    );

  setTimeout(function () {
    $(".table-1")
      .css({
        bottom: "0px",
        opacity: 0.2,
      })
      .animate(
        {
          opacity: 1,
        },
        700,
        "linear"
      );
  }, 400);
};

const hideCoursePaper = () => {
  $(".table").animate(
    {
      marginTop: "100px",
      opacity: 0,
    },
    1000
  );
  $(".table-2").animate(
    {
      rotate: "-5deg",
      left: "15%",
    },
    700
  );
  $(".table-3").animate(
    {
      rotate: "5deg",
      left: "25%",
    },
    700
  );

  setTimeout(function () {
      isCourseInfo.value = true;
      activeName.value = 1;
      oldActiveName.value = activeName.value
      oldIndex.value = 0;
      handleClick(chapterList.value[0].nodeVOS[0], 0, 0);
  }, 1080);
};

// 阅览查看 ----------------------------------------

// 获取章节列表
const getChapterList = async (id) => {
  const result = await findByIdChapterService(id);
  chapterList.value = result.data.data;

  if (chapterList.value != undefined && chapterList.value.length > 0) {
    chapterList.value.forEach((chapter) => {
      chapter.nodeVOS.forEach((node) => {
        node.color = "#c6e2ff";
        node.hollow = false;
        node.type = "";
      });
    });
  }
};

// 查看节点
const handleClick = async (node, index, i) => {
  URL.value = node.content;
  nodeVO.value = node;
  oldIndex.value = i; // 保存节点下标

  // 改变节点颜色
  await changeColor(index, i);
  chapterDTO.value.id = chapterList.value[activeName.value - 1].id;
};

// 改变节点颜色
const changeColor = (index, i) => {
  chapterList.value.forEach((chapter) => {
    chapter.nodeVOS.forEach((node) => {
      node.color != "#c6e2ff" ? (node.color = "#c6e2ff") : node.color;
      node.hollow ? (node.hollow = false) : node.hollow;
      node.type == "primary" ? (node.type = "") : node.type;
    });
  });
  chapterList.value[index].nodeVOS[i].color = "";
  chapterList.value[index].nodeVOS[i].hollow = true;
  chapterList.value[index].nodeVOS[i].type = "primary";
};

// 切换章节和节点
const handleChange = () => {
  cleanDTO();
  if (activeName.value != "") {
    chapterDTO.value.id = chapterList.value[activeName.value - 1].id;
    chapterDTO.value.title = chapterList.value[activeName.value - 1].title;
    oldActiveName.value = activeName.value;
  } else {
    chapterDTO.value.id = chapterList.value[oldActiveName.value - 1].id;
    chapterDTO.value.title = chapterList.value[oldActiveName.value - 1].title;
  }
};

const cleanDTO = () => {
  nodeVO.value = {
    id: null,
    chapterId: null,
    title: "",
    content: "",
    introduce: "",
  };
  chapterDTO.value = {
    id: null,
    title: "",
    courseId: courseStore.course.id,
  };
  nodeDTO.value = {
    id: null,
    chapterId: null,
    title: "",
    introduce: "",
    content: "",
  };
};

watch(checkedTeamList, (val) => {
  if (val.length === 0) {
    checkAll.value = false;
    indeterminate.value = false;
  } else if (val.length === teamList.value.length) {
    checkAll.value = true;
    indeterminate.value = false;
  } else {
    indeterminate.value = true;
  }
});

const handleCheckAll = (val: CheckboxValueType) => {
  indeterminate.value = false;
  if (val) {
    checkedTeamList.value = teamList.value.map((_) => _.id);
  } else {
    checkedTeamList.value = [];
  }
};

// 笔记 -----------------------------------------
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import Index from "@/views/Index.vue";

const toolbarConfig = ref({});
const editorConfig = ref({ placeholder: "请输入内容..." });
const editorRef = shallowRef();
const mode = ref("default");
const valueHtml = ref("<p></p>");
const noteDTO = ref<Note>();
const dialogVisible = ref(false);
const chapterDisabledValue = ref(false);
const nodeDisabledValue = ref(false);
const chapterInfo = ref("章节");
const nodeInfo = ref("节点");
const handleCreated = (editor) => {
  editorRef.value = editor;
};

interface NoteChapter {
  id: number;
  title: string;
  nodeVOS: Array<any>;
}

interface Note {
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

const noteChapterList = ref<NoteChapter[]>([]);
const noteNodeList = ref<Node[]>();

const changeChapter = (index) => {
  noteNodeList.value = noteChapterList.value[index].nodeVOS;
  noteDTO.value.chapterTitle = noteChapterList.value[index].title;
  noteDTO.value.nodeTitle = noteNodeList.value[0].title;
  noteDTO.value.chapterTitle = noteChapterList.value[index].title;
  noteDTO.value.nodeTitle = noteNodeList.value[0].title;
};

const changeNode = (index) => {
  noteDTO.value.nodeTitle = noteNodeList.value[index].title;
  noteDTO.value.nodeTitle = noteNodeList.value[index].title;
};

const clickAddNote = async () => {
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

  if (
    activeName.value != null &&
    activeName.value != undefined &&
    activeName.value != ""
  ) {
    if (oldActiveName.value > 0) changeChapter(oldActiveName.value - 1);
    else changeChapter(0);

    if (oldIndex.value == null) changeNode(0);
    else changeNode(oldIndex.value);
  } else {
    changeChapter(0);
    changeNode(0);
  }
  dialogVisible.value = true;
};

const saveNote = async () => {
  noteDTO.value.content = valueHtml.value;
  noteDTO.value.courseId = courseStore.course.id;
  const result = await saveNoteService(noteDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "添加成功");
  clear();
  dialogVisible.value = false;
};

const getNoteChapterList = async (id) => {
  const result = await findByIdChapterService(id);
  noteChapterList.value = result.data.data;
  noteNodeList.value = noteChapterList.value[0].nodeVOS;
  if (result.data.data.length == 0) {
    chapterDisabledValue.value = true;
    chapterInfo.value = "暂无章节";
    noteNodeList.value = [];
    nodeDisabledValue.value = true;
    nodeInfo.value = "暂无节点";
  }
};

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
</script>

<template>
  <el-dialog
    style="z-index: 99"
    v-model="dialogVisible"
    :show-close="false"
    width="56%"
  >
    <div class="my-header note" style="margin-bottom: 5px">
      <el-select
        class="note-input"
        v-model="noteDTO.chapterTitle"
        filterable
        :disabled="chapterDisabledValue"
        :placeholder="chapterInfo"
        style="width: 240px; margin-right: 10px"
        @change="changeChapter"
      >
        <el-option
          v-for="(node, index) in noteChapterList"
          :key="index"
          :label="node.title"
          :value="index"
          style="padding-left: 12px; font-size: 13px"
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
          v-for="(node, index) in noteNodeList"
          :key="node.id"
          :label="node.title"
          :value="index"
          style="padding-left: 12px; font-size: 13px"
        />
      </el-select>

      <div class="dialog-button">
        <el-button style="width: 80px" type="primary" @click="saveNote" round
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
        style="border-bottom: 1px solid #ccc"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        :mode="mode"
      />
      <Editor
        style="height: 400px; overflow-y: hidden"
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        :mode="mode"
        @onCreated="handleCreated"
      />
    </div>
  </el-dialog>
  <div
    class="course"
    v-if="isCourseInfo"
    style="width: 100%; height: 100vh; overflow: hidden"
  >
    <el-card
      style="
        width: 100%;
        height: 100vh;
        padding: 0;
        box-sizing: border-box;
        --el-card-padding: 0;
      "
      shadow="never"
    >
      <el-container>
        <el-aside width="240px">
          <div class="common-layout">
            <el-container>
              <el-header class="course-header">
                <img class="axier-img" src="@/assets/工作室水印1.png" alt=""
              /></el-header>
              <el-main class="course-main">
                <div class="demo-collapse">
                  <!-- 折叠列表 -->
                  <el-collapse
                    class="collapse-item"
                    v-model="activeName"
                    @change="handleChange"
                    accordion
                  >
                    <el-collapse-item
                      :name="index + 1"
                      v-for="(chapter, index) in chapterList"
                      :key="index"
                    >
                      <template #title>
                        <span
                          style="
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                            margin: 8px;
                            font-weight: 700;
                          "
                          >{{ chapter.title }}</span
                        >
                      </template>
                      <!-- 时间线 -->
                      <el-timeline class="time-line">
                        <el-timeline-item
                          :type="node.type"
                          :color="node.color"
                          :size="node.size"
                          :hollow="node.hollow"
                          style="height: 45px"
                          class="time-item"
                          v-for="(node, i) in chapter.nodeVOS"
                          @click="handleClick(node, index, i)"
                          :key="i"
                        >
                          <button style="" class="time-item-btn">
                            {{ node.title }}
                          </button>
                        </el-timeline-item>
                      </el-timeline>
                    </el-collapse-item>
                  </el-collapse>
                </div>
              </el-main>
              <el-footer class="course-footer">
                <span class="tools-span">
                  <el-button @click="backHome" type="primary" text circle>
                    <el-icon size="16"><HomeFilled /></el-icon>
                  </el-button>
                  <el-button @click="courseIntro" type="warning" text circle>
                    <el-icon size="16"><Tickets /></el-icon>
                  </el-button>
                  <el-button @click="clickAddNote" type="success" text circle>
                    <el-icon size="16"><Notebook /></el-icon>
                  </el-button>
                </span>
              </el-footer>
            </el-container>
          </div>
        </el-aside>
        <el-main style="background-color: #f4f4f5; height: 100vh; padding: 0">
          <div class="video" style="width: 100%; height: 100vh">
            <video :src="URL" controls></video></div
        ></el-main>
      </el-container>
    </el-card>
  </div>
  <div class="intro" v-else>
    <div class="table">
      <!-- 课程信息 -->
      <div class="table-1">
        <h1>
          课程信息
          <!-- 顶部按钮 -->
          <span style="float: right; display: flex; align-items: center">
            <el-button
              style="margin: 0"
              type="danger"
              circle
              size="small"
              :icon="CloseBold"
              @click="hideCoursePaper"
              class="closeBold"
            />
          </span>
        </h1>
        <!-- 课程信息 -->
        <el-divider style="margin: 20px 0" />
        <el-form :model="courseVO" label-width="auto" style="max-width: 720px">
          <el-form-item class="form-item" label="课程标题">
            <div class="input">{{ courseVO.name }}</div>
          </el-form-item>
          <el-form-item class="form-item" label="专业名称" prop="region">
            <div class="input">{{ courseVO.majorName }}</div>
          </el-form-item>
          <el-form-item class="form-item" label="课程学分">
            <div class="input">{{ courseVO.point }}</div>
          </el-form-item>

          <el-form-item class="form-item" label="上课班级">
            <el-select
              v-model="checkedTeamList"
              multiple
              clearable
              collapse-tags
              filterable
              placeholder="选择班级"
              popper-class="custom-header"
              :max-collapse-tags="5"
              style="width: 100%; cursor: default"
              class="title-input"
              disabled
            >
              <template #header>
                <el-checkbox
                  v-model="checkAll"
                  :indeterminate="indeterminate"
                  @change="handleCheckAll"
                >
                  全选
                </el-checkbox>
              </template>
              <el-option
                v-for="item in teamList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
                style="padding-left: 12px; cursor: none"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="form-item" label="开课时间">
            <el-date-picker
              v-model="time"
              class="time-input"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              readonly
            />
          </el-form-item>
          <el-form-item
            style="margin-top: 30px"
            class="form-item"
            label="课程简介"
          >
            <el-input
              class="textarea-input"
              resize="none"
              rows="15"
              v-model="courseVO.introduce"
              type="textarea"
              maxlength="1000"
              placeholder="请输入课程简介 [1000字以内]"
              show-word-limit
              readonly
              input-style="{
                overflow-y: scroll !important;
                overflow-x: none !important;
                -ms-overflow-style: none !important;
                scrollbar-width: none !important;  
              }"
            />
          </el-form-item>
        </el-form>
      </div>
      <div class="table-2">
        <img class="axier-img2" src="@/assets/工作室水印1.png" alt="" />
        <img class="axier-img3" src="@/assets/工作室水印2.png" alt="" />
      </div>
      <div class="table-3">
        <img
          class="axier-img2"
          style="float: right"
          src="@/assets/工作室水印1.png"
          alt=""
        />
      </div>
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

.axier-img2 {
  width: 200px;
  height: 100px;
}

.axier-img3 {
  position: absolute;
  width: 300px;
  height: 200px;
  left: -24px;
  bottom: -80px;
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
.note {
  .dialog-button {
    position: absolute;
    top: 20px;
    right: 20px;
  }
}

.time-line {
  width: 100%;
  height: 100%;
  padding: 15px 10px 0px 10px;
  box-sizing: border-box;

  .time-item {
    background-color: none;

    .time-item-btn {
      position: relative;
      top: -2.5px;
      display: inline-block;
      width: 165px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 14px;
      color: #303133;
      border: none;
      text-align: left;
      background: none;
      cursor: pointer;
    }

    .time-item-btn:hover {
      color: royalblue;
    }
  }
}

* {
  margin: 0;
  padding: 0;
}
.video {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
}

.video video {
  width: 100%;
  height: 100vh;
}

.course {
  .common-layout {
    position: relative;
    height: 100vh;
    box-sizing: border-box;
  }

  .course-header {
    position: absolute;
    padding: 2%;
    width: 100%;
    height: 11vh;
    min-height: 88px;
    box-sizing: border-box;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0px 0px 4px 1px #dedfe0;
    background-color: #f5f7fa;
    color: white;
    font-size: 18px;
    font-weight: 700;
    z-index: 1;

    .axier-img {
      width: 100%;
      height: 12vh;
      min-height: 88px;
      position: relative;
      background-size: cover;
      background-repeat: no-repeat;
      background-position: center;
    }
  }

  .course-main {
    position: absolute;
    top: 11vh;
    padding: 2%;
    width: 100%;
    height: 80vh;
    background-color: white;
    box-sizing: border-box;
    overflow-y: scroll;
    overflow-x: none;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  .course-footer {
    position: absolute;
    top: 91vh;
    padding: 0 5%;
    width: 100%;
    height: 9vh;
    min-height: 50px;
    background-color: #dedfe0;
    box-sizing: border-box;
    display: flex;
    align-items: center;

    .del-span {
      float: left;
      width: 20%;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .edit-span {
      float: right;
      margin-left: 5%;
      width: 75%;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 20px;
      border: 1px solid #ccc;
      background-color: #fff;
    }

    .tools-span {
      float: right;
      margin-left: 5%;
      width: 90%;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 20px;
      border: 1px solid #ccc;
      background-color: #fff;
    }
  }
}

.intro {
  .input {
    width: 100%;
    height: 32px;
    border-radius: 5px;
    border: 1px solid #dcdfe6;
    border-bottom: 3px solid royalblue;
    border-right: 2px solid royalblue;
    font-size: 15px;
    font-weight: 700;
    padding: 0 10px;
    box-sizing: border-box;
    line-height: 30px;
    background-color: #fff;
  }

  .addAndEdit-form {
    padding: 8% 20%;
    width: 100%;
    height: 60%;
    box-sizing: border-box;

    .node-title {
      font-size: 16px;
      font-weight: 700;
      border-bottom: 3px solid royalblue;
      border-right: 2px solid royalblue;
      --el-input-focus-border-color: royalblue;
      --el-input-placeholder-color: #a8abb2;
      --el-input-text-color: #606266;
      --el-input-icon-color: white;
      border-radius: 6px;
      margin-bottom: 20px;
    }

    .node-intro {
      line-height: 1.5;
      border-bottom: 3px solid royalblue;
      border-right: 2px solid royalblue;
      --el-input-focus-border-color: royalblue;
      --el-input-placeholder-color: #a8abb2;
      --el-input-text-color: #606266;
      --el-input-icon-color: white;
      border-radius: 6px;
    }

    .node-content {
      --el-color-primary: royalblue;
      margin: 20px 0;
      .el-upload .el-upload-dragger {
        border-bottom: 3px solid royalblue;
        border-right: 2px solid royalblue;
      }
    }
  }
  .table {
    position: relative;
    margin-top: 80px;
    padding: 20px;
    left: 10%;
    bottom: 0px;
    width: 80%;
    min-width: 800px;
    height: 800px;
    box-sizing: content-box;
  }

  .table-1 {
    position: absolute;
    padding: 36px 4% 0 4%;
    left: 20%;
    bottom: 2%;
    width: 60%;
    min-width: 600px;
    height: 760px;
    background-color: #fafafa;
    z-index: 3;
    opacity: 0;
    border: 2px solid royalblue;
    border-radius: 2px;
    box-shadow: 0.5px 1px 1.5px 1.2px #e4e7ed;
    box-sizing: border-box;

    .closeBold {
      color: #f56c6c;
    }

    .semiSelect {
      color: #e6a23c;
    }

    .select {
      color: #67c23a;
    }

    .closeBold:hover,
    .semiSelect:hover,
    .select:hover {
      transform: scale(1.1);
      color: #fff;
    }
  }

  .table-2 {
    position: absolute;
    width: 60%;
    min-width: 600px;
    height: 800px;
    background-color: #fafafa;
    z-index: 2;
    opacity: 0;
    border: 1px solid royalblue;
    border-radius: 2px;
    box-shadow: 0.5px 1px 1.5px 1.2px #e4e7ed;
  }

  .table-3 {
    position: absolute;
    width: 60%;
    min-width: 600px;
    height: 800px;
    background-color: #fafafa;
    z-index: 1;
    opacity: 0;
    border: 1px solid royalblue;
    border-radius: 2px;
    box-shadow: 0.5px 1px 1.5px 1.2px #e4e7ed;
  }

  .form-item {
    margin-top: 24px;
    --el-color-primary: royalblue;
    --el-fill-color-light: white;
    cursor: default;

    .textarea-input {
      line-height: 1.5;
      --el-input-focus-border-color: royalblue;
      --el-input-placeholder-color: #a8abb2;
      --el-input-text-color: #606266;
      --el-input-icon-color: white;
      border-radius: 6px;
    }

    .title-input {
      font-size: 16px;
      font-weight: 700;
      border-bottom: 3px solid royalblue;
      border-right: 2px solid royalblue;
      --el-input-focus-border-color: royalblue;
      --el-input-placeholder-color: #a8abb2;
      --el-input-text-color: #606266;
      --el-input-icon-color: white;
      border-radius: 6px;
      --el-input-bg-color: none;
    }
  }
}
</style>
