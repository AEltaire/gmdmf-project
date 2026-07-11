<script lang="ts" setup>
import { ref, reactive, onMounted, Ref, onUpdated, onBeforeUpdate } from "vue";
import { findByPaperIdPaperSubjectService } from "@/api/PaperSubject";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { usePaperStore } from "@/stores/paper";
import {
  saveOrUpdatePaperStudentService,
  findByPaperApprovalIdPaperStudentService,
  findByPaperIdAndStudentIdService,
} from "@/api/PaperStudent";
// editor
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { onBeforeUnmount, shallowRef } from "vue";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { findByIdPaperService } from "@/api/Paper";
import {
  savePaperApprovalService,
  findByPaperIdPaperApprovalService,
} from "@/api/PaperApproval";
import { useUserInfoStore } from "@/stores/userInfo";

const router = useRouter();
const paperId = usePaperStore().paper;
const userInfo = useUserInfoStore().userInfo;
const paperApprovalId = ref(null);

const total = ref(0);
const maxTotal = ref(40);
const activeName = ref("single");
const notUpdate = ref(false);
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
const testStudentDTO = ref({
  id: null,
  paperId: paperId,
});
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

const paperSingleAnswer = ref([]);
const paperManyAnswer = ref([]);
const paperFillAnswer = ref([]);
const paperAnswerAnswer = ref([]);
const fillInput = ref("");
const answerInput = ref("");
const type = ref("");

interface Subject {
  id: number;
  warehouse: string;
  type: string;
  label: string;
  question: string;
  options: any;
  answer: string;
  analysis: string;
  level: string;
  pub: string;
  pass: number;
  coefficient: number;
  score: number;
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
  id: 0,
  warehouse: "",
  type: "",
  label: "",
  question: "",
  options: "",
  answer: "",
  analysis: "",
  level: "",
  pub: "",
  pass: 0,
  coefficient: 0,
  score: 0,
});

const paperApprovalDTO = ref({
  id: null,
  paperId: "",
  studentId: null,
  type: "",
  score: "",
  point: 0,
});

