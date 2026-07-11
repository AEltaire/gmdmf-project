<script lang="ts" setup>
import { onBeforeMount, onMounted, ref, watch, onBeforeUnmount } from "vue";
import { findByIdChapterService } from "@/api/Chapter";
import { MoreFilled } from "@element-plus/icons-vue";
import { useCourseStore } from "@/stores/course";
import { ElMessage } from "element-plus";
import { useNodeStore } from "@/stores/node";
import { useRouter } from "vue-router";

// Pinia
const courseId = useCourseStore().course.id;
const nodeStore = useNodeStore();
const activeNames = ref(null);
const router = useRouter();

// 章节列表
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
        size: "large",
        type: "primary",
        icon: MoreFilled,
        hollow: true,
      },
    ],
  },
]);

// 获取章节列表
const getChapterList = async () => {
  const result = await findByIdChapterService(courseId);
  chapterList.value = result.data.data;

  if (chapterList.value.length == 0) {
    empty.value = true;
    return;
  }

  // 遍历章节列表
  for (let c: any, i: number = 0; i < chapterList.value.length; i++) {
    c = chapterList.value[i];
    // 遍历章节下的节点列表
    for (let n: any, j: number = 0; j < c.nodeVOS.length; j++) {
      n = c.nodeVOS[j];
      n.type = "primary";
      n.hollow = true;

      if (c.nodeVOS[j].status == 0) {
        n.size = "large";
        n.icon = MoreFilled;
        n.hollow = false;
      }
    }
  }
};

const empty = ref(false);

onMounted(async () => {
  await getChapterList();
});

const lookAtNode = (node, index, i) => {
  nodeStore.setNode({ node, index, i });
  router.push("/t-chapter");
};

const goToChapter = () => {
  nodeStore.removeNode();
  router.push("/t-chapter");
};
</script>

<template>
  <el-card shadow="never" style="height: 82vh; position: relative">
    <div v-if="empty == true">
      <el-empty :image-size="400" />
    </div>
    <div class="demo-collapse" v-else>
      <el-collapse v-model="activeNames" accordion>
        <el-collapse-item
          :name="index + 1"
          v-for="(chapter, index) in chapterList"
          :key="index"
        >
          <template #title>
            <span>{{ chapter.title }}</span>
          </template>
          <el-timeline style="margin-top: 16px; margin-left: 20px">
            <el-timeline-item
              v-for="(node, i) in chapter.nodeVOS"
              :key="i"
              :icon="node.icon"
              :type="node.type"
              :size="node.size"
              :hollow="node.hollow"
              :timestamp="node.createTime"
            >
              <span
                @click="lookAtNode(node, index, i)"
                class="node-title"
                style=""
                >{{ node.title }}</span
              >
            </el-timeline-item>
          </el-timeline>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div style="margin-bottom: 16px" class="tools">
      <!-- 添加题库 -->
      <button class="contactButton" @click="goToChapter">
        编辑章节
        <div class="iconButton">
          <svg
            t="1715927486406"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="3601"
            width="16"
            height="16"
          >
            <path
              d="M455.111111 170.666667h113.777778v682.666666H455.111111z"
              fill="#ffffff"
              p-id="3602"
            ></path>
            <path
              d="M853.333333 455.111111v113.777778H170.666667V455.111111z"
              fill="#ffffff"
              p-id="3603"
            ></path>
          </svg>
        </div>
      </button>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.node-title {
  position: relative;
  top: -3.8px;
  color: #000;
  text-decoration: none;
  cursor: pointer;
}

.contactButton {
  float: right;
  background: #7079f0;
  color: white;
  font-family: inherit;
  margin-left: 1px;
  padding: 0;
  padding-left: 1em;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
  box-shadow: inset 0 0 1.6em -0.6em #714da6;
  overflow: hidden;
  position: relative;
  height: 32px;
  padding-right: 3em;
  margin-right: 10px;
}

.iconButton {
  margin-left: 1em;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2em;
  width: 2em;
  border-radius: 6px;
  box-shadow: 0.1em 0.1em 0.6em 0.2em #7a8cf3;
  right: 0.3em;
  transition: all 0.3s;
}

.contactButton:hover {
  transform: translate(-0.05em, -0.05em);
  box-shadow: 0.15em 0.15em #5566c2;
}

.contactButton:active {
  transform: translate(0.05em, 0.05em);
  box-shadow: 0.05em 0.05em #5566c2;
}

.tools {
  position: absolute;
  width: 100%;
  padding-right: 32px;
  bottom: 0;
}
</style>
