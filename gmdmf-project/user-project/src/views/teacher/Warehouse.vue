<script lang="ts" setup>
import {
  saveSubjectService,
  deleteSubjectService,
  updateSubjectService,
  findByWarehouseIdSubjectService,
} from "@/api/Subject";
import { nextTick, onBeforeUnmount, onMounted, ref, shallowRef } from "vue";
import { Check, Close } from "@element-plus/icons-vue";
import { usePaperStore } from "@/stores/paper";
import { useRouter } from "vue-router";
// editor
import { useWarehouseStore } from "@/stores/warehouse";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { ElLoading, ElMessage, ElMessageBox } from "element-plus";
import $ from "jquery";

// 变量
const router = useRouter();
const total = ref(0);
const maxTotal = ref(80);
const typeValue = ref("单选");
const typeValueList = ref(["单选", "多选", "填空", "简答"]);
const warehouseId = useWarehouseStore().warehouse;
const dialogFormVisible = ref(false);
const dialogFormVisible2 = ref(false);
const changeAddOrUpdate = ref("Add");
const level = ["简单题", "中等题", "重难题"];
const currentId = ref(null);
const questionHtml = ref();

// 富文本编辑器
const mode = ref("simple");
const editorRefFill = shallowRef();
const editorRefAnswer = shallowRef();
const editorRefAddOrUpdate = shallowRef();
const toolbarConfig = ref({});
const editorConfig = ref({
  placeholder: "请选择要查看的题目...",
  minHeight: 300,
});
const editorConfig2 = ref({
  placeholder: "请输入问题 [1000字以内]",
  maxLength: 1000,
  minHeight: 300,
});

const handleCreatedFill = (editor) => {
  editorRefFill.value = editor;
  editor.disable();
};

const handleCreatedAnswer = (editor) => {
  editorRefAnswer.value = editor;
  editor.disable();
};

const handleCreatedAddOrUpdate = (editor) => {
  editorRefAddOrUpdate.value = editor;
};

onBeforeUnmount(() => {
  const editor1 = editorRefAddOrUpdate.value;
  if (editor1 == null) return;
  editor1.destroy();

  const editor2 = editorRefFill.value;
  if (editor2 == null) return;
  editor2.destroy();

  const editor3 = editorRefAnswer.value;
  if (editor3 == null) return;
  editor3.destroy();
});

interface Subject {
  id: string;
  warehouseId: number;
  type: string;
  label: string;
  question: string;
  options: any;
  answer: string;
  analysis: string;
  level: string;
  pub: any;
  pass: number;
  coefficient: number;
}

// 单选
const singleList = ref<Subject[]>([]);
const manyList = ref<Subject[]>([]);
const fillList = ref<Subject[]>([]);
const answerList = ref<Subject[]>([]);
const subjectVO = ref<Subject>();
const subjectDTO = ref<Subject>({
  id: null,
  warehouseId: warehouseId,
  type: typeValue.value,
  label: "",
  question: "",
  options: "",
  answer: "",
  analysis: "",
  level: "简单题",
  pub: 0,
  pass: 100,
  coefficient: 0.7,
});

// 页面加载事件 --------------------------------
const loading = ref(true);
onMounted(async () => {
  const loadingInstance = ElLoading.service({ fullscreen: true });
  await getSubjectListFindByWid();
  await changeSubjectListType();
  nextTick(() => {
    setTimeout(() => {
      loadingInstance.close();
      loading.value = false;
    }, 1000)
  });
});

// 返回教师端
const backCourse = () => {
  useWarehouseStore().removeWarehouse();
  router.push("/teacher");
};

// 题目代码 ------------------------------------

// 获取题库题目
const getSubjectListFindByWid = async () => {
  const result = await findByWarehouseIdSubjectService(warehouseId);
  singleList.value = result.data.data["单选"];
  manyList.value = result.data.data["多选"];
  fillList.value = result.data.data["填空"];
  answerList.value = result.data.data["简答"];
};

// 切换题目类型
const changeSubjectListType = async () => {
  await cleanSubject();

  total.value = 0;
  if (typeValue.value == "单选" && singleList.value != undefined) {
    subjectVO.value = singleList.value[0];
    try {
      subjectVO.value.options = JSON.parse(singleList.value[0].options);
    } catch (e) {
      subjectVO.value.options = singleList.value[0].options;
    }
    total.value = singleList.value.length;
  } else if (typeValue.value == "多选" && manyList.value != undefined) {
    subjectVO.value = manyList.value[0];
    try {
      subjectVO.value.options = JSON.parse(manyList.value[0].options);
    } catch (e) {
      subjectVO.value.options = manyList.value[0].options;
    }
    total.value = manyList.value.length;
  } else if (typeValue.value == "填空" && fillList.value != undefined) {
    subjectVO.value = fillList.value[0];
    total.value = fillList.value.length;
  } else if (typeValue.value == "简答" && answerList.value != undefined) {
    subjectVO.value = answerList.value[0];
    total.value = answerList.value.length;
  }

  // 清除数据
  selectSubject(0);
  getSubject(0);
};