// 页面加载
onMounted(async () => {
  const result = await findByIdPaperService(paperId);
  paperApprovalDTO.value = {
    id: null,
    paperId: paperId,
    studentId: userInfo.id,
    type: result.data.data.type,
    score: result.data.data.sum,
    point: 0,
  };

  const res = await findByPaperIdPaperApprovalService(paperId);

  if (res.data.data != null) {
    paperApprovalId.value = res.data.data.id;
    paperApprovalDTO.value.id = res.data.data.id;
  }

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
  if (paperApprovalDTO.value.type == "作业") router.push("/stu-course/work");
  else router.push("/stu-course/test");
};

const saveOrUpdate = ref(true);

// 查询题集
const findByPaperApprovalIdPaperStudent = async () => {
  let result = null;
  if (paperApprovalId.value != null)
    result = await findByPaperApprovalIdPaperStudentService(
      paperApprovalId.value
    );
  else {
    result = await findByPaperIdPaperSubjectService(paperId);
  }

  if (paperApprovalId.value == null || result.data.message == "试卷为空") {
    saveOrUpdate.value = true;
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

    return true;
  } else {
    saveOrUpdate.value = false;
    optionsValue.value = [];
    if (singleList.value != undefined) {
      paperSingleAnswer.value = [];
      result.data.data["单选"].forEach((item) => {
        paperSingleAnswer.value.push({
          id: item.id,
          subjectId: item.subjectId,
          type: item.type,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "single",
        label: "单选",
      });
    }

    if (manyList.value != undefined) {
      paperManyAnswer.value = [];
      result.data.data["多选"].forEach((item) => {
        let list = item.input.split("");
        paperManyAnswer.value.push({
          id: item.id,
          subjectId: item.subjectId,
          type: item.type,
          input: list,
        });
      });
      optionsValue.value.push({
        value: "many",
        label: "多选",
      });
    }

    if (fillList.value != undefined) {
      paperFillAnswer.value = [];
      result.data.data["填空"].forEach((item) => {
        paperFillAnswer.value.push({
          id: item.id,
          subjectId: item.subjectId,
          type: item.type,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "fill",
        label: "填空",
      });
    }

    if (answerList.value != undefined) {
      paperAnswerAnswer.value = [];
      result.data.data["简答"].forEach((item) => {
        paperAnswerAnswer.value.push({
          id: item.id,
          subjectId: item.subjectId,
          type: item.type,
          input: item.input,
        });
      });
      optionsValue.value.push({
        value: "answer",
        label: "简答",
      });
    }

    return false;
  }
};

// 获取所有题目
const getSubjectList = async () => {
  const result = await findByPaperIdPaperSubjectService(paperId);

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

  let flag = await findByPaperApprovalIdPaperStudent();

  if (flag) {
    await createPaperAnswer();
  } else {
    notUpdate.value = true;
    editorRef.value.disable()
    editorRef2.value.disable()
  }
};

// 创建题集答案列表
const createPaperAnswer = () => {
  if (singleList.value != undefined) {
    singleList.value.forEach((single) => {
      paperSingleAnswer.value.push({
        paperId,
        subjectId: single.id,
        type: "单选",
        input: [],
      });
    });
  }

  if (manyList.value != undefined) {
    manyList.value.forEach((single) => {
      paperManyAnswer.value.push({
        paperId,
        subjectId: single.id,
        type: "多选",
        input: "",
      });
    });
  }

  if (fillList.value != undefined) {
    fillList.value.forEach((single) => {
      paperFillAnswer.value.push({
        paperId,
        subjectId: single.id,
        type: "填空",
        input: "",
      });
    });
  }
  if (answerList.value != undefined) {
    answerList.value.forEach((single) => {
      paperAnswerAnswer.value.push({
        paperId,
        subjectId: single.id,
        type: "简答",
        input: "",
      });
    });
  }
};

// 查询单个题目
const getSubject = async (index) => {
  if (activeName.value == "single" && singleList.value != undefined) {
    subjectVO.value = singleList.value[index];
    document.querySelectorAll(".single-tag").forEach((item: any) => {
      if (item.value == paperSingleAnswer.value[index].input) {
        item.checked = true;
      } else {
        item.checked = false;
      }
    });
  } else if (activeName.value == "many" && manyList.value != undefined) {
    subjectVO.value = manyList.value[index];
    document.querySelectorAll(".many-checkbox").forEach((item: any) => {
      item.checked = false;
      if (paperManyAnswer.value[index].input.length > 0) {
        paperManyAnswer.value[index].input.forEach((option) => {
          if (item.value == option) {
            item.checked = true;
          }
        });
      }
    });
  } else if (activeName.value == "fill" && fillList.value != undefined) {
    subjectVO.value = fillList.value[index];
    getFill(subjectVO.value.id);
  } else if (activeName.value == "answer" && answerList.value != undefined) {
    subjectVO.value = answerList.value[index];
    getAnswer(subjectVO.value.id);
  }
};

const clickSingle = (id, input) => {
  paperSingleAnswer.value.forEach((item) => {
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
  paperManyAnswer.value.forEach((item) => {
    if (item.subjectId == id) item.input = list;
  });
};

const changeFill = (id) => {
  paperFillAnswer.value.forEach((item) => {
    if (item.subjectId == id) {
      item.input = fillInput.value;
    }
  });
};

const getFill = (id) => {
  paperFillAnswer.value.forEach((item) => {
    if (item.subjectId == id) {
      fillInput.value = item.input;
    }
  });
};

const changeAnswer = (id) => {
  paperAnswerAnswer.value.forEach((item) => {
    if (item.subjectId == id) item.input = answerInput.value;
  });
};

const getAnswer = (id) => {
  answerInput.value = "";
  paperAnswerAnswer.value.forEach((item) => {
    if (item.subjectId == id) {
      answerInput.value = item.input;
    }
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

const paperAnswerAll = ref([]);

const check = () => {
  for (let i = 0; i < paperSingleAnswer.value.length; i++) {
    if (paperSingleAnswer.value[i].input == "") {
      ElMessage.error(`单选第 ${i + 1} 题未完成`);
      return true;
    }
  }
  for (let i = 0; i < paperManyAnswer.value.length; i++) {
    if (paperManyAnswer.value[i].input.length == 0) {
      ElMessage.error(`多选第 ${i + 1} 题未完成`);
      return true;
    }
  }
  for (let i = 0; i < paperFillAnswer.value.length; i++) {
    if (paperFillAnswer.value[i].input == "") {
      ElMessage.error(`填空第 ${i + 1} 题未完成`);
      return true;
    }
  }
  for (let i = 0; i < paperAnswerAnswer.value.length; i++) {
    if (paperAnswerAnswer.value[i].input == "") {
      ElMessage.error(`简答第 ${i + 1} 题未完成`);
      return true;
    }
  }
  return false;
};

const savePaperApproval = async () => {
  await savePaperApprovalService(paperApprovalDTO.value);
};

// 提交试卷
const submit = async () => {
  if (check()) {
    return;
  }

  if (paperApprovalId.value == null) {
    await savePaperApproval();
    const result = await findByPaperIdPaperApprovalService(paperId);
    paperApprovalId.value = result.data.data.id;
    paperApprovalDTO.value.id = paperApprovalId.value;
  }

  paperSingleAnswer.value.forEach((item) => {
    paperAnswerAll.value.push({
      ...item,
      point: 0,
      paperId,
      paperApprovalId: paperApprovalId.value,
    });
  });
  paperManyAnswer.value.forEach((item) => {
    item.input = item.input.join("");
    paperAnswerAll.value.push({
      ...item,
      point: 0,
      paperId,
      paperApprovalId: paperApprovalId.value,
    });
  });
  paperFillAnswer.value.forEach((item) => {
    paperAnswerAll.value.push({
      ...item,
      point: 0,
      paperId,
      paperApprovalId: paperApprovalId.value,
    });
  });
  paperAnswerAnswer.value.forEach((item) => {
    paperAnswerAll.value.push({
      ...item,
      point: 0,
      paperId,
      paperApprovalId: paperApprovalId.value,
    });
  });

  await saveOrUpdatePaperStudentService(paperAnswerAll.value);
  try {
    await savePaperApproval();
  } catch (error) {
    if(error.message == '该试卷已批阅，无法修改') {
      router.back()
      return
    } 
  }
  ElMessage.success("提交成功");

  paperAnswerAll.value = [];
  createPaperAnswer();
  usePaperStore().removePaper();
   router.back()
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
                            class="check-box checkbox-wrapper nav-label-single"
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
                            class="check-box checkbox-wrapper nav-label-single"
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
                            class="check-box checkbox-wrapper nav-label-single"
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
                        <button class="save" @click="submit">
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
                            {{ subjectVO.score }}
                            <span style="font-size: 14px">分</span>
                          </div>
                        </div>
                        <el-divider
                          style="border-top: 1px dashed #409eff"
                          border-style="dashed"
                        />

                        <div class="single-option single-answer">
                          <div class="answer" >
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
                                    :disabled="notUpdate"
                                    @click="
                                      clickSingle(subjectVO.id, option.option)
                                    "
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
                                  :disabled="notUpdate"
                                  @click="clickMany(subjectVO.id)"
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
                                  v-model="fillInput"
                                  :disabled="notUpdate"
                                  @input="changeFill(subjectVO.id)"
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
                              @onChange="changeAnswer(subjectVO.id)"
                              v-model="answerInput"
                              @onCreated="handleCreated"
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

.check-box-active {
  border: 1px solid #606266 !important;
  transition: all 0.3s;
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
