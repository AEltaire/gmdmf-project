<script lang="ts" setup>
import { ref, onMounted, onBeforeMount } from "vue";
import { findByPaperQueryService } from "@/api/Paper";
import { useRouter } from "vue-router";
import { usePaperStore } from "@/stores/paper";

const router = useRouter();
const paperStore = usePaperStore();

const paperQuery = ref({
  id: 0,
  type: "作业",
  title: "",
  startTime: "",
  notEndTime: true,
});

const paperList = ref([
  {
    id: 0,
    type: "",
    school: "",
    teacher: "",
    courseName: "",
    title: "",
    sum: 0,
    startTime: "",
    endTime: "",
    createTime: "",
  },
]);

const empty = ref(true);

const findByPage = async () => {
  paperQuery.value.startTime =
    new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString();
  const result = await findByPaperQueryService(paperQuery.value);
  paperList.value = result.data.data;
  paperList.value.length == 0 ? (empty.value = true) : (empty.value = false);
};

const goPaper = (id) => {
  router.push("/s-paper");
  paperStore.setPaper(id);
};

onMounted(async () => {
  await findByPage();
});
</script>

<template>
  <el-card shadow="never" style="height: 83vh;           overflow-y: scroll;
          overflow-x: none;
          -ms-overflow-style: none;
          scrollbar-width: none;">
    <div v-if="empty == true">
      <el-empty :image-size="400" />
    </div>
    <div v-else>
      <el-timeline
        style="
          width: 84%;
          height: 100%;
          margin-top: 16px;

        "
      >
        <el-timeline-item
          center
          timestamp="2018/4/12"
          placement="top"
          v-for="paper in paperList"
          :key="paper.id"
        >
          <el-card shadow="hover" class="card-sub">
            <el-descriptions :title="paper.title">
              <el-descriptions-item label="课程">{{
                paper.courseName
              }}</el-descriptions-item>
              <el-descriptions-item label="教师">{{
                paper.teacher
              }}</el-descriptions-item>
              <el-descriptions-item label="分数">{{
                paper.sum
              }}</el-descriptions-item>
              <el-descriptions-item label="开始时间">{{
                paper.startTime
              }}</el-descriptions-item>
              <el-descriptions-item label="结束时间">{{
                paper.endTime
              }}</el-descriptions-item>
            </el-descriptions>
            <button @click="goPaper(paper.id)">Go</button>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.card-sub {
  margin-bottom: 16px;
  min-width: 500px;
}

button {
  float: right;
  margin-bottom: 12px;
  padding: 8px 32px;
  border-radius: 40px;
  cursor: pointer;
  border: 0;
  background-color: hsl(261deg 80% 48%);
  color: hsl(0, 0%, 100%);
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-size: 15px;
  transition: all 0.5s ease;
}

button:hover {
  letter-spacing: 3px;
  background-color: hsl(261deg 80% 48%);
  color: hsl(0, 0%, 100%);
  // box-shadow: rgb(93 24 220) 0px 7px 29px 0px;
}

button:active {
  letter-spacing: 3px;
  background-color: hsl(261deg 80% 48%);
  color: hsl(0, 0%, 100%);
  box-shadow: rgb(93 24 220) 0px 0px 0px 0px;
  transform: translateY(4px);
  transition: 100ms;
}
</style>
