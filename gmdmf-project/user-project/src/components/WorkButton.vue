<template>
  <div style="margin-top: 7px">
    <VaButton
      preset="primary"
      icon-right="search"
      size="small"
      class="btn"
      round
      @click="click"
      style="width: 50px; height: 25px; margin: auto"
    >
    </VaButton>
  </div>
</template>

<script lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { usePaperStore } from "@/stores/paper";

export default {
  setup(props: any) {
    const router = useRouter();
    const paperStore = usePaperStore();

    const click = () => {
      router.push("/course-paper");
      paperStore.setPaper(props.params.data.id);
    };

    return {
      router,
      paperStore,
      click,
    };
  },
};
</script>

<style>
.btn {
  margin: 8px auto;
  width: 24px;
}
</style>