// 选中题目突出显示
const selectSubject = (index) => {
  document.querySelector(".check-box-active")
    ? document
        .querySelector(".check-box-active")
        .classList.remove("check-box-active")
    : "";
  if (typeValue.value == "单选" && singleList.value != undefined) {
    document.querySelectorAll(".single-checkbox")
      ? document
          .querySelectorAll(".single-checkbox")
          [index].classList.add("check-box-active")
      : "";
  } else if (typeValue.value == "多选" && manyList.value != undefined) {
    document.querySelectorAll(".many-checkbox")
      ? document
          .querySelectorAll(".many-checkbox")
          [index].classList.add("check-box-active")
      : "";
  } else if (typeValue.value == "填空" && fillList.value != undefined) {
    document.querySelectorAll(".fill-checkbox")
      ? document
          .querySelectorAll(".fill-checkbox")
          [index].classList.add("check-box-active")
      : "";
  } else if (typeValue.value == "简答" && answerList.value != undefined) {
    document.querySelectorAll(".answer-checkbox")
      ? document
          .querySelectorAll(".answer-checkbox")
          [index].classList.add("check-box-active")
      : "";
  }
};

// 查询单个题目
const getSubject = async (index) => {
  if (typeValue.value == "单选" && singleList.value != undefined) {
    subjectVO.value = singleList.value[index];
    try {
      subjectVO.value.options = JSON.parse(singleList.value[index].options);
    } catch (e) {
      subjectVO.value.options = singleList.value[index].options;
    }
    currentId.value = subjectVO.value.id;
  } else if (typeValue.value == "多选" && manyList.value != undefined) {
    subjectVO.value = manyList.value[index];
    try {
      subjectVO.value.options = JSON.parse(manyList.value[index].options);
    } catch (e) {
      subjectVO.value.options = manyList.value[index].options;
    }
    currentId.value = subjectVO.value.id;
  } else if (typeValue.value == "填空" && fillList.value != undefined) {
    subjectVO.value = fillList.value[index];
    currentId.value = subjectVO.value.id;
  } else if (typeValue.value == "简答" && answerList.value != undefined) {
    subjectVO.value = answerList.value[index];
    currentId.value = subjectVO.value.id;
  }

  await selectSubject(index);
};

// 清除数据
const cleanSubject = () => {
  subjectVO.value = undefined;
  optionList.value = [
    {
      option: "A",
      value: "",
    },
    {
      option: "B",
      value: "",
    },
    {
      option: "C",
      value: "",
    },
    {
      option: "D",
      value: "",
    },
  ];
  checkboxGroup.value = [];
  subjectDTO.value = {
    id: null,
    warehouseId: warehouseId,
    type: typeValue.value,
    label: "",
    question: "",
    options: "",
    answer: "",
    analysis: "",
    level: "简单题",
    pub: 0,
    pass: 100,
    coefficient: 0.7,
  };
  checkboxGroup.value = [];
};

const focusQuestionInput = () => {
  $(".question-svg").animate(
    {
      width: "98%",
    },
    500
  );
};

const blurQuestionInput = () => {
  if (subjectDTO.value.question.trim() == "") {
    $(".question-svg").animate(
      {
        width: "0",
      },
      300
    );
  }
};

// 存储题目选项
const optionList = ref([
  {
    option: "A",
    value: "",
  },
  {
    option: "B",
    value: "",
  },
  {
    option: "C",
    value: "",
  },
  {
    option: "D",
    value: "",
  },
]);

// 收集答案
const checkboxGroup = ref([]);

const changeChecked = (value) => {
  if (typeValue.value == "单选") {
    checkboxGroup.value = [value.pop()];
  }
};

const showFrom = () => {
  if (typeValue.value == "单选" || typeValue.value == "多选") {
    dialogFormVisible.value = true;
  } else {
    dialogFormVisible2.value = true;
  }
};

// 校验数据
const checkSingleData = () => {
  if (subjectDTO.value.label.trim() == "") {
    ElMessage({
      message: "题目名不能为空",
      type: "error",
    });
    return true;
  }

  if (subjectDTO.value.question.trim() == "") {
    ElMessage({
      message: "问题不能为空",
      type: "error",
    });
    return true;
  }

  // 校验选项
  let flag = false;
  optionList.value.forEach((item) => {
    if (item.value.trim() == "") {
      flag = true;
    }
  });

  if (flag) {
    ElMessage({
      message: "选项不能为空",
      type: "error",
    });
    return flag;
  }

  if (checkboxGroup.value.length == 0) {
    ElMessage({
      message: "答案不能为空",
      type: "error",
    });
    return true;
  }

  if (checkboxGroup.value.length == 1 && typeValue.value == "多选") {
    ElMessage({
      message: "多选题至少需要两个答案",
      type: "error",
    });
    return true;
  }

  return false;
};

