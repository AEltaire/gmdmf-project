<template>
  <div
    class="ag-custom-component-popup"
    style="display: flex; justify-content: center; width: 100px; height: 24px"
  >
    <VaFileUpload
      type="single"
      hideFileList="true"
      size="small"
      v-model="basic"
      class="upload"
      uploadButtonText="上传头像"
      @fileAdded="upload"
    >
      <template #default>
        <VaButton size="small" preset="secondary" >
          上传头像
        </VaButton></template
      >
    </VaFileUpload>
  </div>
</template>

<script>
import { onMounted, ref } from "vue";
import { uploadImgService } from "@/api/Upload";

export default {
  setup(props) {
    let { value } = props.params;
    const basic = ref();
    const upload = async () => {
      const form = new FormData()
      form.append('file', basic.value)
      const result = await uploadImgService(form);
      props.params.data.picture = result.data.data;
      props.params.api.stopEditing();
    };
    return { basic, upload };
  },
};
</script>

<style>
.ag-custom-component-popup {
  margin: 10px 5px;
  --va-button-font-size: 12px;
  --va-file-upload-margin: 0;
  --va-button-content-py: 2px;
  --va-button-content-px: 8px;
}
</style>
