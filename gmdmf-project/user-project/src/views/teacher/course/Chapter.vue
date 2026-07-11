<script lang="ts" setup>
import $ from "jquery";
import { nextTick, onMounted, ref, watch, onBeforeUnmount } from "vue";
import {
  DocumentAdd,
  DocumentChecked,
  DocumentDelete,
  Refresh,
  HomeFilled,
  UploadFilled,
  List,
  CloseBold,
  Select,
  SemiSelect,
} from "@element-plus/icons-vue";

import { useRouter } from "vue-router";
import {
  findByIdChapterService,
  saveChapterService,
  updateChapterService,
  deleteChapterService,
} from "@/api/Chapter";
import {
  saveNodeService,
  updateNodeByIdService,
  deleteNodeByIdService,
} from "@/api/Node";
import { useCourseStore } from "@/stores/course";
import { ElLoading, ElMessage, ElMessageBox, genFileId } from "element-plus";
import { _throttle } from "@/utils/throttle";
import { ElNotification } from "element-plus";
import type { CheckboxValueType, UploadInstance } from "element-plus";
import {
  findByIdCourseService,
  saveCourseService,
  approvalCourseService,
  updateCourseService,
} from "@/api/Course";
import { findAllMajorService } from "@/api/Major";
import { findByTeacherIdTeamService, findByCourseIdService } from "@/api/Team";
import { useNodeStore } from "@/stores/node";

const isNewCourse = ref(false);
const uploadRef = ref<UploadInstance>();
const token = localStorage.getItem("token");
const courseStore = useCourseStore();
const checkAll = ref(false);
const indeterminate = ref(false);
const checkedTeamList = ref<CheckboxValueType[]>([]);
const URL = ref(null);
const router = useRouter();
const nodeStore = useNodeStore();
const isChapter = ref(true);
const model = ref(true);
const activeName = ref(null);
const oldActiveName = ref(0);
const oldIndex = ref(null);
const info = ref("工具栏: 返回 | 课程信息 | 发布课程");
const upload = ref<UploadInstance>();
const fileList = ref([]);
const isAdd = ref(false);
const isCourseInfo = ref(false);
const time = ref<[Date, Date]>([new Date(), new Date()]);
const file = ref([]);
const coursePublic = ref("私有");
interface Course {
  id: number;
  name: string;
  majorId: string;
  startTime: string;
  endTime: string;
  point: number;
  introduce: string;
  picture: string;
  pub: string;
  teamIds: CheckboxValueType[];
}

// 课程信息表单
const courseDTO = ref<Course>({
  id: null,
  name: "",
  majorId: "",
  startTime: "",
  endTime: "",
  point: 1,
  introduce: "",
  picture: "",
  pub: "0",
  teamIds: [],
});

// 课程列表
const courseList = ref([
  {
    id: 0,
    name: "",
    majorName: "",
    teacherName: "",
    startTime: "",
    endTime: "",
    point: 0,
    introduce: "",
    picture: "",
    number: 0,
    assess: 0,
    love: 0,
    pub: "",
  },
]);

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

// 章节表单
const chapterDTO = ref({
  id: null,
  title: "",
  courseId: null,
});

interface node {
  id: number;
  chapterId: number;
  title: string;
  introduce: string;
  content: string;
}

// 节点
const nodeVO = ref<node>({
  id: null,
  chapterId: null,
  title: "",
  introduce: "",
  content: "",
});
const nodeDTO = ref<node>();

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

const findByIdCourse = async (id) => {
  const result = await findByIdCourseService(id);
  courseDTO.value = {
    id: result.data.data.id,
    name: result.data.data.name,
    majorId: result.data.data.majorId,
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
  coursePublic.value = result.data.data.pub;
};

const findByCourseIdTeam = async (id) => {
  const result = await findByCourseIdService(id);
  checkedTeamList.value = result.data.data.map((item) => item.id);
  courseDTO.value.teamIds = checkedTeamList.value;
};

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
      oldActiveName.value = nodeStore.node.index + 1;
      handleClick(nodeStore.node.node, nodeStore.node.index, nodeStore.node.i);
      isChapter.value = false;
    } else {
      activeName.value = 1;
      oldActiveName.value = 1;
      handleClick(chapterList.value[0].nodeVOS[0], 0, 0);
      isChapter.value = false;
    }
  } else {
    isNewCourse.value = true;
    isCourseInfo.value = false;
    isAdd.value = true;
    isChapter.value = true;
  }
  await findAllMajor();
  if (isCourseInfo.value == false) {
    showCoursePaper();
  }
  await findByTeacherIdTeam();
});

