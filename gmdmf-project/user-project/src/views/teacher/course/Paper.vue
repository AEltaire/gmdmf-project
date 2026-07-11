<script lang="ts" setup>
import { ref, onMounted, nextTick, onBeforeMount } from "vue";
import { findByWarehouseIdSubjectService } from "@/api/Subject";
import { saveStudentPaperApprovalService } from "@/api/PaperApproval";
import { findByTeacherIdWarehouseService } from "@/api/Warehouse";
import { CloseBold, House } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { usePaperStore } from "@/stores/paper";
// editor
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { shallowRef } from "vue";
import { Editor } from "@wangeditor/editor-for-vue";
import {
  savePaperSubjectService,
  updatePaperSubjectService,
  findByPaperIdPaperSubjectService,
} from "@/api/PaperSubject";

import $ from "jquery";
import { useCourseStore } from "@/stores/course";
import { ElLoading } from "element-plus";

// 变量
const router = useRouter();
const paperId = usePaperStore().paper;
const total = ref(0);
const maxTotal = ref(80);
const typeValue = ref("单选");
const typeValueList = ref(["单选", "多选", "填空", "简答"]);
// false: add | true: update
const flag = ref(true);
const courseStore = useCourseStore();
const addOrUpdate = ref(false);
// 分数
const singleScore = ref(0);
const manyScore = ref(0);
const fillScore = ref(0);
const answerScore = ref(0);

// 富文本编辑器
const mode = ref("simple");
const editorRefFill = shallowRef();
const editorRefAnswer = shallowRef();
const editorConfig = ref({ placeholder: "请选择要查看的题目..." });

const checkedSubjectList = ref([]);

const countSubjectLevelList = ref({
  easy: 0,
  medium: 0,
  difficult: 0,
  average: 0,
});

const handleCreatedFill = (editor) => {
  editorRefFill.value = editor;
  editor.disable();
};
const handleCreatedAnswer = (editor) => {
  editorRefAnswer.value = editor;
  editor.disable();
};

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

const countSubjectNumberList = ref({
  single: 0,
  many: 0,
  fill: 0,
  answer: 0,
  sum: 0,
});

const activeWarehouseId = ref(null);
const warehouseList = ref([]);

// 单选
const singleList = ref<Subject[]>([]);
const manyList = ref<Subject[]>([]);
const fillList = ref<Subject[]>([]);
const answerList = ref<Subject[]>([]);
const subjectVO = ref<Subject>();

// 进度条颜色
const colors = [
  { color: "#F56C6C", percentage: 30 },
  { color: "#E6A23C", percentage: 40 },
  { color: "#409EFF", percentage: 70 },
  { color: "#67C23A", percentage: 100 },
];

// 页面加载事件 --------------------------------
const loading = ref(true);
onMounted(async () => {
  const loadingInstance = ElLoading.service({ fullscreen: true });
  await getWarehouseList();
  await findAllWork();
  nextTick(() => {
    setTimeout(() => {
      loadingInstance.close();
      loading.value = false;
    }, 1000);
  });
});

// 返回课程端
const backCourse = () => {
  usePaperStore().removePaper();
  router.back();
};

// 提交试题集
const submitPaper = async () => {
  const submitSubjectList = [];
  checkedSubjectList.value.forEach((item: any) => {
    submitSubjectList.push({
      paperId: paperId,
      subjectId: item.id,
      score: item.score,
    });
  });

  if (submitSubjectList.length == 0) {
    ElMessage.warning("请先选择题目");
    return;
  }

  if (addOrUpdate.value == false) {
    await savePaperSubjectService(submitSubjectList);
    // 创建试卷
    await saveStudentPaperApprovalService(courseStore.course.id, paperId);
    ElMessage.success("提交成功，初始化学生试题卷完成");
  } else {
    await updatePaperSubjectService(paperId, submitSubjectList);
    ElMessage.success("修改成功");
  }

  router.back();
};

