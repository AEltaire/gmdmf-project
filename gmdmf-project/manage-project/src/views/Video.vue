<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watchEffect } from "vue";
import { useBreakpoint } from "vuestic-ui";
import { useRouter } from "vue-router";
import { useCourseStore } from "@/stores/course";
import { updateApprovalService } from "@/api/Approval";
import { findByCourseIdChapterService } from "@/api/Chapter";
import { ElMessage } from "element-plus";

interface Approval {
  id: number;
  courseId: number;
  manage: string;
  state: boolean;
}

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
  status: string;
  createTime: Date;
  approval: string;
}

const router = useRouter();
const courseStore = useCourseStore();
const breakpoints = useBreakpoint();
const isSidebarVisible = ref(false);
const chapterList = ref<Chapter[]>([]);
const activeElement = ref("");
const URL = ref(null);
const items = ref([]);
const showModal = ref(false);
const info = ref({
  title: "审批通过",
  placeholder: "课程审批通过",
});

const approvalDTO = ref<Approval>({
  id: null,
  courseId: null,
  manage: "",
  state: false,
});

onMounted(async () => {
  await findByCourseIdChapter();
});

onBeforeUnmount(() => {
  useCourseStore().removeCourse()
})

const findByCourseIdChapter = async () => {
  const result = await findByCourseIdChapterService(courseStore.course.id);
  chapterList.value = result.data.data;
  chapterList.value.forEach((chapter: Chapter) => {
    let nodeList = [];
    chapter.nodeVOS.forEach((node: NodeList) => {
      nodeList.push({
        title: node.title,
        icon: "send",
        content: node.content,
      });
    });
    items.value.push({
      title: chapter.title,
      children: nodeList,
    });
  });
};

const goHome = () => {
  router.back();
};

watchEffect(() => {
  isSidebarVisible.value = breakpoints.smUp;
});

const changeNode = (title, content) => {
  activeElement.value = title;
  URL.value = content;
};

const approvalCourse = (flag) => {
  if (flag) {
    info.value = {
      title: "审批通过",
      placeholder: "课程审批通过",
    };
    approvalDTO.value.state = true;
  } else {
    info.value = {
      title: "审批否决",
      placeholder: "课程审批不通过",
    };
    approvalDTO.value.state = false;
  }
  showModal.value = true;
};

const updateApproval = async () => {
  if (approvalDTO.value.manage.trim() == "")
    approvalDTO.value.manage = info.value.placeholder;
  approvalDTO.value.courseId = courseStore.course.id;
  const result = await updateApprovalService(approvalDTO.value);
  ElMessage.success(result.data.message ? result.data.message : "审批成功");
  showModal.value = false;
  approvalDTO.value = {
    id: null,
    courseId: null,
    manage: "",
    state: false
  };
  router.back();
};
</script>

<template>
  <VaModal
    v-model="showModal"
    ok-text="确定"
    cancel-text="取消"
    @ok="updateApproval"
  >
    <h5 class="va-h5">{{ info.title }}</h5>
    <br />
    <VaTextarea
      v-model="approvalDTO.manage"
      label="审批意见"
      :placeholder="info.placeholder"
      style="width: 100%; margin-bottom: 10px; font-size: 13px; height: 120px"
      :resize="false"
    />
  </VaModal>
  <VaLayout
    :left="{ fixed: true, absolute: breakpoints.smDown, order: 2 }"
    :top="{ fixed: true }"
  >
    <template #left>
      <div style="display: flex; height: 100%">
        <VaSidebar width="min-content" color="primary" hover-color="secondary">
          <VaSidebarItem>
            <VaSidebarItemContent @click="isSidebarVisible = !isSidebarVisible">
              <VaIcon :name="isSidebarVisible ? 'menu_open' : 'menu'" />
            </VaSidebarItemContent>
          </VaSidebarItem>
          <VaSpacer />
          <VaSidebarItem @click="approvalCourse(false)">
            <VaSidebarItemContent>
              <VaIcon name="cloud_off" />
            </VaSidebarItemContent>
          </VaSidebarItem>
          <VaSidebarItem @click="approvalCourse(true)">
            <VaSidebarItemContent>
              <VaIcon name="cloud_done" />
            </VaSidebarItemContent>
          </VaSidebarItem>

          <VaSidebarItem @click="goHome">
            <VaSidebarItemContent>
              <VaIcon name="home" />
            </VaSidebarItemContent>
          </VaSidebarItem>
        </VaSidebar>

        <!-- 章节 -->
        <VaSidebar v-model="isSidebarVisible">
          <VaAccordion class="bar" style="font-size: 13px">
            <template v-for="item in items">
              <VaCollapse
                v-if="item.children"
                :key="item.title + 'collapse'"
                body-color="#00000022"
              >
                <template #header="{ value: isCollapsed }">
                  <VaSidebarItem
                    :active="
                      item.children.some(
                        (child) => child.title === activeElement
                      )
                    "
                  >
                    <VaSidebarItemContent>
                      <VaIcon :name="item.icon" />
                      <VaSidebarItemTitle>{{ item.title }}</VaSidebarItemTitle>
                      <VaSpacer />
                      <VaIcon
                        :name="isCollapsed ? 'va-arrow-up' : 'va-arrow-down'"
                      />
                    </VaSidebarItemContent>
                  </VaSidebarItem>
                </template>

                <!-- 节点 -->
                <template #body>
                  <VaSidebarItem
                    v-for="child in item.children"
                    :key="child.title"
                    :active="child.title === activeElement"
                    @click="changeNode(child.title, child.content)"
                  >
                    <VaSidebarItemContent>
                      <VaIcon :name="child.icon" />
                      <VaSidebarItemTitle>{{ child.title }}</VaSidebarItemTitle>
                    </VaSidebarItemContent>
                  </VaSidebarItem>
                </template>
              </VaCollapse>

              <VaSidebarItem
                v-else
                :key="item.title + 'item'"
                :active="item.title === activeElement"
              >
                <VaSidebarItemContent>
                  <VaIcon :name="item.icon" />
                  <VaSidebarItemTitle>{{ item.title }}</VaSidebarItemTitle>
                </VaSidebarItemContent>
              </VaSidebarItem>
            </template>
          </VaAccordion>
        </VaSidebar>
      </div>
    </template>

    <template #content>
      <div class="video">
        <video :src="URL" controls></video>
      </div>
    </template>
  </VaLayout>
</template>

<style lang="css" scoped>
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

.bar {
  --va-sidebar-item-content-padding: 0 0 0 0.5rem;
  --va-sidebar-menu-max-height: 40px;
}
</style>
