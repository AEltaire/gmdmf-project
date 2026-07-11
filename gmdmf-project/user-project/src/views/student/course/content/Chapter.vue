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
  router.push("/s-chapter");
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

</style>