// 查询题库
const findAllWork = async () => {
  const result = await findByPaperIdPaperSubjectService(paperId);

  if (result.data != undefined) {
    if (result.data.data["单选"] != undefined) {
      result.data.data["单选"].forEach((item: any) => {
        checkedSubjectList.value.push(item);
      });
    }
    if (result.data.data["多选"] != undefined) {
      result.data.data["多选"].forEach((item: any) => {
        checkedSubjectList.value.push(item);
      });
    }
    if (result.data.data["填空"] != undefined) {
      result.data.data["填空"].forEach((item: any) => {
        checkedSubjectList.value.push(item);
      });
    }
    if (result.data.data["简答"] != undefined) {
      result.data.data["简答"].forEach((item: any) => {
        checkedSubjectList.value.push(item);
      });
    }
    await echoCheckedSubject();
    total.value = checkedSubjectList.value.length;
  }

  if (total.value != 0) {
    addOrUpdate.value = true;
  } else {
    addOrUpdate.value = false;
  }
};

// 题库代码 ------------------------------------

// 获取所有题库
const getWarehouseList = async () => {
  const result = await findByTeacherIdWarehouseService();
  warehouseList.value = result.data.data;
  activeWarehouseId.value == null
    ? (activeWarehouseId.value = warehouseList.value[0].id)
    : "";
  await getSubjectListFindByWid();
  await selectSubject(0);
  await getSubject(0);
};

// 切换题库
const handleClick = async (id) => {
  activeWarehouseId.value = id;
  await countSubjectNumber();
  await cleanCheckedSubjectBUG();
  await getWarehouseList();
  await changeSubjectListType();
  await echoCheckedSubject();
};

// 清除选中 [BUG]
const cleanCheckedSubjectBUG = () => {
  document
    .querySelectorAll('input[type="checkbox"]:checked')
    .forEach((item: any) => {
      item.checked = false;
    });
};

// 题目代码 ------------------------------------

// 获取题库题目
const getSubjectListFindByWid = async () => {
  const result = await findByWarehouseIdSubjectService(activeWarehouseId.value);
  singleList.value = result.data.data["单选"];
  manyList.value = result.data.data["多选"];
  fillList.value = result.data.data["填空"];
  answerList.value = result.data.data["简答"];
};

// 切换题目类型
const changeSubjectListType = async () => {
  await cleanSubjectVO();

  if (typeValue.value == "单选" && singleList.value != undefined) {
    subjectVO.value = singleList.value[0];
    try {
      subjectVO.value.options = JSON.parse(singleList.value[0].options);
    } catch (e) {
      subjectVO.value.options = singleList.value[0].options;
    }
  } else if (typeValue.value == "多选" && manyList.value != undefined) {
    subjectVO.value = manyList.value[0];
    try {
      subjectVO.value.options = JSON.parse(manyList.value[0].options);
    } catch (e) {
      subjectVO.value.options = manyList.value[0].options;
    }
  } else if (typeValue.value == "填空" && fillList.value != undefined) {
    subjectVO.value = fillList.value[0];
  } else if (typeValue.value == "简答" && answerList.value != undefined) {
    subjectVO.value = answerList.value[0];
  }
  // 清除数据
  selectSubject(0);
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
  } else if (typeValue.value == "多选" && manyList.value != undefined) {
    subjectVO.value = manyList.value[index];
    try {
      subjectVO.value.options = JSON.parse(manyList.value[index].options);
    } catch (e) {
      subjectVO.value.options = manyList.value[index].options;
    }
  } else if (typeValue.value == "填空" && fillList.value != undefined) {
    subjectVO.value = fillList.value[index];
  } else if (typeValue.value == "简答" && answerList.value != undefined) {
    subjectVO.value = answerList.value[index];
  }
  await selectSubject(index);
};

// 清除数据
const cleanSubjectVO = () => {
  subjectVO.value = undefined;
};

// 统计代码 ------------------------------------

// 统计题目信息
const clickCountSubject = async () => {
  await countSubjectNumber();
  if (checkedSubjectList.value.length == 0) {
    ElMessage({
      message: "请先选择题目",
      type: "warning",
    });
    return true;
  }
  await countSubjectLevel();
  flag.value = false;
};