// 模式 ------------------------------------------------
const open = () => {
  if (model.value) {
    ElNotification({
      title: "阅览模式",
      duration: 1200,
    });
  } else {
    ElNotification({
      title: "编辑模式",
      duration: 1200,
    });
  }
};

// 模式切换
const refresh = () => {
  model.value = !model.value;
  open();
  if (model.value) {
    info.value = "工具栏: 返回 | 课程信息 | 发布课程";
  } else {
    info.value = "工具栏: 添加 | 删除 | 保存";
  }
  if (!isAdd.value) {
    chapterDTO.value.title = chapterList.value[oldActiveName.value - 1].title;
  }
};

// 按钮控制 -----------------------------------------------

// 返回首页
const backHome = () => {
  if (!isCourseInfo) {
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
    setTimeout(async function () {
      router.push("/tea-course");
    }, 1000);
  } else {
    router.push("/tea-course");
  }
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
        bottom: "-40px",
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

  setTimeout(async function () {
    isCourseInfo.value = !isCourseInfo.value;
  }, 1000);
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
  if (chapterList.value.length == 0) {
    isAdd.value = true;
    isChapter.value = true;
    model.value = false;
  }
};

// 查看节点
const handleClick = async (node, index, i) => {
  try {
    URL.value = node.content;
    nodeVO.value = node;
    oldIndex.value = i; // 保存节点下标
    // 改变节点颜色
    await changeColor(index, i);
    isAdd.value = false;
    chapterDTO.value.id = chapterList.value[activeName.value - 1].id;
  } catch (e) {
    null;
  }
};

// 改变节点颜色
const changeColor = (index, i) => {
  try {
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
  } catch (e) {
    null;
  }
};

// 切换章节和节点
const handleChange = (val) => {
  cleanDTO();
  activeName.value = val;

  if (activeName.value != "") {
    chapterDTO.value.id = chapterList.value[activeName.value - 1].id;
    chapterDTO.value.title = chapterList.value[activeName.value - 1].title;
    isChapter.value = false;
    isAdd.value = true;
    oldActiveName.value = activeName.value;
  } else {
    if (oldActiveName.value != 0) {
      chapterDTO.value.id = chapterList.value[oldActiveName.value - 1].id;
      chapterDTO.value.title = chapterList.value[oldActiveName.value - 1].title;
      isChapter.value = true;
      isAdd.value = false;
    }

    if (activeName.value == "" && isChapter.value == false) {
      isChapter.value = true;
    }

    if (oldActiveName.value == 0 && activeName.value == "") {
      isAdd.value = true;
    }
  }
};

// 增删改查 --------------------------------

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

const checkDTO = () => {
  if (!isChapter.value) {
    if (nodeVO.value.title == "" || nodeVO.value.title == null) {
      ElMessage({
        message: "请输入节点标题",
        type: "warning",
      });
      return true;
    }
    if (nodeVO.value.introduce == "" || nodeVO.value.introduce == null) {
      ElMessage({
        message: "请输入节点简介",
        type: "warning",
      });
      return true;
    }
    if (
      fileList.value.length == 0 &&
      (nodeVO.value.content == "" || nodeVO.value.content == null)
    ) {
      ElMessage.warning("请输入上传视频");
      return true;
    }
    if (
      (nodeVO.value.content == "" || nodeVO.value.content == null) &&
      fileList.value[0].raw.type != "video/mp4"
    ) {
      ElMessage.warning("请上传一个 MP4 格式的文件，大小不超过 200 MB");
      fileList.value = [];
      return;
    }
  } else {
    if (chapterDTO.value.title == "" || chapterDTO.value.title == null) {
      ElMessage({
        message: "请输入章节标题",
        type: "warning",
      });
      return true;
    }
  }
  return false;
};

