<script lang="ts" setup>
import { ref, onMounted, onBeforeMount } from "vue";
import { ElMessage } from "element-plus";
import { useCourseStore } from "@/stores/course";
import { uploadFileService } from "@/api/Upload";
import { findByCourseIdResourceService } from "@/api/Resource";

const courseId = useCourseStore().course.id;
onMounted(async () => {
  await findAllResource();
});
const courseStore = useCourseStore();

const courseWare = ref([]);

const testQuestions = ref([
  {
    id: 0,
    name: "",
    type: "",
    url: "",
    createTime: "",
  },
]);

const information = ref([
  {
    id: 0,
    name: "",
    type: "",
    url: "",
    createTime: "",
  },
]);

const note = ref([
  {
    id: 0,
    name: "",
    type: "",
    url: "",
    createTime: "",
  },
]);

const findAllResource = async () => {
  const result = await findByCourseIdResourceService(courseId);

  if (result.data != "{}") {
    courseWare.value = result.data.data["课件"];
    testQuestions.value = result.data.data["试题"];
    information.value = result.data.data["资料"];
    note.value = result.data.data["笔记"];

    if (courseWare.value != undefined) {
      courseWare.value.forEach((item) => {
        let date = new Date(item.createTime);
        item.createTime = `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
      });
    }

    if (testQuestions.value != undefined) {
      testQuestions.value.forEach((item) => {
        let date = new Date(item.createTime);
        item.createTime = `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
      });
    }

    if (information.value != undefined) {
      information.value.forEach((item) => {
        let date = new Date(item.createTime);
        item.createTime = `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
      });
    }

    if (note.value != undefined) {
      note.value.forEach((item) => {
        let date = new Date(item.createTime);
        item.createTime = `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
      });
    }
  }
};

// 默认展开课件
const activeName = ref("课件");
const centerDialogVisible = ref(false);

const download = (row) => {
  location.href = row.url;
};

const uploadFile = async (formData) => {
  const result = await uploadFileService(formData);
  ElMessage.success(result.data.message ? result.data.message : "上传成功");
  await findAllResource();
  centerDialogVisible.value = false;
};

const submitUpload = async (evt) => {
  if (evt.raw.type != "application/pdf") {
    ElMessage.warning("请上传一个 PDF 格式的文件，大小不超过 10 MB");
    fileList.value = [];
    return;
  }

  flag.value = true;
  const formData = new FormData();
  let type = activeName.value ? activeName.value : "课件";

  formData.append("file", evt.raw);
  formData.append("courseId", courseStore.course.id);
  formData.append("name", evt.name);
  formData.append("type", type);

  await uploadFile(formData);
  flag.value = false;
  fileList.value = [];
};
const flag = ref(false);
const text = ref("文件上传中...");
const fileList = ref([]);
</script>