// 收集选中题目
const countSubjectNumber = () => {
  // 统计各个题型的题目数量
  const singleCheckbox = document.querySelectorAll(
    ".single-checkbox input[type='checkbox']:checked"
  );

  singleCheckbox.forEach((item) => {
    singleList.value.forEach((subject) => {
      if (subject.id == item.id) {
        checkedSubjectList.value.push({ ...subject, score: 2 });
      }
    });
  });

  const manyCheckbox = document.querySelectorAll(
    ".many-checkbox  input[type='checkbox']:checked"
  );

  manyCheckbox.forEach((item) => {
    manyList.value.forEach((subject) => {
      if (subject.id == item.id) {
        checkedSubjectList.value.push({ ...subject, score: 2 });
      }
    });
  });

  const fillCheckbox = document.querySelectorAll(
    ".fill-checkbox  input[type='checkbox']:checked"
  );

  fillCheckbox.forEach((item) => {
    fillList.value.forEach((subject) => {
      if (subject.id == item.id) {
        checkedSubjectList.value.push({ ...subject, score: 5 });
      }
    });
  });

  const answerCheckbox = document.querySelectorAll(
    ".answer-checkbox  input[type='checkbox']:checked"
  );

  answerCheckbox.forEach((item) => {
    answerList.value.forEach((subject) => {
      if (subject.id == item.id) {
        checkedSubjectList.value.push({ ...subject, score: 10 });
      }
    });
  });

  // 去除 checkedSubjectList 中的重复题目
  const uniqueSubjectList = [];
  checkedSubjectList.value.forEach((subject) => {
    if (
      !uniqueSubjectList.some((item) => item.id === subject.id) &&
      subject.id !== undefined
    ) {
      uniqueSubjectList.push(subject);
    }
  });
  checkedSubjectList.value = uniqueSubjectList;

  countSubjectNumberList.value = {
    single: 0,
    many: 0,
    fill: 0,
    answer: 0,
    sum: 0,
  };

  // 收集题目类型
  checkedSubjectList.value.forEach((subject) => {
    if (subject.type == "单选") {
      countSubjectNumberList.value.single++;
    } else if (subject.type == "多选") {
      countSubjectNumberList.value.many++;
    } else if (subject.type == "填空") {
      countSubjectNumberList.value.fill++;
    } else if (subject.type == "简答") {
      countSubjectNumberList.value.answer++;
    }
  });

  // 统计总数
  countSubjectNumberList.value.sum =
    countSubjectNumberList.value.single +
    countSubjectNumberList.value.many +
    countSubjectNumberList.value.fill +
    countSubjectNumberList.value.answer;
};

// 收集题目难度
const countSubjectLevel = () => {
  countSubjectLevelList.value = {
    easy: 0,
    medium: 0,
    difficult: 0,
    average: 0,
  };

  let sumScore = 0.0;
  checkedSubjectList.value.forEach((subject) => {
    if (subject.level == "简单题") {
      countSubjectLevelList.value.easy++;
    } else if (subject.level == "中等题") {
      countSubjectLevelList.value.medium++;
    } else if (subject.level == "重难题") {
      countSubjectLevelList.value.difficult++;
    }
    sumScore += subject.coefficient;
  });

  countSubjectLevelList.value.average =
    Math.floor((sumScore / countSubjectNumberList.value.sum) * 10000) / 100;

  countSubjectLevelList.value.easy =
    Math.floor(
      (countSubjectLevelList.value.easy / countSubjectNumberList.value.sum) *
        10000
    ) / 100;
  countSubjectLevelList.value.medium =
    Math.floor(
      (countSubjectLevelList.value.medium / countSubjectNumberList.value.sum) *
        10000
    ) / 100;
  countSubjectLevelList.value.difficult =
    Math.floor(
      (countSubjectLevelList.value.difficult /
        countSubjectNumberList.value.sum) *
        10000
    ) / 100;
};

// 统计页面返回
const changeFlag = async () => {
  flag.value = !flag.value;
  nextTick(() => {
    echoCheckedSubject();
    selectSubject(0);
  });
  // 等待执行完，再执行下一步
};

