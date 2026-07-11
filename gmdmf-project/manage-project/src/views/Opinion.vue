<script setup lang="ts">
import {
  ref,
  watchEffect,
  onMounted,
  computed,
  onUnmounted,
  nextTick,
} from "vue";
import { findAllOpinionService, deleteOpinionService } from "@/api/Opinion";
import { useTokenStore } from "@/stores/token";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import $ from "jquery";
const store = useStore();
const showModal = ref(false);
var websocket = null;
var clientId = Math.random().toString(36).substr(2);

const opinionList = ref([]);
const findAllOpinion = async () => {
  const result = await findAllOpinionService();
  opinionList.value = result.data.data;
};

onMounted(async () => {
 await findAllOpinion();
});

const opinionVO = ref({
  id: null,
  content: "",
});

const showOpinion = (opinion) => {
  showModal.value = true;
  opinionVO.value = opinion;
};

const waitDelete = ref(false);
const waitTime = ref(0);
let id = null;

const deleteOpinion = (opinion) => {
  waitTime.value = 100;
  waitDelete.value = !waitDelete.value;
  if (waitDelete.value) {
    if (id) clearTimeout(id);
    id = setTimeout(async () => {
      if (!waitDelete.value || waitTime.value == 0) {
        return;
      }
      await deleteOpinionService(opinionVO.value.id);
      ElMessage.success("删除成功");
      showModal.value = false;
      waitDelete.value = false;
      waitTime.value = 0;
      findAllOpinion();
    }, 2000);
  } else {
    clearTimeout(id);
    waitTime.value = 0;
  }
};

const showList = ref(true);

const ani = () => {
  showList.value = false;
  setTimeout(() => {
    showList.value = true;
  }, 640);
};
</script>

<template>
  <VaModal v-model="showModal" hide-default-actions overlay-opacity="0.2">
    <h6 class="va-h6" style="margin-bottom: 20px">意见反馈</h6>
    <p>
      {{ opinionVO.content }}
    </p>
    <template #footer>
      <VaButton
        @click="deleteOpinion(opinionVO.id)"
        preset="primary"
        style="margin-right: 10px"
      >
        <svg
          v-if="waitDelete"
          xmlns="http://www.w3.org/2000/svg"
          height="20px"
          viewBox="0 -960 960 960"
          width="20px"
          fill="#3868ca"
        >
          <path
            d="m256-200-56-56 224-224-224-224 56-56 224 224 224-224 56 56-224 224 224 224-56 56-224-224-224 224Z"
          />
        </svg>
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          height="20px"
          viewBox="0 -960 960 960"
          width="20px"
          fill="#7395da"
        >
          <path
            d="M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z"
          />
        </svg>
      </VaButton>
      <VaButton style="width: 80px" @click="showModal = false"> 好的 </VaButton>
      <VaProgressBar
        class="bar"
        style="
          margin-top: 15px;
          --va-progress-bar-buffer-transition: width ease 5s;
        "
        :model-value="waitTime"
        :buffer="100"
      />
    </template>
  </VaModal>
  <VaCard stripe stripe-color="primary">
    <VaCardContent>
      <VaScrollContainer
        class="max-h-52"
        style="
          overflow-y: scroll;
          overflow-x: none;
          -ms-overflow-style: none;
          scrollbar-width: none;
          height: 79.5vh;
        "
        vertical
      >
        <transition v-show="showList" name="fade" appear>
          <VaList style="height: 79.5vh">
            <VaListLabel> 意见反馈 </VaListLabel>
            <VaListItem
              v-for="(opinion, index) in opinionList"
              :key="index"
              class="list__item"
            >
              <VaListItemSection avatar>
                <VaAvatar>
                  <img
                    :src="
                      opinion.picture
                        ? opinion.picture
                        : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                    "
                    :alt="opinion.username"
                  />
                </VaAvatar>
              </VaListItemSection>
              <VaListItemSection>
                <VaListItemLabel style="margin-bottom: 2px; font-weight: 600">
                  <span
                    :class="store.state.isLightTheme ? null : 'text-color-dark'"
                    style="margin-right: 4px"
                    >{{ opinion.username }}</span
                  >
                  <el-tag
                    :type="
                      opinion.state == '好评'
                        ? 'success'
                        : opinion.state == '差评'
                        ? 'danger'
                        : 'warning'
                    "
                    size="small"
                    style="margin-left: 5px; margin-bottom: 3px"
                    >{{ opinion.state }}</el-tag
                  >
                </VaListItemLabel>

                <VaListItemLabel style="margin-bottom: 3px" caption>
                  {{ opinion.content }}
                </VaListItemLabel>
              </VaListItemSection>
              <VaListItemSection
                @click="showOpinion(opinion)"
                style="cursor: pointer"
                icon
              >
                <VaIcon name="remove_red_eye" color="primary" />
              </VaListItemSection>
            </VaListItem>
          </VaList>
        </transition>
      </VaScrollContainer>
    </VaCardContent>
  </VaCard>
</template>

<style lang="css" scoped>
.text-color-dark {
  color: #f1f1f1;
}
.va-table-responsive {
  overflow: auto;
}

.list__item + .list__item {
  margin-top: 20px;
}

.bar {
  --va-progress-bar-buffer-transition: width linear 2s;
  --va-progress-bar-transition: width linear 2s;
}

/* fade-enter: DOM 进入初始状态 */
.fade-enter-from {
  /* transform: translateX(20px); */
  opacity: 0;
}

/* fade-enter-active: 过渡动画 */
.fade-enter-active {
  transition: all 0.64s ease-in-out;
}

/* fade-enter-to: DOM 进入结束状态 */
.fade-enter-to {
  /* transform: translateX(0px); */
  opacity: 1;
}

.fade-leave-from {
  transform: translateX(0px);
  opacity: 1;
}

.fade-leave-active {
  transition: all 0.64s ease-in-out;
}

.fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>