// 添加
const clickAdd = () => {
  cleanDTO();
  if (activeName.value != "") {
    chapterDTO.value.id = chapterList.value[activeName.value - 1].id;
  } else {
    chapterDTO.value.id = null;
  }
  isAdd.value = true;
};
let loadingInstance = null;
const saveChapterOrNode = async () => {
  if (coursePublic.value == "审批") {
    ElMessage.warning("课程审批中，无法修改");
    return;
  } else if (coursePublic.value == "公开") {
    ElMessage.warning("课程已上线，无法修改");
    return;
  }

  if (checkDTO()) {
    return;
  }

  if (isChapter.value && isAdd.value) {
    chapterDTO.value.courseId = useCourseStore().course.id;
    const result = await saveChapterService(chapterDTO.value);
    result.data.message
      ? ElMessage.success(result.data.message)
      : ElMessage.error("添加成功");
    cleanDTO();
    await getChapterList(courseStore.course.id);
  } else if (isChapter.value && isAdd.value == false) {
    const result = await updateChapterService(chapterDTO.value);
    result.data.message
      ? ElMessage.success(result.data.message)
      : ElMessage.error("修改成功");
    cleanDTO();
    await getChapterList(courseStore.course.id);
  } else if (isChapter.value == false && isAdd.value) {
    loadingInstance = ElLoading.service({ fullscreen: true });
    nodeDTO.value = {
      id: null,
      chapterId: chapterDTO.value.id,
      title: nodeVO.value.title,
      introduce: nodeVO.value.introduce,
      content: null,
    };
    ElMessage.info("上传中，请耐心等待...");
    await upload.value!.submit();
    // loadingInstance.close();
  } else {
    // 修改节点
    nodeDTO.value = {
      id: nodeVO.value.id,
      chapterId: chapterDTO.value.id,
      title: nodeVO.value.title,
      introduce: nodeVO.value.introduce,
      content: null,
    };
    if (fileList.value.length != 0) {
      loadingInstance = ElLoading.service({ fullscreen: true });
      ElMessage.info("上传中，请耐心等待...");
      await upload.value!.submit();
      // loadingInstance.close();
    } else {
      nodeDTO.value.content = null;
      const result: any = await updateNodeByIdService(nodeDTO.value);
      await getChapterList(useCourseStore().course.id);
      result.data.message
        ? ElMessage.success(result.data.message)
        : ElMessage.success("修改成功");

      cleanDTO();
      nextTick(async () => {
        await handleClick(
          chapterList.value[activeName.value - 1].id,
          activeName.value - 1,
          oldIndex.value
        );
        nodeVO.value =
          chapterList.value[activeName.value - 1].nodeVOS[oldIndex.value];
        URL.value = nodeVO.value.content; //更新 URL
        // loadingInstance.close();
      });
    }
  }
};

const deleteChapterOrNode = async () => {
  if (coursePublic.value == "审批") {
    ElMessage.warning("课程审批中，无法修改");
    return;
  } else if (coursePublic.value == "公开") {
    ElMessage.warning("课程已上线，无法修改");
    return;
  }

  if(isAdd.value) {
    ElMessage.warning("请在编辑页面下点击删除");
    return;
  }

  if (isChapter.value) {
    ElMessageBox.confirm(`确定要删除章节 "${chapterDTO.value.title}"?`, "温馨提示", {
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      type: "warning",
    })
      .then(async () => {
        const result = await deleteChapterService(chapterDTO.value.id);
        result.data.message
          ? ElMessage.success(result.data.message)
          : ElMessage.error("删除失败");
        await getChapterList(courseStore.course.id);
        model.value = !model.value;
      })
      .catch(() => {
        null;
      });
  } else {
    ElMessageBox.confirm(`确定要删除节点 "${nodeVO.value.title}"?`, "温馨提示", {
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      type: "warning",
    })
      .then(async () => {
        const result = await deleteNodeByIdService(nodeVO.value.id);
        result.data.message
          ? ElMessage.success(result.data.message)
          : ElMessage.error("删除失败");
        await getChapterList(courseStore.course.id);
        model.value = !model.value;
        if (chapterList.value[oldActiveName.value - 1].nodeVOS.length != 0) {
          oldIndex.value =
            chapterList.value[oldActiveName.value - 1].nodeVOS.length - 1;
          nextTick(async () => {
            await handleClick(
              chapterList.value[activeName.value - 1].id,
              activeName.value - 1,
              oldIndex.value
            );
            nodeVO.value =
              chapterList.value[activeName.value - 1].nodeVOS[oldIndex.value];
            URL.value = nodeVO.value.content;
          });
        } else {
          oldIndex.value = null;
          URL.value = null;
          isAdd.value = true;
          cleanDTO();
        }
      })
      .catch(() => {
        null;
      });
  }
};