// 删除选中题目
const deleteCheckedSubject = (row) => {
  // 直接过滤覆盖，快速删除
  checkedSubjectList.value = checkedSubjectList.value.filter(
    (subject) => subject.id !== row.id
  );
  clickCountSubject();
};

// 修改分数
const changeCheckedSubjectScore = (row) => {
  checkedSubjectList.value.forEach((subject) => {
    if (subject.id == row.id) {
      subject.score = row.score;
    }
  });
};

// 分数设置应用
const applyScore = () => {
  checkedSubjectList.value.forEach((subject) => {
    if (subject.type == "单选") {
      subject.score = singleScore.value;
    } else if (subject.type == "多选") {
      subject.score = manyScore.value;
    } else if (subject.type == "填空") {
      subject.score = fillScore.value;
    } else if (subject.type == "简答") {
      subject.score = answerScore.value;
    }
  });
  singleScore.value = 1;
  manyScore.value = 1;
  fillScore.value = 1;
  answerScore.value = 5;
};

// 筛选题目类型
const filterTag = (value, row) => {
  return row.type === value;
};

// 检测元素是否加载完成
const isElementLoaded = async (selector) => {
  // 通过 await 等待，如果是 null 就返回失败结果
  while ($(`#${selector}`) === null) {
    await new Promise((resolve) => requestAnimationFrame(resolve));
  }
  return $(`#${selector}`);
};

// 回显选中题目
const echoCheckedSubject = async () => {
  if (
    checkedSubjectList.value == undefined ||
    checkedSubjectList.value.length == 0
  ) {
    return;
  }

  checkedSubjectList.value.forEach(async (subject) => {
    try {
      const temp: any = document.getElementById(subject.id);
      temp.checked = true;
    } catch (error) {
      null;
    }
  });
};

