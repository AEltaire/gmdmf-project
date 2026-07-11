<script lang="ts" setup>
import {
  ref,
  reactive,
  onMounted,
  Ref,
  onUpdated,
  onBeforeUpdate,
  nextTick,
} from "vue";
import { findByPaperIdPaperSubjectService } from "@/api/PaperSubject";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { usePaperApprovalStore } from "@/stores/paperApproval";
import {
  findByPaperApprovalIdPaperStudentService,
  approvalPaperStudentService,
} from "@/api/PaperStudent";
import { approvalPaperApprovalService } from "@/api/PaperApproval";
// editor
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { onBeforeUnmount, shallowRef } from "vue";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { findByIdPaperService } from "@/api/Paper";
import { useUserInfoStore } from "@/stores/userInfo";
import $ from "jquery";

const router = useRouter();
const paperApprovalId = usePaperApprovalStore().paperApproval;

const total = ref(0);
const maxTotal = ref(40);
const activeName = ref("single");

// 富文本编辑器
const editorRef = shallowRef();
const editorRef2 = shallowRef();
const valueHtml = ref("<p></p>");
const mode = ref("simple");
const toolbarConfig = ref({});
const editorConfig = ref({ placeholder: "点击此处输入答案..." });
const handleCreated = (editor) => {
  editorRef.value = editor;
};

const handleCreated2 = (editor) => {
  editorRef2.value = editor;
  const item: any = document.querySelector(".w-e-scroll");
  item.style.scrollbarWidth = "none";
  editor.disable();
};

const destroy = async (editor) => {
  if (editor == null) return;
  await editor.destroy();
};

const paperSinglePoint = ref([]);
const paperManyPoint = ref([]);
const paperFillPoint = ref([]);
const paperAnswerPoint = ref([]);
const fillInput = ref("");
const answerInput = ref("");

interface Subject {
  id: number;
  subjectId: number;
  type: string;
  label: string;
  input: string;
  question: string;
  options: any;
  point: number;
  score: number;
  answer: string;
  analysis: string;
  level: string;
  pub: string;
}

const optionsValue = ref([
  {
    value: "single",
    label: "单选",
  },
  {
    value: "many",
    label: "多选",
  },
  {
    value: "fill",
    label: "填空",
  },
  {
    value: "answer",
    label: "简答",
  },
]);

const singleList = ref<Subject[]>([]);
const manyList = ref<Subject[]>([]);
const fillList = ref<Subject[]>([]);
const answerList = ref<Subject[]>([]);
const subjectVO = ref<Subject>({
  id: null,
  subjectId: null,
  type: "",
  label: "",
  input: "",
  question: "",
  options: null,
  point: null,
  score: null,
  answer: "",
  analysis: "",
  level: "",
  pub: "",
});

// 页面加载
onMounted(async () => {
  await getSubjectList();
  await handleChange();
  await getSubject(0);
});

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
  destroy(editorRef2.value);
});

// 返回
const back = () => {
  router.back();
};

