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
    <VaModal
      v-model="dialogVisible"
      ok-text=""
      cancel-text=""
      :overlay="false"
      no-outside-dismiss
      close-button
      fullscreen
      style="z-index: 1000"
    >
      <h5 class="va-h5">成绩分析</h5>
      <div v-if="total == 0" style="width: 100%; height: 85vh; margin-top: 2vh">
        <el-empty :image-size="400" />
      </div>
      <div
        v-else
        id="paper"
        style="width: 100%; height: 85vh; margin-top: 2vh"
      ></div>
    </VaModal>
  </div>
</template>

<script lang="ts">
import {
  getPaperByStudentIdService,
  getScoreByStudentIdService,
} from "@/api/Data";
import { useCourseStore } from "@/stores/course";
import { findByStudentIdPaperStudentService } from "@/api/PaperStudent";
import { nextTick, onMounted, ref } from "vue";
import * as echarts from "echarts";
export default {
  setup(props: any) {
    const studentId = props.params.data.id;
    const courseId = useCourseStore().course.id;
    const dialogVisible = ref(false);

    const click = async () => {
      dialogVisible.value = true;
      await getScoreByStudentId();
      if (total.value == 0) {
        return;
      }
      await findByStudentIdPaperStudent();
      await getPaperByStudentId();
      await getScoreList();
      paperChart = echarts.init(document.getElementById("paper"));
      await paper();
      window.addEventListener("resize", function () {
        this.setTimeout(() => {
          paperChart.resize();
        }, 320);
      });
    };

    let paperChart = null;

    const total = ref(0);
    const paperList = ref(null);
    const paperNameList = ref([]);
    const paperScoreList = ref([]);
    const paperApprovalList = ref(null);
    const lineList = ref([]);
    const barList = ref([]);
    const sumList = ref([]);
    const singleList = ref([]);
    const manyList = ref([]);
    const fillList = ref([]);
    const answerList = ref([]);

    const findByStudentIdPaperStudent = async () => {
      const result = await findByStudentIdPaperStudentService(studentId, courseId);
      paperList.value = result.data.data;
    };

    const getScoreByStudentId = async () => {
      const result = await getScoreByStudentIdService(studentId, courseId);
      paperScoreList.value = result.data.data;
      total.value = result.data.data.length;
    };

    const getPaperByStudentId = async () => {
      const result = await getPaperByStudentIdService(studentId, courseId);
      paperApprovalList.value = result.data.data;
    };

    const getScoreList = () => {
      paperList.value.forEach((paper) => {
        const id = paper["paperApprovalId"];
        const name = paper["paperName"];
        paperNameList.value.push(name);
        sumList.value.push(paperScoreList.value[id][0]);
        singleList.value.push(paperScoreList.value[id][1]);
        manyList.value.push(paperScoreList.value[id][2]);
        fillList.value.push(paperScoreList.value[id][3]);
        answerList.value.push(paperScoreList.value[id][4]);
        barList.value.push(paperApprovalList.value[id][0].point);
        lineList.value.push(paperApprovalList.value[id][0].score);
      });
    };

    const paper = () => {
      var myChart = paperChart;
      var option;

      // option
      option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          data: ["得分[折线]", "得分[柱形]", "总分"],
          textStyle: {
            color: "#606266",
          },
        },
        xAxis: {
          data: paperNameList.value,
          axisLine: {
            lineStyle: {
              color: "#606266",
            },
          },
        },
        yAxis: {
          splitLine: { show: false },
          axisLine: {
            lineStyle: {
              color: "#606266",
            },
          },
        },
        series: [
          {
            name: "得分[折线]",
            type: "line",
            smooth: true,
            showAllSymbol: true,
            symbol: "emptyCircle",
            symbolSize: 15,
            data: barList.value,
          },
          {
            name: "得分[柱形]",
            type: "bar",
            barWidth: 10,
            itemStyle: {
              borderRadius: 5,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#14c8d4" },
                { offset: 1, color: "#748ede" },
              ]),
            },
            data: barList.value,
          },
          {
            name: "总分",
            type: "bar",
            barGap: "-100%",
            barWidth: 10,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "rgba(20,200,212,0.5)" },
                { offset: 0.2, color: "rgba(20,200,212,0.2)" },
                { offset: 1, color: "rgba(20,200,212,0)" },
              ]),
            },
            z: -12,
            data: lineList.value,
          },
          {
            name: "总分",
            type: "pictorialBar",
            symbol: "rect",
            itemStyle: {
              color: "#fff",
            },
            symbolRepeat: true,
            symbolSize: [12, 4],
            symbolMargin: 1,
            z: -10,
            data: lineList.value,
          },
        ],
      };

      option && myChart.setOption(option);
    };

    return {
      studentId,
      click,
      dialogVisible,
      paper,
      paperChart,
      paperNameList,
      sumList,
      singleList,
      manyList,
      fillList,
      answerList,
      barList,
      lineList,
      total,
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