<template>
  <!-- 外框 -->
  <el-card shadow="never" style="height: 82vh">
    <el-dialog
      v-model="centerDialogVisible"
      title="上传文件"
      width="500"
      center
    >
      <el-upload
        :limit="1"
        class="upload-demo"
        :disabled="flag"
        drag
        v-model:file-list="fileList"
        :show-file-list="true"
        :on-change="submitUpload"
        :auto-upload="false"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">拖拽上传 or <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">
            <span style="color: gray; margin: 3px; font-size: 11px">
              PS: 将 PDF 文件上传至打开的目录，默认“课件”目录
              <span
                style="float: right; color: #67c23a"
                v-show="flag == true"
                >{{ text }}</span
              >
            </span>
          </div>
        </template>
      </el-upload>
    </el-dialog>
    <!-- 目录 -->
    <el-collapse v-model="activeName" accordion style="min-width: 800px">
      <el-collapse-item name="课件">
        <template #title>
          <svg
            t="1716298264549"
            class="ali-icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="14731"
            width="20"
            height="20"
            style="padding: 0 0.6rem"
          >
            <path
              d="M902.1 316.8v538c0 16.3-13.2 29.5-29.5 29.5H225c-16.3 0-29.5-13.2-29.5-29.5V159.3c0-16.3 13.2-29.5 29.5-29.5h426.2l12.1 148 75.6 39h163.2z"
              fill="#FBCCD1"
              p-id="14732"
            ></path>
            <path
              d="M882.6 511.5c-11.7 0-19.5 7.8-19.5 19.5v77.9c0 11.7 7.8 19.5 19.5 19.5s19.5-7.8 19.5-19.5V531c0-11.7-7.8-19.5-19.5-19.5z m-13.6-72c-3.9 3.9-5.8 7.8-5.8 13.6 0 1.9 0 5.8 1.9 7.8 1.9 1.9 1.9 3.9 3.9 5.8 3.9 3.9 9.7 5.8 13.6 5.8h3.9c1.9 0 1.9 0 3.9-1.9 1.9 0 1.9-1.9 3.9-1.9s1.9-1.9 3.9-1.9c1.9-1.9 3.9-3.9 3.9-5.8v-7.8c0-5.8-1.9-9.7-5.8-13.6-7.9-7.9-19.6-7.9-27.3-0.1z"
              fill="#F55467"
              p-id="14733"
            ></path>
            <path
              d="M902.1 316.8c0-1.9 0-3.9-1.9-7.8-1.9-1.9-1.9-3.9-3.9-5.8L662.5 69.4c-1.9-1.9-3.9-3.9-5.8-3.9-1.9-1.9-5.8-1.9-7.8-1.9H239.8c-64.3 0-116.9 52.6-116.9 116.9v662.3c0 64.3 52.6 116.9 116.9 116.9h545.4c64.3 0 116.9-52.6 116.9-116.9V686.9c0-11.7-7.8-19.5-19.5-19.5s-19.5 7.8-19.5 19.5v155.8c0 42.9-35.1 77.9-77.9 77.9H239.8c-42.9 0-77.9-35.1-77.9-77.9V180.4c0-42.9 35.1-77.9 77.9-77.9h389.6v116.9c0 64.3 52.6 116.9 116.9 116.9h116.9v39c0 11.7 7.8 19.5 19.5 19.5s19.5-7.8 19.5-19.5l-0.1-58.5z m-233.8-97.4v-89.6l167.5 167.5h-89.6c-42.8 0-77.9-35.1-77.9-77.9z"
              fill="#F55467"
              p-id="14734"
            ></path>
            <path
              d="M259.3 316.8h253.2c11.7 0 19.5-7.8 19.5-19.5s-7.8-19.5-19.5-19.5H259.3c-11.7 0-19.5 7.8-19.5 19.5s7.8 19.5 19.5 19.5z m486.9 175.3h-487c-11.7 0-19.5 7.8-19.5 19.5s7.8 19.5 19.5 19.5h487c11.7 0 19.5-7.8 19.5-19.5s-7.8-19.5-19.5-19.5z m0 214.2h-487c-11.7 0-19.5 7.8-19.5 19.5s7.8 19.5 19.5 19.5h487c11.7 0 19.5-7.8 19.5-19.5s-7.8-19.5-19.5-19.5z"
              fill="#F55467"
              p-id="14735"
            ></path>
          </svg>
          <span style="font-size: 16px; font-weight: bold">课件</span>
        </template>
        <el-table
          :data="courseWare"
          style="box-sizing: border-box"
          height="38vh"
          fit="true"
          align="center"
        >
          <el-table-column
            prop="createTime"
            label="上传时间"
            align="center"
            width="200px"
          />
          <el-table-column
            prop="name"
            label="文件名"
            align="center"
            width="700px"
          />
          <el-table-column label="操作" align="center" width="180px">
            <template #default="{ row }">
              <div class="box">
                <div class="button" @click="download(row)">
                  <div class="button-wrapper">
                    <div class="text" style="text-align: center;">下载</div>
                    <span class="icon">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        aria-hidden="true"
                        role="img"
                        width="2em"
                        height="2em"
                        preserveAspectRatio="xMidYMid meet"
                        viewBox="0 0 24 24"
                      >
                        <path
                          fill="none"
                          stroke="currentColor"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15V3m0 12l-4-4m4 4l4-4M2 17l.621 2.485A2 2 0 0 0 4.561 21h14.878a2 2 0 0 0 1.94-1.515L22 17"
                        ></path>
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="没有数据" />
          </template>
        </el-table>
      </el-collapse-item>
      <el-collapse-item name="试题">
        <template #title>
          <svg
            t="1716297863866"
            class="ali-icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="4479"
            width="20"
            height="20"
            style="padding: 0 0.6rem"
          >
            <path
              d="M865.8 150.3v793.6c0 21.7-17.6 39.3-39.3 39.3h-39.3c-21.7 0-39.3-17.6-39.3-39.3v-78.6H236.3v-715c0-21.7 17.6-39.3 39.3-39.3h590.1v39.3z"
              fill="#D6EAFE"
              opacity=".7"
              p-id="4480"
            ></path>
            <path
              d="M944 0.6H197.3c-43.4 0-78.6 35.2-78.6 78.6v786.1H0.7v78.6c0 43.4 35.2 78.6 78.6 78.6h746.8c43.4 0 78.6-35.2 78.6-78.6V157.8h117.9V79.2C1022.7 35.8 987.5 0.6 944 0.6z m-78.6 78.6v864.7c0 21.7-17.6 39.3-39.3 39.3h-39.3c-21.7 0-39.3-17.6-39.3-39.3v-78.6H158V79.2c0-21.7 17.6-39.3 39.3-39.3H876c-6.9 12-10.6 25.5-10.6 39.3z"
              fill="#5CBDFF"
              p-id="4481"
            ></path>
            <path
              d="M295.5 236.4H492c10.9 0 19.7-8.8 19.7-19.7 0-10.9-8.8-19.7-19.7-19.7H295.5c-10.9 0-19.7 8.8-19.7 19.7 0.1 10.9 8.9 19.7 19.7 19.7zM295.5 393.7h432.4c10.8 0 19.6-8.8 19.6-19.7 0-10.8-8.8-19.7-19.6-19.7H295.5c-10.9 0-19.7 8.8-19.7 19.7 0.1 10.9 8.9 19.7 19.7 19.7zM295.5 550.9h275.1c10.9 0 19.7-8.8 19.7-19.6 0-10.9-8.8-19.7-19.7-19.7H295.5c-10.9 0-19.7 8.8-19.7 19.7 0.1 10.8 8.9 19.6 19.7 19.6zM295.5 708.1h432.4c10.8 0 19.6-8.8 19.6-19.7s-8.8-19.7-19.6-19.7H295.5c-10.9 0-19.7 8.8-19.7 19.7s8.9 19.7 19.7 19.7z"
              fill="#5CBDFF"
              p-id="4482"
            ></path>
          </svg>
          <span style="font-size: 16px; font-weight: bold">试题</span>
        </template>
        <el-table
          :data="testQuestions"
          style="box-sizing: border-box"
           height="38vh"
          fit="true"
          align="center"
        >
          <el-table-column
            prop="createTime"
            label="上传时间"
            align="center"
            width="200px"
          />
          <el-table-column
            prop="name"
            label="文件名"
            align="center"
            width="700px"
          />
          <el-table-column label="操作" align="center" width="180px">
            <template #default="{ row }">
              <div class="box">
                <div class="button" @click="download(row)">
                  <div class="button-wrapper">
                    <div class="text">下载</div>
                    <span class="icon">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        aria-hidden="true"
                        role="img"
                        width="2em"
                        height="2em"
                        preserveAspectRatio="xMidYMid meet"
                        viewBox="0 0 24 24"
                      >
                        <path
                          fill="none"
                          stroke="currentColor"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15V3m0 12l-4-4m4 4l4-4M2 17l.621 2.485A2 2 0 0 0 4.561 21h14.878a2 2 0 0 0 1.94-1.515L22 17"
                        ></path>
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="没有数据" />
          </template>
        </el-table>
      </el-collapse-item>
      <el-collapse-item title="" name="资料">
        <template #title>
          <svg
            t="1716298143713"
            class="ali-icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="7779"
            width="20"
            height="20"
            style="padding: 0 0.6rem"
          >
            <path
              d="M238.592 184.7296m133.5808 0l436.8896 0q133.5808 0 133.5808 133.5808l0 524.9024q0 133.5808-133.5808 133.5808l-436.8896 0q-133.5808 0-133.5808-133.5808l0-524.9024q0-133.5808 133.5808-133.5808Z"
              fill="#FFF3AE"
              p-id="7780"
            ></path>
            <path
              d="M700.4672 920.9856H263.6288a159.3344 159.3344 0 0 1-159.1808-159.1808V236.9024a159.3344 159.3344 0 0 1 159.1808-159.1808h436.8384a159.3344 159.3344 0 0 1 159.1808 159.1808v524.9024a159.3344 159.3344 0 0 1-159.1808 159.1808zM263.6288 128.9216A108.0832 108.0832 0 0 0 155.648 236.9024v524.9024a108.0832 108.0832 0 0 0 107.9808 107.9808h436.8384a108.0832 108.0832 0 0 0 107.9808-107.9808V236.9024a108.0832 108.0832 0 0 0-107.9808-107.9808z"
              fill="#333333"
              p-id="7781"
            ></path>
            <path
              d="M651.1104 405.6576H315.904a25.6 25.6 0 0 1 0-51.2h335.2064a25.6 25.6 0 0 1 0 51.2zM484.352 607.744H315.904a25.6 25.6 0 0 1 0-51.2h168.448a25.6 25.6 0 0 1 0 51.2z"
              fill="#333333"
              p-id="7782"
            ></path>
          </svg>
          <span style="font-size: 16px; font-weight: bold">资料</span>
        </template>
        <el-table
          :data="information"
          style="box-sizing: border-box"
        height="38vh"
          fit="true"
          align="center"
        >
          <el-table-column
            prop="createTime"
            label="上传时间"
            align="center"
            width="200px"
          />
          <el-table-column
            prop="name"
            label="文件名"
            align="center"
            width="700px"
          />
          <el-table-column label="操作" align="center" width="180px">
            <template #default="{ row }">
              <div class="box">
                <div class="button" @click="download(row)">
                  <div class="button-wrapper">
                    <div class="text">下载</div>
                    <span class="icon">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        aria-hidden="true"
                        role="img"
                        width="2em"
                        height="2em"
                        preserveAspectRatio="xMidYMid meet"
                        viewBox="0 0 24 24"
                      >
                        <path
                          fill="none"
                          stroke="currentColor"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15V3m0 12l-4-4m4 4l4-4M2 17l.621 2.485A2 2 0 0 0 4.561 21h14.878a2 2 0 0 0 1.94-1.515L22 17"
                        ></path>
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="没有数据" />
          </template>
        </el-table>
      </el-collapse-item>
      <el-collapse-item title="" name="笔记">
        <template #title>
          <svg
            t="1716298465905"
            class="ali-icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="36145"
            width="20"
            height="20"
            style="padding: 0 0.6rem"
          >
            <path
              d="M725.333333 136.533333H349.866667a34.133333 34.133333 0 0 0-34.133334 34.133334v43.349333a31.402667 31.402667 0 0 0 16.042667 28.672 34.133333 34.133333 0 0 1 0 60.757333 31.402667 31.402667 0 0 0-16.042667 28.672V443.733333a31.744 31.744 0 0 0 16.042667 28.672 34.133333 34.133333 0 0 1 18.090667 30.037334 34.133333 34.133333 0 0 1-18.090667 30.378666 31.744 31.744 0 0 0-16.042667 28.672v111.957334a31.744 31.744 0 0 0 16.042667 28.672 34.133333 34.133333 0 0 1 18.090667 30.378666 34.133333 34.133333 0 0 1-18.090667 30.037334 31.744 31.744 0 0 0-16.042667 28.672V853.333333a34.133333 34.133333 0 0 0 34.133334 34.133334h375.466666a34.133333 34.133333 0 0 0 34.133334-34.133334V170.666667a34.133333 34.133333 0 0 0-34.133334-34.133334z"
              fill="#b2d235"
              p-id="36146"
            ></path>
            <path
              d="M810.666667 0h-546.133334a102.4 102.4 0 0 0-102.4 102.4v34.133333a34.133333 34.133333 0 0 0 68.266667 0V102.4a34.133333 34.133333 0 0 1 34.133333-34.133333h546.133334a34.133333 34.133333 0 0 1 34.133333 34.133333v819.2a34.133333 34.133333 0 0 1-34.133333 34.133333h-546.133334a34.133333 34.133333 0 0 1-34.133333-34.133333v-154.624H247.466667a34.133333 34.133333 0 0 0 0-68.266667h-17.066667v-161.792H247.466667a34.133333 34.133333 0 0 0 0-68.266666h-17.066667V307.2H247.466667a34.133333 34.133333 0 0 0 0-68.266667H145.066667a34.133333 34.133333 0 0 0 0 68.266667h17.066666v161.450667H145.066667a34.133333 34.133333 0 0 0 0 68.266666h17.066666v161.792H145.066667a34.133333 34.133333 0 0 0 0 68.266667h17.066666V921.6a102.4 102.4 0 0 0 102.4 102.4h546.133334a102.4 102.4 0 0 0 102.4-102.4V102.4a102.4 102.4 0 0 0-102.4-102.4z"
              fill="#666666"
              p-id="36147"
            ></path>
            <path
              d="M674.133333 238.933333a34.133333 34.133333 0 0 0-34.133333-34.133333h-204.8a34.133333 34.133333 0 0 0 0 68.266667h204.8a34.133333 34.133333 0 0 0 34.133333-34.133334zM469.333333 341.333333a34.133333 34.133333 0 0 0 0 68.266667h136.533334a34.133333 34.133333 0 0 0 0-68.266667z"
              fill="#666666"
              p-id="36148"
            ></path>
          </svg>
          <span style="font-size: 16px; font-weight: bold">笔记</span>
        </template>
        <el-table
          :data="note"
          style="box-sizing: border-box"
      height="38vh"
          fit="true"
          align="center"
        >
          <el-table-column
            prop="createTime"
            label="上传时间"
            align="center"
            width="200px"
          />
          <el-table-column
            prop="name"
            label="文件名"
            align="center"
            width="700px"
          />
          <el-table-column label="操作" align="center" width="180px">
            <template #default="{ row }">
              <div class="box">
                <div class="button" @click="download(row)">
                  <div class="button-wrapper">
                    <div class="text">下载</div>
                    <span class="icon">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        aria-hidden="true"
                        role="img"
                        width="2em"
                        height="2em"
                        preserveAspectRatio="xMidYMid meet"
                        viewBox="0 0 24 24"
                      >
                        <path
                          fill="none"
                          stroke="currentColor"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15V3m0 12l-4-4m4 4l4-4M2 17l.621 2.485A2 2 0 0 0 4.561 21h14.878a2 2 0 0 0 1.94-1.515L22 17"
                        ></path>
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="没有数据" />
          </template>
        </el-table>
      </el-collapse-item>
    </el-collapse>
    <button class="contactButton" @click="centerDialogVisible = true">
      上传文件
      <div class="iconButton">
        <el-icon size="15"><Upload /></el-icon>
      </div>
    </button>
  </el-card>