// 获取所有题目
const getSubjectList = async () => {
  const result = await findByPaperApprovalIdPaperStudentService(
    paperApprovalId
  );

  singleList.value = result.data.data["单选"];
  manyList.value = result.data.data["多选"];
  fillList.value = result.data.data["填空"];
  answerList.value = result.data.data["简答"];

  if (answerList.value != undefined) {
    activeName.value = "answer";
  }
  if (fillList.value != undefined) {
    activeName.value = "fill";
  }
  if (manyList.value != undefined) {
    manyList.value.forEach((many) => {
      many.options = JSON.parse(many.options);
    });
    activeName.value = "many";
  }
  if (singleList.value != undefined) {
    singleList.value.forEach((single) => {
      single.options = JSON.parse(single.options);
    });
    activeName.value = "single";
  }

  if (paperApprovalId == null || result.data.message == "试卷为空") {
    optionsValue.value = [];
    if (singleList.value != undefined) {
      optionsValue.value.push({
        value: "single",
        label: "单选",
      });
    }

    if (manyList.value != undefined) {
      optionsValue.value.push({
        value: "many",
        label: "多选",
      });
    }

    if (fillList.value != undefined) {
      optionsValue.value.push({
        value: "fill",
        label: "填空",
      });
    }

    if (answerList.value != undefined) {
      optionsValue.value.push({
        value: "answer",
        label: "简答",
      });
    }
  } else {
    optionsValue.value = [];
    if (singleList.value != undefined) {
      paperSinglePoint.value = [];
      result.data.data["单选"].forEach((item) => {
        paperSinglePoint.value.push({
          id: item.id,
          subjectId: item.subjectId,
          point: item.point,
          score: item.score,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "single",
        label: "单选",
      });
    }

    if (manyList.value != undefined) {
      paperManyPoint.value = [];
      result.data.data["多选"].forEach((item) => {
        let list = item.input.split("");
        paperManyPoint.value.push({
          id: item.id,
          subjectId: item.subjectId,
          point: item.point,
          score: item.score,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "many",
        label: "多选",
      });
    }

    if (fillList.value != undefined) {
      paperFillPoint.value = [];
      result.data.data["填空"].forEach((item) => {
        paperFillPoint.value.push({
          id: item.id,
          subjectId: item.subjectId,
          point: item.point,
          score: item.score,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "fill",
        label: "填空",
      });
    }

    if (answerList.value != undefined) {
      paperAnswerPoint.value = [];
      result.data.data["简答"].forEach((item) => {
        paperAnswerPoint.value.push({
          id: item.id,
          subjectId: item.subjectId,
          point: item.point,
          score: item.score,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "answer",
        label: "简答",
      });
    }
  }
};

// 查询单个题目
const getSubject = async (index) => {
  if (activeName.value == "single" && singleList.value != undefined) {
    subjectVO.value = singleList.value[index];
    document.querySelectorAll(".single-tag").forEach((item: any) => {
      if (item.value == paperSinglePoint.value[index].input) {
        item.checked = true;
      } else {
        item.checked = false;
      }
    });
  } else if (activeName.value == "many" && manyList.value != undefined) {
    subjectVO.value = manyList.value[index];
    document.querySelectorAll(".many-checkbox").forEach((item: any) => {
      item.checked = false;
      if (paperManyPoint.value[index].input.trim() != "") {
        paperManyPoint.value[index].input.split("").forEach((option) => {
          if (item.value == option) {
            item.checked = true;
          }
        });
      }
    });
  } else if (activeName.value == "fill" && fillList.value != undefined) {
    subjectVO.value = fillList.value[index];
    fillInput.value = paperFillPoint.value[index].input;
  } else if (activeName.value == "answer" && answerList.value != undefined) {
    subjectVO.value = answerList.value[index];
    answerInput.value = paperAnswerPoint.value[index].input;
  }
  nextTick(async () => {
    await active(index);
  });
};

const active = (index) => {
  document.querySelector(".active")
    ? document.querySelector(".active").classList.remove("active")
    : null;
  if (activeName.value == "single") {
    $(".nav-label-single")[index].classList.add("active");
  } else if (activeName.value == "many") {
    $(".nav-label-many")[index].classList.add("active");
  } else if (activeName.value == "fill") {
    $(".nav-label-fill")[index].classList.add("active");
  } else if (activeName.value == "answer") {
    $(".nav-label-answer")[index].classList.add("active");
  }
};

const clickSingle = (id, input) => {
  paperSinglePoint.value.forEach((item) => {
    if (item.subjectId == id) {
      item.input = input;
    }
  });
};

const clickMany = (id) => {
  let list = [];
  document.querySelectorAll(".many-checkbox:checked").forEach((item: any) => {
    list.push(item.value);
  });
  paperManyPoint.value.forEach((item) => {
    if (item.subjectId == id) item.input = list;
  });
};

const handleChange = async () => {
  if (activeName.value == "single" && singleList.value != undefined) {
    subjectVO.value = singleList.value[0];
    total.value = singleList.value.length;
    await getSubject(0);
  } else if (activeName.value == "many" && manyList.value != undefined) {
    subjectVO.value = manyList.value[0];
    total.value = manyList.value.length;
    await getSubject(0);
  } else if (activeName.value == "fill" && fillList.value != undefined) {
    subjectVO.value = fillList.value[0];
    total.value = fillList.value.length;
  } else if (activeName.value == "answer" && answerList.value != undefined) {
    subjectVO.value = answerList.value[0];
    total.value = answerList.value.length;
  }
  await getSubject(0);
};

const handleChangePoint = (value: number) => {
  if (activeName.value == "single") {
    paperSinglePoint.value.forEach((item) => {
      if (item.subjectId == subjectVO.value.subjectId) {
        item.point = value;
      }
    });
  } else if (activeName.value == "many") {
    paperManyPoint.value.forEach((item) => {
      if (item.subjectId == subjectVO.value.subjectId) {
        item.point = value;
      }
    });
  } else if (activeName.value == "fill") {
    paperFillPoint.value.forEach((item) => {
      if (item.subjectId == subjectVO.value.subjectId) {
        item.point = value;
      }
    });
  } else if (activeName.value == "answer") {
    paperAnswerPoint.value.forEach((item) => {
      if (item.subjectId == subjectVO.value.subjectId) {
        item.point = value;
      }
    });
  }
};

const approvalPaperStudent = async () => {
  let paperStudentList = [];
  if (paperSinglePoint.value.length > 0) {
    paperSinglePoint.value.forEach((item) => {
      paperStudentList.push(item);
    });
  }
  if (paperManyPoint.value.length > 0) {
    paperManyPoint.value.forEach((item) => {
      paperStudentList.push(item);
    });
  }
  if (paperFillPoint.value.length > 0) {
    paperFillPoint.value.forEach((item) => {
      paperStudentList.push(item);
    });
  }
  if (paperAnswerPoint.value.length > 0) {
    paperAnswerPoint.value.forEach((item) => {
      paperStudentList.push(item);
    });
  }

  await approvalPaperStudentService(paperStudentList);
  const result = await approvalPaperApprovalService(paperApprovalId);
  ElMessage.success(result.data.message ? result.data.message : "批改成功");
  router.back();
};
</script>
<template>
  <!-- 题库页面 -->
  <el-row :gutter="24" style="width: 100%">
    <el-col :span="1"><div class="grid-content ep-bg-purple" /></el-col>
    <el-col :span="22">
      <el-container>
        <el-main>
          <el-card style="width: 99%" shadow="never">
            <div class="common-layout" style="">
              <el-container>
                <el-container>
                  <div class="box">
                    <!-- 左边 -->
                    <div class="box-left">
                      <div class="radio-input">
                        <div class="info">
                          <span class="question">
                            <el-select
                              v-model="activeName"
                              placeholder="Select"
                              style="width: 100px"
                              @change="handleChange"
                            >
                              <el-option
                                v-for="item in optionsValue"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                              /> </el-select
                          ></span>
                          <span class="steps"
                            >{{ total }} / {{ maxTotal }}</span
                          >
                        </div>
                        <div class="subjectList-box">
                          <div
                            v-show="activeName == 'single'"
                            v-for="(item, index) in singleList"
                            :data-id="item.id"
                            class="check-box checkbox-wrapper nav-label-single"
                            @click="getSubject(index)"
                            :key="index"
                          >
                            <input type="checkbox" class="check-box-input" />
                            <span :data-id="item.id" class="check-box-text">{{
                              item.label
                            }}</span>
                          </div>
                          <div
                            v-show="activeName == 'many'"
                            v-for="(item, index) in manyList"
                            :data-id="item.id"
                            @click="getSubject(index)"
                            class="check-box checkbox-wrapper nav-label-many"
                            :key="index"
                          >
                            <input type="checkbox" class="check-box-input" />
                            <span :data-id="item.id" class="check-box-text">{{
                              item.label
                            }}</span>
                          </div>
                          <div
                            v-show="activeName == 'fill'"
                            v-for="(item, index) in fillList"
                            :data-id="item.id"
                            @click="getSubject(index)"
                            class="check-box checkbox-wrapper nav-label-fill"
                            :key="index"
                          >
                            <input type="checkbox" class="check-box-input" />

                            <span :data-id="item.id" class="check-box-text">{{
                              item.label
                            }}</span>
                          </div>
                          <div
                            v-show="activeName == 'answer'"
                            v-for="(item, index) in answerList"
                            :data-id="item.id"
                            @click="getSubject(index)"
                            class="check-box checkbox-wrapper nav-label-answer"
                            :key="index"
                          >
                            <input type="checkbox" class="check-box-input" />

                            <span :data-id="item.id" class="check-box-text">{{
                              item.label
                            }}</span>
                          </div>
                        </div>
                      </div>
                      <div class="box-left-footer">
                        <button class="back" @click="back">
                          <div class="svg-wrapper">
                            <svg
                              t="1718616215103"
                              class="icon"
                              viewBox="0 0 1024 1024"
                              version="1.1"
                              xmlns="http://www.w3.org/2000/svg"
                              p-id="14870"
                              width="24"
                              height="24"
                            >
                              <path
                                d="M64 320l448-192 448 192-448 192z"
                                fill="#ffffff"
                                p-id="14871"
                              ></path>
                              <path
                                d="M832 374.4V576h64v-227.2z"
                                fill="#ffffff"
                                p-id="14872"
                              ></path>
                              <path
                                d="M224 448v307.2l288 140.8 288-140.8V448l-288 128z"
                                fill="#ffffff"
                                p-id="14873"
                              ></path>
                            </svg>
                          </div>
                        </button>
                        <button class="save" @click="approvalPaperStudent">
                          <div class="svg-wrapper">
                            <el-icon size="20"><Select /></el-icon>
                          </div>
                        </button>
                      </div>
                    </div>
                    <!-- 右边 -->
                    <div class="box-right">
                      <el-card
                        style="
                          width: 100%;
                          height: 100%;
                          box-sizing: border-box;
                        "
                        shadow="never"
                        class="subjectList-box"
                        ><div class="question">
                          {{ subjectVO.label }}
                          <div style="float: right" class="score">
                            <el-input-number
                              v-model="subjectVO.point"
                              :min="0"
                              :max="subjectVO.score"
                              @change="handleChangePoint"
                              step="0.5"
                            />
                          </div>
                        </div>
                        <el-divider
                          style="border-top: 1px dashed #409eff"
                          border-style="dashed"
                        />

                        <div class="single-option single-answer">
                          <div class="answer">
                            <Editor
                              class="answer-box-analysis"
                              v-model="subjectVO.question"
                              :defaultConfig="editorConfig"
                              :mode="mode"
                              @onCreated="handleCreated2"
                            />
                          </div>
                          <el-divider />
                          <div
                            v-show="activeName == 'single'"
                            style="padding-bottom: 20px"
                          >
                            <div
                              class="radio-input"
                              v-for="(option, index) in subjectVO.options"
                              :key="index"
                            >
                              <div>
                                <label class="label">
                                  <input
                                    type="radio"
                                    class="single-tag"
                                    :name="subjectVO.label"
                                    :value="option.option"
                                    @click="
                                      clickSingle(subjectVO.id, option.option)
                                    "
                                    disabled
                                  />
                                  <p class="text">
                                    {{ option.value }}
                                  </p>
                                </label>
                              </div>
                            </div>
                          </div>
                          <div
                            v-show="activeName == 'many'"
                            style="padding-bottom: 20px"
                          >
                            <div
                              class="radio-input"
                              v-for="(option, index) in subjectVO.options"
                              :key="index"
                            >
                              <label class="label">
                                <input
                                  type="checkbox"
                                  class="many-checkbox"
                                  :name="subjectVO.label"
                                  :value="option.option"
                                  @click="clickMany(subjectVO.id)"
                                  disabled
                                />
                                <p class="text">
                                  {{ option.value }}
                                </p>
                              </label>
                            </div>
                          </div>
                          <div
                            v-show="activeName == 'fill'"
                            style="padding-bottom: 20px"
                            class="radio-input"
                          >
                            <label class="cyberpunk-checkbox-label label">
                              <div class="textInputWrapper" style="width: 100%">
                                <input
                                  placeholder="请输入答案，多个答案之间用 “&amp;” 分隔"
                                  type="text"
                                  class="textInput"
                                  maxlength="200"
                                  v-model="subjectVO.input"
                                  disabled
                                />
                              </div>
                            </label>
                          </div>
                          <div id="editor-box" v-show="activeName == 'answer'">
                            <Toolbar
                              style=""
                              :editor="editorRef"
                              :defaultConfig="toolbarConfig"
                              :mode="mode"
                            />
                            <Editor
                              id="input-editor"
                              class="answer-box-analysis"
                              :defaultConfig="editorConfig"
                              style="min-height: 350px"
                              :mode="mode"
                              v-model="answerInput"
                              @onCreated="handleCreated"
                              disabled
                            />
                          </div>
                        </div>
                      </el-card>
                    </div>
                  </div>
                </el-container>
              </el-container>
            </div> </el-card
        ></el-main>
      </el-container>
    </el-col>
    <el-col :span="1"><div class="grid-content ep-bg-purple" /></el-col>
  </el-row>
</template>
<style lang="scss" scoped>
.active {
  border: 1px solid #606266 !important;
}

.answer-box-analysis {
  overflow-y: scroll;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.cyberpunk-checkbox-label {
  font-size: 18px;
  color: #fff;
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
}

.textInputWrapper {
  position: relative;
  width: 90%;
  margin: 12px 5px;
  --accent-color: #435dd8;
}

.textInputWrapper:before,
.textInputWrapper:after {
  content: "";
  left: 0;
  right: 0;
  position: absolute;
  pointer-events: none;
  bottom: -1px;
  z-index: 4;
  width: 100%;
}

.textInputWrapper:focus-within:before {
  border-bottom: 1px solid var(--accent-color);
}

.textInputWrapper:focus-within:before {
  border-bottom: 1px solid var(--accent-color);
  transform: scaleX(1);
}

.textInputWrapper:focus-within:after {
  border-bottom: 2px solid var(--accent-color);
  transform: scaleX(1);
}

.textInputWrapper:after {
  content: "";
  transform: scaleX(0);
  transition: transform 250ms cubic-bezier(0, 0, 0.2, 1) 0ms;
  will-change: transform;
  border-bottom-color: var(--accent-color);
}

.textInputWrapper .textInput {
  border: none;
  height: 40px;
  transition-duration: 200ms;
  transition-property: background-color;
  color: #000000;
  font-size: 14px;
  font-weight: 500;
  padding: 5px;
  width: 100%;
  background-color: #fefefe;
}

.textInputWrapper .textInput:focus,
.textInputWrapper .textInput:active {
  outline: none;
}

.textInputWrapper:focus-within .textInput,
.textInputWrapper .textInput:focus,
.textInputWrapper .textInput:active {
  background-color: #ffffff;
}

.textInputWrapper:focus-within .textInput::placeholder {
  opacity: 0;
}

.subjectList-box {
  overflow-y: scroll;
  overflow-x: none;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.subjectList-box::-webkit-scrollbar {
  display: none;
}

.subjectList-box {
  .radio-input {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .radio-input * {
    box-sizing: border-box;
    padding: 0;
    margin: 0;
  }

  .radio-input label {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 0px 16px;
    width: 100%;
    cursor: pointer;
    height: 40px;
    position: relative;
  }

  .radio-input label::before {
    position: absolute;
    content: "";
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 220px;
    height: 45px;
    z-index: -1;
    transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    border-radius: 10px;
    border: 2px solid transparent;
  }
  .radio-input label:hover::before {
    transition: all 0.2s ease;
    background-color: #2a2e3c;
  }

  .radio-input .label:has(input:checked)::before {
    background-color: #2d3750;
    border-color: #435dd8;
    height: 50px;
  }
  .radio-input .label .text {
    color: #000;
  }

  .radio-input .label input[type="radio"] {
    background-color: #ababdb;
    appearance: none;
    width: 17px;
    height: 17px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .radio-input .label input[type="radio"]:checked {
    background-color: #435dd8;
    -webkit-animation: puls 0.7s forwards;
    animation: pulse 0.7s forwards;
  }

  .radio-input .label input[type="radio"]:before {
    content: "";
    width: 6px;
    height: 6px;
    border-radius: 50%;
    transition: all 0.1s cubic-bezier(0.165, 0.84, 0.44, 1);
    background-color: #fff;
    transform: scale(0);
  }

  .radio-input .label input[type="radio"]:checked::before {
    transform: scale(1);
  }

  // -----------------
  .radio-input .label input[type="checkbox"] {
    background-color: #ababdb;
    appearance: none;
    width: 17px;
    height: 17px;
    border-radius: 24%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .radio-input .label input[type="checkbox"]:checked {
    // background-color: #435dd8;
    -webkit-animation: puls 0.7s forwards;
    animation: pulse 0.7s forwards;
  }

  .radio-input .label input[type="checkbox"]:before {
    content: "";
    width: 100%;
    height: 100%;
    border-radius: 24%;
    transition: all 0.2s cubic-bezier(0.165, 0.84, 0.44, 1);
    background-color: #6b7dd7;
    transform: scale(0);
  }

  .radio-input .label input[type="checkbox"]:checked::before {
    transform: scale(1);
  }
}

.question {
  font-size: 16px;
  font-weight: 600;

  .score {
    position: relative;
    top: -5px;
    margin-right: 10px;
    font-size: 20px;
    color: #409eff;
  }
}

.nav-label-active {
  border: 1px solid #606266 !important;
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
}

.check-box-text {
  position: absolute;
  display: inline-block;
  font-size: 14px;
  font-weight: 600;
  width: 78%;
  height: 22px;
  line-height: 22px;
  margin-left: 3%;
  overflow: hidden;
  // background-color: pink;
}

.check-box-input {
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

// ---------------------------

.box {
  margin: 0% 1%;
  width: 98%;
  min-width: 840px;
  height: 800px;
  // background-color: red;
}

input {
  width: 10px;
  height: 10px;
}

.box-left {
  position: relative;
  float: left;
  width: 24%;
  min-width: 160px;
  max-width: 300px;
  height: 100%;
  box-sizing: border-box;
  --el-border-color-light: #409eff;
}

.box-left-footer {
  position: absolute;
  bottom: 1px;
  // margin: 0 5%;
  width: 94%;
  min-width: 160px;
  height: 5.2%;
  border-radius: 4px;
}

.box-right {
  position: relative;
  float: right;
  width: 76%;
  min-width: 600px;
  height: 100%;
  --el-border-color-light: #409eff;
}

.box-right {
  button {
    // bottom: 5%;
    font-family: inherit;
    font-size: 14px;
    color: white;
    display: flex;
    align-items: center;
    border: none;
    border-radius: 4px;
    overflow: hidden;
    transition: all 0.2s;
    cursor: pointer;
    text-align: center;
  }

  .group {
    display: flex;
    line-height: 30px;
    align-items: center;
    position: relative;
    width: 100%;
    height: 50px;
    margin-top: 5px;
    margin-bottom: 10px;
  }

  .input {
    width: 100%;
    height: 40px;
    line-height: 30px;
    padding-left: 0.6rem;
    border: 2px dashed #6b7dd7;
    border-radius: 8px;
    outline: none;
    background-color: #f8fafc;
    color: #0d0c22;
    transition: 0.5s ease;
  }

  .del {
    // position: absolute;
    // bottom: 0;
    float: left;
    margin-top: 16px;
    width: 16%;
    height: 32px;
    background-color: #f48787;
  }

  @keyframes fly-1 {
    from {
      transform: translateY(0.1em);
    }

    to {
      transform: translateY(-0.1em);
    }
  }
}

.box-right-main {
  margin-top: 2%;
  height: 28%;
}

// 左边 -------------------------
.box-left {
  .radio-input {
    display: flex;
    flex-direction: column;
    background: #fff;
    color: #000;
    border-radius: 4px;
    // margin: 0 5%;
    margin-bottom: 2%;
    padding-bottom: 2%;
    width: 94%;
    height: 92%;
    border: 1px solid #c0c4cc;
  }

  .info {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .question {
    margin: 12px 10px;
    color: rgb(49, 49, 49);
    font-size: 1rem;
    line-height: 1rem;
    font-weight: 800;
  }

  .radio-input label {
    display: flex;
    margin: 2% 5%;
    width: 32px;
    height: 32px;
    line-height: 36px;
    font-size: 12px;
    font-weight: 600;
    border-radius: 6px;
    cursor: pointer;
    border: 1px solid #dcdfe6;
    color: #000;
    transition: 0.3s ease;
  }

  .radio-input label span {
    margin-left: 2px;
  }

  .radio-input label:hover {
    background-color: #ebedf0;
    border: 1px solid #c0c4cc;
  }

  .nav-label-active {
    border: 1px solid #606266 !important;
  }

  .radio-input:has(input[value="value-2"]:checked) .result.success {
    display: flex;
  }

  .radio-input:has(input:not([value="value-2"]):checked) .result.error {
    display: flex;
  }

  button {
    position: relative;
    font-family: inherit;
    font-size: 14px;
    background: royalblue;
    color: white;
    display: flex;
    align-items: center;
    border: none;
    border-radius: 4px;
    overflow: hidden;
    transition: all 0.2s;
    cursor: pointer;
  }

  .steps {
    background-color: rgb(0, 0, 0);
    padding: 6px 8px;
    margin: 12px 10px;
    color: #fff;
    border-radius: 4px;
    font-size: 14px;
    line-height: 12px;
    font-weight: 600;
  }

  .back {
    float: left;
    width: 24%;
    min-width: 48px;
    height: 100%;
  }

  .back .svg-wrapper {
    margin: 10%;
    width: 80%;
    height: 60%;
  }

  .save {
    float: right;
    width: 74%;
    min-width: 130px;
    height: 100%;
  }

  .save .svg-wrapper {
    margin: 10%;
    width: 80%;
    height: 60%;
    font-family: "宋体";
    padding-top: 2.4%;
  }

  button:active {
    transform: scale(0.95);
  }

  @keyframes fly-1 {
    from {
      transform: translateY(0.1em);
    }

    to {
      transform: translateY(-0.1em);
    }
  }
}

.submit-btn {
  width: 24%;
  min-width: 48px;
  height: 100%;
  border-color: #435dd8;
  background: royalblue;
  color: white;
}

// 右边 -------------------------
.box-right {
  .single-option {
    margin: 0 0 3% 0;
    padding: 0.5rem 1rem;
    width: 100%;
    height: 100%;
    min-height: 89.2vh;
    line-height: 36px;
    font-size: 16px;
    font-family: "宋体";
  }

  .analysis {
    width: 90%;
    height: 72px;
    margin-top: 10px;
    padding: 0.6rem 0.8rem;
    font-size: 14px;
    resize: none;
    border: 2px solid #778ae7;
    border-radius: 4px;
  }

  textArea::-webkit-scrollbar {
    width: 0;
    height: 0;
  }
}
</style>
