<template>
  <div style="margin-top: 10px">
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
import { usePaperApprovalStore } from "@/stores/paperApproval";
export default {
  setup(props: any) {
    const router = useRouter();
    const paperApprovalStore = usePaperApprovalStore();

    const click = () => {
      router.push("/t-paper");
      paperApprovalStore.setPaperApproval(props.params.data.id);
    };

    return {
      router,
      paperApprovalStore,
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