// 填充数据
const fillData = () => {
  if (typeValue.value == "单选" || typeValue.value == "多选") {
    subjectDTO.value.options = JSON.stringify(optionList.value);
    subjectDTO.value.answer = checkboxGroup.value.join("");
  } else {
    subjectDTO.value.question = questionHtml.value;
  }
  subjectDTO.value.type = typeValue.value;

  if (subjectDTO.value.level == "简单题") {
    subjectDTO.value.pass = 70.0;
    subjectDTO.value.coefficient = 0.7;
  } else if (subjectDTO.value.level == "中等题") {
    subjectDTO.value.pass = 50.0;
    subjectDTO.value.coefficient = 0.5;
  } else if (subjectDTO.value.level == "重难题") {
    subjectDTO.value.pass = 30.0;
    subjectDTO.value.coefficient = 0.3;
  }
};

const submitSingleOrMany = async () => {
  if (checkSingleData()) {
    return;
  }
  fillData();

  const result = await saveSubjectService(subjectDTO.value);
  result.data.message
    ? ElMessage.success(result.data.message)
    : ElMessage.success("题目添加成功");
  await getSubjectListFindByWid();
  await changeSubjectListType();
  await cleanSubject();
  dialogFormVisible.value = false;
};

const checkAnswerData = () => {
  if (subjectDTO.value.label.trim() == "") {
    ElMessage({
      message: "题目名不能为空",
      type: "error",
    });
    return true;
  }

  if (
    questionHtml.value.trim() == "" ||
    questionHtml.value.trim() == "<p></p>" ||
    questionHtml.value.trim() == "<p> &nbsp;</p>"
  ) {
    ElMessage({
      message: "问题不能为空",
      type: "error",
    });
    return true;
  }
  
  if (typeValue.value != "简答" && subjectDTO.value.answer.trim() == "") {
    ElMessage({
      message: "答案不能为空",
      type: "error",
    });
    return true;
  }

  return false;
};

const submitFillOrAnswer = async () => {
  if (checkAnswerData()) {
    return;
  }
  await fillData();

  const result = await saveSubjectService(subjectDTO.value);
  result.data.message
    ? ElMessage.success(result.data.message)
    : ElMessage.success("题目添加成功");
  await getSubjectListFindByWid();
  await changeSubjectListType();
  await cleanSubject();
  dialogFormVisible2.value = false;
};