const handleSuccess = async (response: any) => {
  nodeDTO.value.content = response.data;
  if (isAdd.value) {
    const result: any = await saveNodeService(nodeDTO.value);
    await getChapterList(useCourseStore().course.id);
    model.value = !model.value;
    result.data.message
      ? ElMessage.success(result.data.message)
      : ElMessage.success("添加成功");
    nextTick(async () => {
      await handleClick(
        chapterList.value[activeName.value - 1].id,
        activeName.value - 1,
        chapterList.value[activeName.value - 1].nodeVOS.length - 1
      );
      URL.value = response.data;
      loadingInstance.close();
      nodeVO.value =
        chapterList.value[activeName.value - 1].nodeVOS[
          chapterList.value[activeName.value - 1].nodeVOS.length - 1
        ];
    });
  } else {
    const result: any = await updateNodeByIdService(nodeDTO.value);
    await getChapterList(useCourseStore().course.id);
    result.data.message
      ? ElMessage.success(result.data.message)
      : ElMessage.success("修改成功");
    nextTick(async () => {
      await handleClick(
        chapterList.value[activeName.value - 1].id,
        activeName.value - 1,
        oldIndex.value
      );
      URL.value = response.data;
      loadingInstance.close();
      nodeVO.value =
        chapterList.value[activeName.value - 1].nodeVOS[oldIndex.value];
    });
  }

  fileList.value = [];
  cleanDTO();
};

// 课程 --------------------------------------

// 检查
const checkCourseInfo = () => {
  if (courseDTO.value.name.trim() == "") {
    ElMessage.error("课程标题不能为空");
    return true;
  }

  if (courseDTO.value.majorId == "") {
    ElMessage.error("课程专业不能为空");
    return true;
  }

  if (courseDTO.value.point == null) {
    ElMessage.error("课程学分不能为空");
    return true;
  }

  if (checkedTeamList.value.length == 0) {
    ElMessage.error("课程班级不能为空");
    return true;
  }

  // 计算课程时间
  const startTime = new Date(time.value[0]);
  const endTime = new Date(time.value[1]);
  const timeDiff = endTime.getTime() - startTime.getTime();

  if (timeDiff <= 7 * 24 * 60 * 60 * 1000 - 1) {
    ElMessage.error("课程时间不能少于7天");
    return true;
  }

  if (courseDTO.value.introduce == "") {
    ElMessage.error("课程介绍不能为空");
    return true;
  }

  if (file.value == null && courseDTO.value.picture == "") {
    ElMessage.error("请上传图片");
    return true;
  }

  if (
    courseDTO.value.picture == "" &&
    file.value[0].raw.type != "image/png" &&
    file.value[0].raw.type != "image/jpg" &&
    file.value[0].raw.type != "image/jpeg"
  ) {
    ElMessage.warning("请上传一个 PNG/JPG 格式的文件，大小不超过 10 MB");
    fileList.value = [];
    return true;
  }

  return false;
};

// 数据填充
const saveCourse = async () => {
  if (checkCourseInfo()) return;

  courseDTO.value.startTime =
    time.value[0].toLocaleDateString() +
    " " +
    time.value[0].toLocaleTimeString();

  courseDTO.value.endTime =
    time.value[1].toLocaleDateString() +
    " " +
    time.value[0].toLocaleTimeString();

  courseDTO.value.pub = "0";
  courseDTO.value.teamIds = checkedTeamList.value;

  if (file.value.length != 0) {
    uploadRef.value!.submit();
  } else {
    courseDTO.value.picture = null;
    await updateCourseInfo();
  }
};