</template>

<style lang="scss" scoped>
.contactButton {
  float: right;
  background: #7079f0;
  color: white;
  font-family: inherit;
  margin: 20px;
  margin-right: 0px;
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

.box {
  position: relative;
  width: 100%;
  height: 32px;
}

.button {
  --width: 80px;
  --height: 32px;
  --tooltip-height: 35px;
  --tooltip-width: 90px;
  --gap-between-tooltip-to-button: 18px;
  --button-color: #1163ff;
  --tooltip-color: #fff;
  width: var(--width);
  height: var(--height);
  background: var(--button-color);
  position: absolute;
  left: 50%;
  margin-left: -40px;
  text-align: center;
  border-radius: 0.45em;
  font-family: "Arial";
  transition: background 0.3s;
}

.button::before {
  position: absolute;
  content: attr(data-tooltip);
  width: var(--tooltip-width);
  height: var(--tooltip-height);
  background-color: var(--tooltip-color);
  font-size: 0.9rem;
  color: #111;
  border-radius: 0.25em;
  line-height: var(--tooltip-height);
  bottom: calc(var(--height) + var(--gap-between-tooltip-to-button) + 10px);
  left: calc(50% - var(--tooltip-width) / 2);
}

.button::after {
  position: absolute;
  content: "";
  width: 0;
  height: 0;
  border: 10px solid transparent;
  border-top-color: var(--tooltip-color);
  left: calc(50% - 10px);
  bottom: calc(100% + var(--gap-between-tooltip-to-button) - 10px);
}

.button::after,
.button::before {
  opacity: 0;
  visibility: hidden;
  transition: all 0.5s;
}

.text {
  // display: flex;
  // align-items: center;
  // justify-content: center;
  text-align: center;
}

.button-wrapper,
.text,
.icon {
  overflow: hidden;
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  color: #fff;
}

.text {
  top: 0;
  margin: 0 !important
}

.text,
.icon {
  transition: top 0.5s;
}

.icon {
  color: #fff;
  top: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon svg {
  width: 24px;
  height: 24px;
}

.button:hover {
  cursor: pointer;
  background: #6c18ff;
}

.button:hover .text {
  top: -100%;
}

.button:hover .icon {
  top: 0;
}

.button:hover:before,
.button:hover:after {
  opacity: 1;
  visibility: visible;
}

.button:hover:after {
  bottom: calc(var(--height) + var(--gap-between-tooltip-to-button) - 20px);
}

.button:hover:before {
  bottom: calc(var(--height) + var(--gap-between-tooltip-to-button));
}
</style>