// 删除题目
const deleteSubject = async () => {
  if (typeValue.value == "单选") {
    if (singleList.value == undefined || singleList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  } else if (typeValue.value == "多选") {
    if (manyList.value == undefined || manyList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  } else if (typeValue.value == "填空") {
    if (fillList.value == undefined || fillList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  } else {
    if (answerList.value == undefined || answerList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  }

  ElMessageBox.confirm("确定删除题目?", "温馨提示", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const result = await deleteSubjectService(currentId.value);
      result.data.message
        ? ElMessage.success(result.data.message)
        : ElMessage.success("题目添加成功");
      await getSubjectListFindByWid();
      await changeSubjectListType();
    })
    .catch(() => {
      null;
    });
};

const addSubject = async () => {
  await cleanSubject();
  changeAddOrUpdate.value = "Add";
  nextTick(() => {
    showFrom();
  });
  subjectDTO.value.question = "";
};

// 回显数据
const echoSubject = async () => {
  if (typeValue.value == "单选") {
    if (singleList.value == undefined || singleList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  } else if (typeValue.value == "多选") {
    if (manyList.value == undefined || manyList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  } else if (typeValue.value == "填空") {
    if (fillList.value == undefined || fillList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  } else {
    if (answerList.value == undefined || answerList.value.length == 0) {
      ElMessage.warning("请先选中题目");
      return;
    }
  }

  subjectDTO.value = subjectVO.value;
  if (typeValue.value == "单选" || typeValue.value == "多选") {
    optionList.value = subjectVO.value.options;
    checkboxGroup.value = subjectVO.value.answer.split("");
  }

  if (subjectVO.value.pub == "公开") {
    subjectDTO.value.pub = "2";
  } else {
    subjectDTO.value.pub = "0";
  }

  changeAddOrUpdate.value = "Update";
  showFrom();
};

// 修改题目
const editSubject = async () => {
  if (
    (typeValue.value == "单选" || typeValue.value == "多选") &&
    checkSingleData()
  ) {
    return;
  }

  if (
    (typeValue.value == "填空" || typeValue.value == "简答") &&
    checkAnswerData()
  ) {
    return;
  }

  fillData();

  const result = await updateSubjectService(subjectDTO.value);
  result.data.message
    ? ElMessage.success(result.data.message)
    : ElMessage.success("题目修改成功");
  dialogFormVisible.value = false;
  dialogFormVisible2.value = false;
  cleanSubject();
  await getSubjectListFindByWid();
  await changeSubjectListType();
};

const show = () => {
  nextTick(() => {
    try {
      questionHtml.value = subjectVO.value.question;
    } catch (e) {
      console.log(e.message);
    }
  });
};

const hide = () => {
  questionHtml.value = "";
  changeSubjectListType();
  if (typeValue.value == "简答") {
    try {
      editorRefAnswer.value.disable();
    } catch (e) {
      null;
    }
  } else if (typeValue.value == "填空") {
    try {
      editorRefFill.value.disable();
    } catch (e) {
      null;
    }
  }
};
</script>
<template>
  <!-- 选择题表单 -->
  <el-dialog
    class="dialog-single"
    v-model="dialogFormVisible"
    width="40%"
    style="padding: 24px; min-height: 700px; min-width: 520px"
    @close="cleanSubject"
  >
    <template #header>
      <div class="label-box">
        <el-input
          v-model="subjectDTO.label"
          maxlength="30"
          placeholder="请输入题目名"
          type="text"
          class="label-input"
        />
      </div>
    </template>
    <div class="dialog-single-header">
      <div class="level-radio-box">
        <el-segmented v-model="subjectDTO.level" :options="level" />
      </div>
      <div class="public-radio-box">
        <span>公开</span>
        <el-switch
          v-model="subjectDTO.pub"
          class="publicBox"
          style="margin-left: 8px"
          size="large"
          inline-prompt
          :active-icon="Check"
          :inactive-icon="Close"
          active-value="2"
          inactive-value="0"
        />
      </div>
    </div>
    <div class="dialog-single-content">
      <div class="question-box">
        <el-input
          v-model="subjectDTO.question"
          maxlength="200"
          placeholder="请输入问题"
          show-word-limit
          type="text"
          class="question-input"
          :input-style="{ border: 'none' }"
          @focus="focusQuestionInput"
          @blur="blurQuestionInput"
        >
        </el-input>
        <span class="question-svg"></span>
      </div>
      <div class="option-box">
        <el-checkbox-group
          v-model="checkboxGroup"
          @change="changeChecked"
          size="small"
        >
          <div
            class="option-group"
            v-for="(item, index) in optionList"
            :key="index"
          >
            <span class="option-group-left">
              <el-checkbox
                class="option-checkbox"
                :value="item.option"
                size="large"
              >
                <span class="checkbox-svg"></span>
              </el-checkbox>
            </span>
            <span class="option-group-right">
              <el-input
                v-model="item.value"
                maxlength="200"
                placeholder="请输入选项"
                show-word-limit
                type="text"
                class="option-input"
                :input-style="{ border: 'none' }"
              />
            </span>
          </div>
        </el-checkbox-group>
      </div>
      <div class="analysis-box">
        <textarea
          v-model="subjectDTO.analysis"
          class="analysis-input"
          placeholder="请输入 1000 字以内的解析 [可选]"
          rows="6"
        ></textarea>
      </div>
    </div>
    <div class="dialog-single-footer">
      <el-divider border-style="dashed">
        <el-button
          style="background-color: royalblue"
          color="#626aef"
          :dark="true"
          :icon="Check"
          circle
          @click="
            changeAddOrUpdate == 'Add' ? submitSingleOrMany() : editSubject()
          "
          text
          bg
        />
      </el-divider>
    </div>
  </el-dialog>

  <!-- 简答题表单 -->
  <el-dialog
    class="dialog-single"
    v-model="dialogFormVisible2"
    width="60%"
    style="padding: 24px; min-height: 1000px; min-width: 600px"
    @close="cleanSubject()"
    @open="show()"
    @closed="hide()"
  >
    <template #header>
      <div class="label-box">
        <el-input
          v-model="subjectDTO.label"
          maxlength="30"
          placeholder="请输入题目名"
          type="text"
          class="label-input"
        />
      </div>
    </template>
    <div class="dialog-single-header">
      <div class="level-radio-box">
        <el-segmented v-model="subjectDTO.level" :options="level" />
      </div>
      <div class="public-radio-box">
        <span>公开</span>
        <el-switch
          v-model="subjectDTO.pub"
          class="mt-2"
          style="margin-left: 8px"
          size="large"
          inline-prompt
          :active-icon="Check"
          :inactive-icon="Close"
          active-value="1"
          inactive-value="0"
        />
      </div>
    </div>
    <div class="dialog-single-content" style="height: 760px">
      <div class="question-box" v-show="typeValue == '填空'">
        <el-input
          v-model="subjectDTO.answer"
          maxlength="200"
          placeholder="请输入答案"
          show-word-limit
          type="text"
          class="question-input"
          :input-style="{ border: 'none' }"
          @focus="focusQuestionInput"
          @blur="blurQuestionInput"
        />
        <span class="question-svg"></span>
      </div>
      <div class="editor-box" contentEditable="{false}">
        <Toolbar
          id="toolbar"
          :editor="editorRefAddOrUpdate"
          :defaultConfig="toolbarConfig"
          :mode="mode"
        />
        <Editor
          id="editor"
          style="
            height: 400px;
            overflow-y: none;
            overflow-x: none;
            -ms-overflow-style: none;
            scrollbar-width: none;
          "
          v-model="questionHtml"
          :defaultConfig="editorConfig2"
          :mode="mode"
          @onCreated="handleCreatedAddOrUpdate"
        />
      </div>
      <div class="analysis-box">
        <textarea
          v-model="subjectDTO.analysis"
          class="analysis-input"
          placeholder="请输入 1000 字以内的解析 [可选]"
          rows="6"
        ></textarea>
      </div>
    </div>
    <div class="dialog-single-footer">
      <el-divider border-style="dashed">
        <el-button
          style="background-color: royalblue"
          color="#626aef"
          :dark="true"
          icon="Check"
          circle
          @click="
            changeAddOrUpdate == 'Add' ? submitFillOrAnswer() : editSubject()
          "
          text
          bg
        />
      </el-divider>
    </div>
  </el-dialog>

  <!-- 题库页面 -->
  <el-row v-show="loading == false" :gutter="24" style="width: 100%">
    <el-col :span="1"><div class="grid-content ep-bg-purple" /></el-col>
    <el-col :span="22">
      <el-card shadow="never" style="margin: 20px 0">
        <el-container>
          <el-header>
            <el-tabs
              v-model="typeValue"
              class="demo-tabs"
              @tab-change="changeSubjectListType"
            >
              <el-tab-pane
                :label="type"
                :name="type"
                v-for="(type, index) in typeValueList"
                :key="index"
              ></el-tab-pane>
            </el-tabs>
          </el-header>
          <el-container>
            <div class="box">
              <!-- 左边 -->
              <div class="box-left">
                <div class="box-left-header">
                  <!-- 题目类型选择 -->
                  <span class="header-text"> 题目列表 </span>
                  <!-- 选中题目统计 -->
                  <span class="header-count">
                    <div>{{ total }} / {{ maxTotal }}</div>
                  </span>
                </div>
                <!-- 题目列表 -->
                <div class="box-left-content">
                  <div class="subjectList">
                    <!-- 单选列表 -->
                    <div
                      v-show="typeValue == '单选'"
                      class="check-box checkbox-wrapper single-checkbox"
                      v-for="(item, index) in singleList"
                      :key="index"
                      @click="getSubject(index)"
                    >
                      <span class="check-box-text">{{ item.label }}</span>
                    </div>
                    <!-- 多选列表 -->
                    <div
                      v-show="typeValue == '多选'"
                      class="check-box checkbox-wrapper many-checkbox"
                      v-for="(item, index) in manyList"
                      :key="index"
                      @click="getSubject(index)"
                    >
                      <span class="check-box-text">{{ item.label }}</span>
                    </div>
                    <!-- 填空列表 -->
                    <div
                      v-show="typeValue == '填空'"
                      class="check-box checkbox-wrapper fill-checkbox"
                      v-for="(item, index) in fillList"
                      :key="index"
                      @click="getSubject(index)"
                    >
                      <span class="check-box-text">{{ item.label }}</span>
                    </div>
                    <!-- 简答列表 -->
                    <div
                      v-show="typeValue == '简答'"
                      class="check-box checkbox-wrapper answer-checkbox"
                      v-for="(item, index) in answerList"
                      :key="index"
                      @click="getSubject(index)"
                    >
                      <span class="check-box-text">{{ item.label }}</span>
                    </div>
                  </div>
                </div>
                <div class="box-left-footer">
                  <span class="back">
                    <button @click="backCourse">
                      <el-icon size="20" style="color: white"
                        ><ArrowLeftBold
                      /></el-icon>
                    </button>
                  </span>
                  <span class="submit" @click="addSubject"
                    ><button>
                      <el-icon size="22" style="color: white"
                        ><Plus
                      /></el-icon></button
                  ></span>
                </div>
              </div>
              <!-- 右边 -->
              <div class="box-right">
                <el-card
                  style="
                    width: 98%;
                    margin: 0 1%;
                    height: 100%;
                    box-sizing: border-box;
                  "
                  shadow="never"
                  >{{ subjectVO != undefined ? subjectVO.label : "题目" }}

                  <div style="float: right">
                    <el-button
                      style="background-color: #f0f2f5"
                      type="danger"
                      circle
                      text
                      bg
                      @click="deleteSubject"
                    >
                      <el-icon size="18"><DeleteFilled /></el-icon>
                    </el-button>
                    <el-button
                      type="primary"
                      round
                      dark
                      text
                      bg
                      style="
                        width: 80px;
                        background-color: royalblue;
                        color: #fff;
                      "
                      @click="echoSubject"
                    >
                      <el-icon size="18"><Edit /></el-icon>
                    </el-button>
                  </div>
                  <el-divider border-style="dashed" />
                  <div
                    v-if="typeValue == '单选' || typeValue == '多选'"
                    class="single-question"
                  >
                    {{ subjectVO != undefined ? subjectVO.question : "问题" }}
                  </div>
                  <div v-show="typeValue == '单选' || typeValue == '多选'">
                    <div class="single-view" v-for="(index, i) in 4" :key="i">
                      <span class="single-view-option">{{
                        index == 1
                          ? "A"
                          : index == 2
                          ? "B"
                          : index == 3
                          ? "C"
                          : "D"
                      }}</span>
                      <span class="single-view-text">
                        {{
                          subjectVO != undefined &&
                          (typeValue == "单选" || typeValue == "多选")
                            ? subjectVO.options[index - 1].value
                            : ""
                        }}
                      </span>
                    </div>
                  </div>
                  <div
                    v-if="typeValue == '填空' && subjectVO != undefined"
                    class="fill-view"
                  >
                    <Editor
                      v-model="subjectVO.question"
                      :defaultConfig="editorConfig"
                      :mode="mode"
                      @onCreated="handleCreatedFill"
                    />
                  </div>
                  <div
                    v-if="typeValue == '简答' && subjectVO != undefined"
                    class="answer-view"
                  >
                    <Editor
                      v-model="subjectVO.question"
                      :defaultConfig="editorConfig"
                      :mode="mode"
                      @onCreated="handleCreatedAnswer"
                    />
                  </div>
                  <div
                    class="analysis"
                    v-if="
                      (typeValue != '简答' && subjectVO != undefined) ||
                      typeValue == '单选' ||
                      typeValue == '多选'
                    "
                  >
                    <span class="analysis-svg">题目解析</span>
                    <span class="analysis-text">
                      {{
                        subjectVO != undefined && subjectVO.analysis != ""
                          ? subjectVO.analysis
                          : "暂无解析"
                      }}
                    </span>
                  </div>
                </el-card>
              </div>
            </div>
          </el-container>
        </el-container>
      </el-card>
    </el-col>
    <el-col :span="1"><div class="grid-content ep-bg-purple" /></el-col>
  </el-row>
</template>
<style lang="scss" scoped>
.w-e-scroll {
  overflow-y: none;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.editor-box {
  margin: 20px 1%;
  width: 98%;
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

.analysis-box {
  margin: 1%;
  width: 98%;
  border-radius: 6px;
  box-sizing: border-box;

  .analysis-input {
    padding: 2%;
    width: 100%;
    height: 100%;
    font-size: 14px;
    font-family: "微软雅黑";
    line-height: 1.5;
    resize: none;
    border-radius: 6px;
    overflow-y: scroll;
    overflow-x: none;
    -ms-overflow-style: none;
    outline: none;
    scrollbar-width: none;
    box-sizing: border-box;
    border: 2px solid rgb(118, 150, 248);
  }

  .analysis-input:focus {
    border: 2px solid royalblue;
  }
}

.option-box {
  margin-top: 20px;
  padding: 1%;
  width: 100%;
  height: 250px;
  box-sizing: border-box;

  .option-group:hover {
    border: 2px dashed royalblue;
  }

  .option-group {
    position: relative;
    margin-bottom: 20px;
    width: 100%;
    height: 40px;
    // background-color: pink;
    border: 2px dashed rgb(118, 150, 248);
    border-radius: 6px;
    box-shadow: 0px 0px 1px 1px #e4e7ed;
    box-sizing: border-box;

    .option-group-left {
      position: absolute;
      float: left;
      width: 8%;
      height: 100%;
      border-radius: 6px 0 0 6px;
      box-sizing: border-box;
      display: flex;
      justify-content: center;
      align-items: center;

      .option-checkbox {
        position: relative;
        padding-top: 1%;
        padding-left: 18%;
        display: flex;
        justify-content: center;
        align-items: center;
        --el-checkbox-size: 24px;
        box-sizing: border-box;
        border-radius: 50%;
        --el-checkbox-checked-bg-color: royalblue;
        --el-checkbox-border-color: none;
        --el-checkbox-border-radius: 50%;
        --el-checkbox-bg-color: #303133;
        --el-checkbox-checked-text-color: none;
        --el-checkbox-input-border: none;
        --el-checkbox-checked-icon-color: none;
        color: royalblue;
      }
    }

    .option-group-right {
      float: right;
      width: 94%;
      height: 100%;
      box-sizing: border-box;
      border-radius: 0 6px 6px 0;

      .option-input {
        top: 0;
        width: 100%;
        height: 100%;
        font-size: 14px;
        --el-input-focus-border-color: none;
        --el-input-border-color: none;
        --el-input-hover-border-color: none;
        --el-input-placeholder-color: #a8abb2;
        --el-input-text-color: #606266;
        --el-input-icon-color: white;
        border-radius: 0px 6px 6px 0px;
        --el-input-bg-color: none;
      }
    }
  }
}

.question-box {
  position: relative;
  margin-top: 20px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .question-input {
    width: 98%;
    height: 40px;
    --el-input-focus-border-color: white;
    --el-input-border-color: white;
    --el-input-hover-border-color: white;
    --el-input-placeholder-color: #a8abb2;
    --el-input-text-color: #606266;
    --el-input-icon-color: white;
    --el-input-bg-color: none;
    border-radius: 4px;
    box-shadow: 0.5px 1px 1.5px 1.2px #e4e7ed;
  }

  .question-svg {
    position: absolute;
    bottom: 0px;
    width: 0;
    height: 2px;
    background-color: royalblue;
  }
}

.label-box {
  position: relative;
  margin-top: 20px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;

  .label-input {
    width: 98%;
    height: 40px;
    box-sizing: border-box;
    border-left: 4px solid royalblue;
    --el-input-focus-border-color: white;
    --el-input-border-color: white;
    --el-input-hover-border-color: white;
    --el-input-placeholder-color: #a8abb2;
    --el-input-text-color: #606266;
    --el-input-icon-color: white;
    --el-input-bg-color: none;
    border-radius: 1px 4px 4px 1px;
  }
}

.level-radio-box .el-segmented {
  --el-segmented-item-selected-color: #fefefe;
  --el-segmented-item-selected-bg-color: royalblue;
  --el-border-radius-base: 16px;
  font-weight: 600;
  font-size: 15px;
  line-height: 2;
  color: #909399;
  background-color: #f0f2f5;
  box-shadow: 1px 1px 1px 1px #e4e7ed;
}

.dialog-single {
  .dialog-single-header {
    position: relative;
    width: 100%;
    height: 50px;

    .level-radio-box {
      float: left;
      width: 205px;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .public-radio-box {
      float: right;
      margin-top: 2px;
      width: 100px;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;

      span {
        font-size: 15px;
        font-weight: 600;
        line-height: 2;
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
      }

      .el-switch {
        --el-switch-on-color: royalblue;
        --el-switch-off-color: #d4d7de;
      }
    }
  }

  .dialog-single-content {
    width: 100%;
    height: 490px;
  }

  .dialog-single-footer {
    width: 100%;
    color: #778ae7;
  }
}

.box {
  margin: 0% 1%;
  width: 98%;
  min-width: 840px;
  height: 800px;
}

.box-left {
  position: relative;
  float: left;
  width: 24%;
  min-width: 160px;
  max-width: 300px;
  height: 100%;
  box-sizing: border-box;
  border-radius: 4px;
  border: 1px solid black;

  .box-left-header {
    position: absolute;
    top: 0;
    width: 100%;
    height: 6%;
    box-sizing: border-box;
    padding-top: 5px;

    .header-text {
      float: left;
      margin-left: 10px;
      width: 50%;
      height: 100%;
      display: flex;
      align-items: center;
      box-sizing: border-box;
      font-size: 17px;
      font-weight: 700;
    }

    .header-count {
      float: left;
      width: 45%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      box-sizing: border-box;

      div {
        position: absolute;
        right: 12px;
        padding: 4px 10px;
        color: white;
        border-radius: 6px;
        background: black;
        text-align: center;
        font-size: 14px;
        font-weight: 700;
        box-sizing: border-box;
      }
    }
  }

  .box-left-content {
    position: absolute;
    top: 6%;
    width: 100%;
    height: 86%;
    padding: 2% 0;
    box-sizing: border-box;
    overflow-y: scroll;
    overflow-x: none;
    -ms-overflow-style: none;
    scrollbar-width: none;

    .subjectList {
      overflow-y: scroll;
      overflow-x: none;
      -ms-overflow-style: none;
      scrollbar-width: none;
    }

    .subjectList::-webkit-scrollbar {
      display: none;
    }

    .check-box {
      margin: 1.5% 5%;
      padding: 9px;
      width: 90%;
      height: 40px;
      background-color: none;
      border: 1px solid #c0c4cc;
      box-sizing: border-box;
      border-radius: 8px;
      cursor: pointer;
      box-sizing: border-box;
    }

    .check-box-text {
      position: absolute;
      display: inline-block;
      font-size: 14px;
      font-weight: 600;
      width: 80%;
      height: 22px;
      line-height: 22px;
      margin-left: 3%;
      overflow: hidden;
    }

    .checkSubject {
      margin: 9px !important;
      width: 22px !important;
      height: 22px !important;
    }

    .checkbox-wrapper:hover .check {
      stroke-dashoffset: 0;
    }

    .checkbox-wrapper {
      position: relative;
      display: inline-block;
    }

    .checkbox-wrapper .background {
      fill: #dbe1f2;
      transition: ease all 0.6s;
      -webkit-transition: ease all 0.6s;
    }

    .checkbox-wrapper .stroke {
      fill: none;
      stroke: #fff;
      stroke-miterlimit: 10;
      stroke-width: 2px;
      stroke-dashoffset: 100;
      stroke-dasharray: 100;
      transition: ease all 0.6s;
      -webkit-transition: ease all 0.6s;
    }

    .checkbox-wrapper svg {
      margin-top: 0.2px;
      width: 21px;
      height: 21px;
    }

    .checkbox-wrapper .check {
      fill: none;
      stroke: #fff;
      stroke-linecap: round;
      stroke-linejoin: round;
      stroke-width: 2px;
      stroke-dashoffset: 22;
      stroke-dasharray: 22;
      transition: ease all 0.6s;
      -webkit-transition: ease all 0.6s;
    }

    .checkbox-wrapper input[type="checkbox"] {
      position: absolute;
      width: 100%;
      height: 100%;
      left: 0;
      top: 0;
      margin: 0;
      opacity: 0;
      -appearance: none;
      -webkit-appearance: none;
    }

    .checkbox-wrapper input[type="checkbox"]:hover {
      cursor: pointer;
    }

    .checkbox-wrapper input[type="checkbox"]:checked + svg .background {
      fill: #778ae7;
    }

    .checkbox-wrapper input[type="checkbox"]:checked + svg .stroke {
      stroke-dashoffset: 0;
    }

    .checkbox-wrapper input[type="checkbox"]:checked + svg .check {
      stroke-dashoffset: 0;
    }
  }

  .box-left-footer {
    position: absolute;
    bottom: 0px;
    width: 100%;
    min-width: 200px;
    height: 7%;
    border-radius: 4px;
    // background-color: gold;

    .back {
      float: left;
      width: 26%;
      height: 98%;
      box-sizing: border-box;
      display: flex;
      justify-content: center;
      align-items: center;

      button {
        width: 80%;
        height: 80%;
        text-align: center;
        border: 1px solid #fff;
        background: royalblue;
        border-radius: 5px;
        display: flex;
        justify-content: center;
        align-items: center;
        box-sizing: border-box;
        cursor: pointer;
      }
    }

    .submit {
      float: left;
      width: 74%;
      height: 98%;
      box-sizing: border-box;
      display: flex;
      justify-content: center;
      align-items: center;
      button {
        margin-right: 6px;
        width: 96%;
        height: 80%;
        text-align: center;
        border: 1px solid #fff;
        background: royalblue;
        border-radius: 5px;
        box-sizing: border-box;
        cursor: pointer;
      }
    }
  }

  .check-box-active {
    border: 1px solid #606266 !important;
    transition: all 0.3s;
  }
}

.box-right {
  position: relative;
  float: right;
  width: 76%;
  min-width: 600px;
  height: 100%;

  .single-question {
    margin-bottom: 4%;
    padding-left: 2%;
    width: 100%;
    height: 60px;
    border-left: 4px solid #778ae7;
    display: flex;
    align-items: center;
    box-sizing: border-box;
    box-shadow: 1px 1px 2px 2px #ebe8e8;
    border-radius: 1px 4px 4px 1px;
    overflow: hidden;
    font-weight: 700;
  }

  .fill-question {
    margin-bottom: 5%;
    padding: 2%;
    width: 100%;
    max-height: 200px;
    border-left: 4px solid #778ae7;
    box-sizing: border-box;
    box-shadow: 1px 1px 2px 2px #ebe8e8;
    border-radius: 1px 4px 4px 1px;
    overflow: hidden;
    font-weight: 700;
  }

  .analysis {
    position: relative;
    margin-top: 5%;
    width: 100%;
    height: 320px;
    border: 1px solid #778ae7;
    box-sizing: border-box;
    box-shadow: 1px 1px 2px 2px #ebe8e8;
    border-radius: 5px;

    .analysis-svg {
      position: absolute;
      top: 0;
      right: 20px;
      width: 100px;
      height: 40px;
      display: flex;
      align-items: center;
      box-sizing: border-box;
      justify-content: center;
      background-color: royalblue;
      color: white;
      font-size: 14px;
      font-weight: 700;
      border-radius: 0 0 20% 20%;
    }

    .analysis-text {
      display: inline-block;
      padding: 2%;
      width: 100%;
      height: 100%;
      box-sizing: border-box;
      overflow-y: scroll;
      overflow-x: none;
      -ms-overflow-style: none;
      scrollbar-width: none;
      line-height: 1.5;
    }
  }

  .single-view {
    margin-bottom: 2%;
    width: 100%;
    height: 40px;
    min-width: 400px;
    box-shadow: 1px 1px 2px 2px #edecec;
    border-radius: 6px;

    .single-view-option {
      float: left;
      width: 5%;
      min-width: 24px;
      height: 100%;
      background-color: #778ae7;
      display: flex;
      justify-content: center;
      align-items: center;
      box-sizing: border-box;

      border-radius: 6px 0 0 6px;
      border: 1px solid #778ae7;
      border-right: none;
      font-weight: 700;
      color: #fef6f6;
      overflow: hidden;
    }

    .single-view-text {
      float: left;
      padding-left: 2%;
      width: 95%;
      height: 100%;
      display: flex;
      align-items: center;
      box-sizing: border-box;
      border: 2px dashed #778ae7;
      border-left: none;
      border-radius: 0px 6px 6px 0px;
      font-weight: 700;
      font-size: 14px;
    }
  }

  .fill-view {
    padding: 2%;
    max-height: 280px;
    border: 1px solid #778ae7;
    box-shadow: 1px 1px 2px 2px #ebe8e8;
    border-radius: 5px;
    overflow-y: scroll;
    overflow-x: none;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  .answer-view {
    overflow-y: scroll;
    overflow-x: none;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
}
</style>