// 选中题目
const changeInput = (e) => {
  if (e.target.checked == false) {
    checkedSubjectList.value = checkedSubjectList.value.filter((subject) => {
      return e.target.id !== subject.id;
    });
    total.value -= 1;
  } else {
    if (total.value >= maxTotal.value) {
      e.target.checked = false;
      ElMessage.warning("题目数量已达到上限");
      return;
    }
    total.value += 1;
  }
};
</script>
<template>
  <!-- 题库页面 -->
  <el-row v-show="loading == false" :gutter="24" style="width: 100%">
    <el-col :span="1"><div class="grid-content ep-bg-purple" /></el-col>
    <el-col :span="22">
      <el-container v-if="flag">
        <el-main>
          <el-card style="width: 99%" shadow="never">
            <div class="common-layout" style="">
              <el-container>
                <el-header>
                  <el-tabs
                    v-model="activeWarehouseId"
                    class="demo-tabs"
                    @tab-change="handleClick(activeWarehouseId)"
                  >
                    <el-tab-pane
                      :label="warehouse.name"
                      :name="warehouse.id"
                      v-for="(warehouse, index) in warehouseList"
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
                        <span class="header-select">
                          <el-select
                            v-model="typeValue"
                            placeholder="类型"
                            style="width: 100px"
                            @change="changeSubjectListType"
                          >
                            <el-option
                              v-for="item in typeValueList"
                              :key="item"
                              :label="item"
                              :value="item"
                            />
                          </el-select>
                        </span>
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
                            @click="getSubject(index)"
                            :key="index"
                          >
                            <input
                              :id="item.id"
                              type="checkbox"
                              class="checkSubject"
                              @change="changeInput"
                            />
                            <svg viewBox="0 0 35.6 35.6">
                              <circle
                                class="background"
                                cx="17.8"
                                cy="17.8"
                                r="17.8"
                              ></circle>
                              <circle
                                class="stroke"
                                cx="17.8"
                                cy="17.8"
                                r="14.37"
                              ></circle>
                              <polyline
                                class="check"
                                points="11.78 18.12 15.55 22.23 25.17 12.87"
                              ></polyline>
                            </svg>
                            <span class="check-box-text">{{ item.label }}</span>
                          </div>
                          <!-- 多选列表 -->
                          <div
                            v-show="typeValue == '多选'"
                            class="check-box checkbox-wrapper many-checkbox"
                            v-for="(item, index) in manyList"
                            @click="getSubject(index)"
                            :key="index"
                          >
                            <input
                              :id="item.id"
                              type="checkbox"
                              class="checkSubject"
                              @change="changeInput"
                            />
                            <svg viewBox="0 0 35.6 35.6">
                              <circle
                                class="background"
                                cx="17.8"
                                cy="17.8"
                                r="17.8"
                              ></circle>
                              <circle
                                class="stroke"
                                cx="17.8"
                                cy="17.8"
                                r="14.37"
                              ></circle>
                              <polyline
                                class="check"
                                points="11.78 18.12 15.55 22.23 25.17 12.87"
                              ></polyline>
                            </svg>
                            <span class="check-box-text">{{ item.label }}</span>
                          </div>
                          <!-- 填空列表 -->
                          <div
                            v-show="typeValue == '填空'"
                            class="check-box checkbox-wrapper fill-checkbox"
                            v-for="(item, index) in fillList"
                            @click="getSubject(index)"
                            :key="index"
                          >
                            <input
                              :id="item.id"
                              type="checkbox"
                              class="checkSubject"
                              @change="changeInput"
                            />
                            <svg viewBox="0 0 35.6 35.6">
                              <circle
                                class="background"
                                cx="17.8"
                                cy="17.8"
                                r="17.8"
                              ></circle>
                              <circle
                                class="stroke"
                                cx="17.8"
                                cy="17.8"
                                r="14.37"
                              ></circle>
                              <polyline
                                class="check"
                                points="11.78 18.12 15.55 22.23 25.17 12.87"
                              ></polyline>
                            </svg>
                            <span class="check-box-text">{{ item.label }}</span>
                          </div>
                          <!-- 简答列表 -->
                          <div
                            v-show="typeValue == '简答'"
                            class="check-box checkbox-wrapper answer-checkbox"
                            v-for="(item, index) in answerList"
                            @click="getSubject(index)"
                            :key="index"
                          >
                            <input
                              :id="item.id"
                              type="checkbox"
                              class="checkSubject"
                              @change="changeInput"
                            />
                            <svg viewBox="0 0 35.6 35.6">
                              <circle
                                class="background"
                                cx="17.8"
                                cy="17.8"
                                r="17.8"
                              ></circle>
                              <circle
                                class="stroke"
                                cx="17.8"
                                cy="17.8"
                                r="14.37"
                              ></circle>
                              <polyline
                                class="check"
                                points="11.78 18.12 15.55 22.23 25.17 12.87"
                              ></polyline>
                            </svg>
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
                        <span class="submit" @click="clickCountSubject"
                          ><button>
                            <el-icon
                              size="22"
                              style="color: white; margin-top: 4px"
                              ><DocumentCopy
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
                        <el-divider border-style="dashed" />
                        <div
                          v-if="typeValue == '单选' || typeValue == '多选'"
                          class="single-question"
                        >
                          {{
                            subjectVO != undefined ? subjectVO.question : "问题"
                          }}
                        </div>
                        <div
                          v-show="typeValue == '单选' || typeValue == '多选'"
                        >
                          <div
                            class="single-view"
                            v-for="(index, i) in 4"
                            :key="i"
                          >
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
            </div>
          </el-card></el-main
        >
        <el-footer></el-footer>
      </el-container>
      <el-container v-else>
        <el-main class="count-box">
          <el-row>
            <!-- 题目表格 -->
            <el-col :span="16"
              ><el-card style="width: 99%; height: 680px" shadow="never">
                <el-table
                  :data="checkedSubjectList"
                  height="640"
                  style="width: 100%"
                >
                  <el-table-column align="center" type="index" />
                  <el-table-column
                    align="center"
                    prop="label"
                    label="题目名"
                    width="280"
                  />
                  <el-table-column
                    prop="tag"
                    label="类型"
                    width="100"
                    :filters="[
                      { text: '单选', value: '单选' },
                      { text: '多选', value: '多选' },
                      { text: '填空', value: '填空' },
                      { text: '简答', value: '简答' },
                    ]"
                    :filter-method="filterTag"
                    filter-placement="bottom-end"
                  >
                    <template #default="scope">
                      <el-tag
                        :type="
                          scope.row.type === '单选'
                            ? 'success'
                            : scope.row.type === '多选'
                            ? 'primary'
                            : scope.row.type === '填空'
                            ? 'warning'
                            : 'danger'
                        "
                        disable-transitions
                        >{{ scope.row.type }}</el-tag
                      >
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="level"
                    label="难度"
                    width="130"
                  />
                  <el-table-column
                    align="center"
                    prop="score"
                    label="分值"
                    width="130"
                  >
                    <template #default="scope">
                      <el-input-number
                        v-model="scope.row.score"
                        style="width: 100px"
                        :min="1"
                        :max="100"
                        step="0.5"
                        step-strictly="true"
                        precision="1"
                        size="small"
                        @change="changeCheckedSubjectScore(scope.row)"
                      >
                        <template #decrease-icon>
                          <el-icon><ArrowLeft /></el-icon>
                        </template>
                        <template #increase-icon>
                          <el-icon><ArrowRight /></el-icon>
                        </template>
                      </el-input-number>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="操作">
                    <template #default="scope">
                      <el-button
                        size="small"
                        type="danger"
                        :icon="CloseBold"
                        circle
                        text
                        @click="deleteCheckedSubject(scope.row)"
                      />
                    </template>
                  </el-table-column>
                </el-table> </el-card
            ></el-col>
            <!-- 题目情况阅览 -->
            <el-col :span="8"
              ><el-card
                style="
                  width: 99%;
                  height: 250px;
                  min-width: 360px;
                  margin-bottom: 8px;
                "
                shadow="never"
              >
                <el-row style="margin-bottom: 20px">
                  <el-col :span="6">
                    <el-statistic
                      title="单选题"
                      :value="countSubjectNumberList['single']"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-statistic
                      title="多选题"
                      :value="countSubjectNumberList['many']"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-statistic
                      title="填空题"
                      :value="countSubjectNumberList['fill']"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-statistic
                      title="简答题"
                      :value="countSubjectNumberList['answer']"
                    />
                  </el-col>
                </el-row>
                <el-row style="margin-bottom: 30px">
                  <el-col :span="10">
                    <el-statistic title="试卷难度" value="">
                      <template #suffix>
                        <el-progress
                          type="dashboard"
                          :percentage="countSubjectLevelList['average']"
                          :color="colors"
                          :format="() => `${countSubjectLevelList['average']}`"
                        />
                      </template>
                    </el-statistic>
                  </el-col>
                  <el-col :span="14">
                    <el-statistic title="难度占比" value="">
                      <template #suffix>
                        <div class="demo-progress">
                          <div
                            style="
                              font-size: 12px;
                              color: #b1b3b8;
                              margin-bottom: 15px;
                            "
                          >
                            简单题、中等题和重难题的占比
                          </div>
                          <el-progress
                            style="
                              min-width: 240px;
                              max-width: 230px;
                              width: 100%;
                            "
                            :percentage="countSubjectLevelList['easy']"
                            color="#409EFF"
                          />
                          <el-progress
                            :percentage="countSubjectLevelList['medium']"
                            color="#E6A23C"
                          />
                          <el-progress
                            :percentage="countSubjectLevelList['difficult']"
                            color="#F56C6C"
                          />
                        </div>
                      </template>
                    </el-statistic>
                  </el-col>
                </el-row>
              </el-card>
              <!-- 分值控制工具 -->
              <el-card
                style="
                  width: 99%;
                  height: 420px;
                  min-width: 360px;
                  position: relative;
                "
                shadow="never"
              >
                <span class="score-svg">快捷设置</span>
                <el-row style="margin-bottom: 15px">
                  <el-col :span="24">
                    <el-statistic
                      :value="
                        countSubjectNumberList['single'] * singleScore +
                        countSubjectNumberList['many'] * manyScore +
                        countSubjectNumberList['fill'] * fillScore +
                        countSubjectNumberList['answer'] * answerScore
                      "
                    >
                      <template #title>
                        <div style="display: inline-flex; align-items: center">
                          试卷总分 (当前总分 / 默认值)
                        </div>
                      </template>
                      <template #suffix>/ 100</template>
                    </el-statistic>
                  </el-col>
                </el-row>
                <el-row style="margin-bottom: 20px">
                  <el-col :span="6">
                    <el-statistic
                      title="单选题"
                      :value="countSubjectNumberList['single'] * singleScore"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-statistic
                      title="多选题"
                      :value="countSubjectNumberList['many'] * manyScore"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-statistic
                      title="填空题"
                      :value="countSubjectNumberList['fill'] * fillScore"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-statistic
                      title="简答题"
                      :value="countSubjectNumberList['answer'] * answerScore"
                    />
                  </el-col>
                </el-row>

                <div class="slider-demo-block">
                  <span class="demonstration">Single score</span>
                  <el-slider
                    :min="1"
                    :max="10"
                    v-model="singleScore"
                    :step="1"
                    show-stops
                  />
                </div>
                <div class="slider-demo-block">
                  <span class="demonstration">Many score</span>
                  <el-slider
                    :min="1"
                    :max="10"
                    v-model="manyScore"
                    :step="1"
                    show-stops
                  />
                </div>
                <div class="slider-demo-block">
                  <span class="demonstration">Fill score</span>
                  <el-slider
                    :min="1"
                    :max="10"
                    v-model="fillScore"
                    :step="1"
                    show-stops
                  />
                </div>
                <div class="slider-demo-block">
                  <span class="demonstration">Answer score</span>
                  <el-slider
                    :min="5"
                    :max="50"
                    v-model="answerScore"
                    :step="5"
                    show-stops
                  />
                </div>
                <div style="margin-top: 30px">
                  <el-button round circle @click="changeFlag"
                    ><el-icon><ArrowLeftBold /></el-icon
                  ></el-button>
                  <el-button
                    type="primary"
                    round
                    style="width: 80px; font-size: 13.6px"
                    @click="applyScore"
                    >应用</el-button
                  >
                  <el-button
                    type="success"
                    round
                    style="width: 100px; float: right; font-size: 13.6px"
                    @click="submitPaper"
                    >发布题集</el-button
                  >
                </div>
              </el-card></el-col
            >
          </el-row>
        </el-main>
      </el-container>
    </el-col>
    <el-col :span="1"> </el-col>
  </el-row>