// 课程信息
const successUploadFile = async (response) => {
  courseDTO.value.picture = response.data;
  if (isNewCourse.value) {
    await saveCourseInfo();
  } else {
    await updateCourseInfo();
  }
  isCourseInfo.value = !isCourseInfo.value;
};

// 保存课程信息
const saveCourseInfo = async () => {
  const result = await saveCourseService(courseDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "保存成功");
  courseStore.removeCourse();
  courseStore.setCourse({ id: result.data.data });
};

// 修改课程信息
const updateCourseInfo = async () => {
  const result = await updateCourseService(courseDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "修改成功");
};

// 上线审批
const approvalCourse = async () => {
  if (chapterList.value.length == 0) {
    ElMessage.warning("至少有一章节才能发布");
    return;
  } else {
    for (let i = 0; i < chapterList.value.length; i++) {
      if (chapterList.value[i].nodeVOS.length == 0) {
        ElMessage.warning("发布课程不能有空章节");
        return;
      }
    }
  }

  if (coursePublic.value != "私有") {
    ElMessage.warning("课程已提交审批");
    return;
  }

  const result = await approvalCourseService(useCourseStore().course.id);
  result.data.message
    ? ElMessage.success(result.data.message)
    : ElMessage.error("操作失败");
};

// 清除数据
const cleanCourseDTO = () => {
  courseDTO.value = {
    id: null,
    name: "",
    majorId: "",
    startTime: "",
    endTime: "",
    point: 1,
    introduce: "",
    picture: "",
    pub: "0",
    teamIds: [],
  };
  file.value = [];
  checkedTeamList.value = [];
  time.value = [new Date(), new Date()];
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

onBeforeUnmount(() => {
  // nodeStore.removeNode();
  URL.value = null;
});
</script>

<template>
  <div
    class="course"
    v-if="isCourseInfo"
    style="width: 100%; height: 100vh; overflow: hidden"
  >
    <el-card
      style="
        width: 100%;
        height: 100%;
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
                    @change="handleChange(activeName)"
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
                <span class="del-span">
                  <el-popover
                    placement="top-start"
                    title="模式切换"
                    :width="200"
                    trigger="hover"
                    :content="info"
                  >
                    <template #reference>
                      <el-button
                        type="danger"
                        text
                        bg
                        plain
                        circle
                        style="color: white; background-color: royalblue"
                        @click="refresh()"
                      >
                        <el-icon size="16"><Refresh /></el-icon>
                      </el-button>
                    </template>
                  </el-popover>
                </span>
                <span v-if="model" class="tools-span">
                  <el-button @click="backHome" type="primary" text circle>
                    <el-icon size="16"><HomeFilled /></el-icon>
                  </el-button>
                  <el-button @click="courseIntro" type="warning" text circle>
                    <el-icon size="16"><List /></el-icon>
                  </el-button>
                  <el-button @click="approvalCourse" type="success" text circle>
                    <el-icon size="16"> <UploadFilled /></el-icon>
                  </el-button>
                </span>
                <span v-else class="edit-span">
                  <el-button type="primary" @click="clickAdd" text circle>
                    <el-icon size="16"><DocumentAdd /></el-icon>
                  </el-button>
                  <el-button
                    type="danger"
                    @click="deleteChapterOrNode"
                    text
                    circle
                  >
                    <el-icon size="16"><DocumentDelete /></el-icon>
                  </el-button>
                  <el-button
                    type="success"
                    text
                    circle
                    @click="saveChapterOrNode"
                  >
                    <el-icon size="16"> <DocumentChecked /></el-icon>
                  </el-button>
                </span>
              </el-footer>
            </el-container>
          </div>
        </el-aside>
        <el-main style="background-color: #f4f4f5; height: 100vh; padding: 0">
          <div class="addAndEdit-form" style="height: 100vh" v-if="!model">
            <h1
              class="H1"
              style="margin-bottom: 20px; text-align: center"
              v-if="isAdd"
            >
              {{ isChapter ? "新增章节" : "添加节点" }}
            </h1>
            <h1
              class="H1"
              style="margin-bottom: 20px; text-align: center"
              v-else
            >
              {{ isChapter ? "编辑章节" : "编辑节点" }}
            </h1>
            <div v-if="isChapter">
              <el-input
                style="width: 100%; height: 50px"
                class="node-title"
                maxlength="30"
                placeholder="请输入章节标题..."
                show-word-limit
                type="text"
                v-model="chapterDTO.title"
              />
            </div>
            <div v-else>
              <el-input
                style="width: 100%; height: 50px"
                class="node-title"
                maxlength="30"
                placeholder="请输入节点标题..."
                show-word-limit
                type="text"
                v-model="nodeVO.title"
              />
              <el-input
                maxlength="200"
                rows="7"
                resize="none"
                class="node-intro"
                style="width: 100%; margin: 12px 0 20px 0"
                placeholder="请输入节点简介..."
                show-word-limit
                type="textarea"
                v-model="nodeVO.introduce"
                input-style="{            overflow-y: scroll !important;
                overflow-x: none !important;
                -ms-overflow-style: none !important;
                scrollbar-width: none !important;  }"
              />

              <el-upload
                ref="upload"
                class="node-content"
                :limit="1"
                :show-file-list="true"
                :auto-upload="false"
                :on-success="handleSuccess"
                action="/api/uploadMP4"
                :headers="{ Authorization: token }"
                v-model:file-list="fileList"
                drag
                multiple
              >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">拖拽上传 or <em>点击上传</em></div>
                <template #tip>
                  <div class="el-upload__tip" style="margin: 10px">
                    文件大小请不要超过 200 MB
                  </div>
                </template>
              </el-upload>
            </div>
          </div>
          <div class="video" style="width: 100%; height: 100vh" v-else>
            <video :src="URL" controls></video></div
        ></el-main>
      </el-container>
    </el-card>
  </div>
  <div class="intro" v-else>
    <div class="table">
      <!-- 课程信息 -->
      <div class="table-1" style="height: 800px">
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
              @click="backHome"
              class="closeBold"
            />
            <el-button
              style="margin-right: 12px"
              type="warning"
              circle
              size="small"
              :icon="SemiSelect"
              @click="hideCoursePaper"
              class="semiSelect"
            />
            <el-button
              style="margin: 0"
              type="success"
              circle
              size="small"
              :icon="Select"
              @click="saveCourse"
              class="select"
            />
          </span>
        </h1>
        <!-- 课程信息 -->
        <el-divider style="margin: 20px 0" />
        <el-form :model="courseDTO" label-width="auto" style="max-width: 720px">
          <el-form-item class="form-item" label="课程标题">
            <el-input
              class="title-input"
              v-model="courseDTO.name"
              show-word-limit
              maxlength="30"
            />
          </el-form-item>
          <el-form-item class="form-item" label="专业名称" prop="region">
            <el-select
              v-model="courseDTO.majorId"
              placeholder="请选择专业"
              class="title-input"
              filterable
            >
              <el-option
                :label="major.name"
                :value="major.id"
                v-for="(major, index) in majorList"
                style="padding-left: 10px"
                :key="index"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="form-item" label="课程学分">
            <el-input-number
              class="title-input"
              v-model="courseDTO.point"
              :step="0.5"
              :min="1"
              :max="20"
            />
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
              style="width: 100%"
              class="title-input"
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
                style="padding-left: 12px"
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
            />
          </el-form-item>
          <el-form-item class="form-item" label="课程封面">
            <el-upload
              ref="uploadRef"
              class="upload-input"
              drag
              action="/api/uploadImg"
              multiple
              style="width: 100%"
              v-model:file-list="file"
              :show-file-list="true"
              :auto-upload="false"
              :limit="1"
              :on-success="successUploadFile"
              :headers="{ Authorization: token }"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">拖拽上传 or <em>点击上传</em></div>
              <template #tip>
                <div class="el-upload__tip">文件大小最好不要超过 10 MB</div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item
            style="margin-top: 2px"
            class="form-item"
            label="课程简介"
          >
            <el-input
              class="textarea-input"
              resize="none"
              rows="5"
              v-model="courseDTO.introduce"
              type="textarea"
              maxlength="1000"
              placeholder="请输入课程简介 [1000字以内]"
              show-word-limit
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
.video {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
}

.video video {
  width: 100%;
  height: 100%;
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
      width: 75%;
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
    height: 800px;
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