</template>
<style lang="scss" scoped>
.count-box {
  // 难度分布进度条
  .demo-progress .el-progress--line {
    margin-bottom: 15px;
    max-width: 600px;
  }
  .demo-progress .el-progress--circle {
    margin-right: 15px;
  }

  .demo-progress .el-progress--line {
    margin-bottom: 15px;
    max-width: 600px;
  }

  // 分段控制
  .slider-demo-block {
    max-width: 600px;
    display: flex;
    align-items: center;
  }
  .slider-demo-block .el-slider {
    margin-top: 0;
    margin-left: 12px;
  }
  .slider-demo-block .demonstration {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    line-height: 44px;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 0;
  }
  .slider-demo-block .demonstration + .el-slider {
    flex: 0 0 70%;
  }

  .score-svg {
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
}

// --------------------------

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
    // background-color: red;
    box-sizing: border-box;
    padding-top: 5px;

    .header-select {
      float: left;
      margin-left: 10px;
      width: 50%;
      height: 100%;
      display: flex;
      align-items: center;
      box-sizing: border-box;
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
      transition: ease all 0.4s;
      -webkit-transition: ease all 0.4s;
    }

    .checkbox-wrapper .stroke {
      fill: none;
      stroke: #fff;
      stroke-miterlimit: 10;
      stroke-width: 2px;
      stroke-dashoffset: 100;
      stroke-dasharray: 100;
      transition: ease all 0.4s;
      -webkit-transition: ease all 0.4s;
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
      transition: ease all 0.4s;
      -webkit-transition: ease all 0.4s;
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
