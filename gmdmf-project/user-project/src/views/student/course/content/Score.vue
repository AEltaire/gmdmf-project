<script lang="ts" setup>
import { nextTick, onMounted, ref } from "vue";
import { useCourseStore } from "@/stores/course";
import {
  getScoreByStudentIdService,
  getPaperByStudentIdService,
} from "@/api/Data";
import { findByStudentIdPaperStudentService } from "@/api/PaperStudent";
import * as echarts from "echarts";
import { useUserInfoStore } from "@/stores/userInfo";
const courseId = useCourseStore().course.id;
const studentId = useUserInfoStore().userInfo.id;

let loadChart = null,
  scoreChart = null,
  paperChart = null;

onMounted(async () => {
  await loading();
  await findByStudentIdPaperStudent();
  await getScoreByStudentId();
  await getPaperByStudentId();
  await getScoreList();

  await setTimeout(async () => {
    load.value = false;

    if (total.value > 0) {
      nextTick(async () => {
        scoreChart = echarts.init(document.getElementById("score"));
        paperChart = echarts.init(document.getElementById("paper"));
        await score();
        await paper();
        window.addEventListener("resize", function () {
          this.setTimeout(() => {
            loadChart.resize();
            scoreChart.resize();
            paperChart.resize();
          }, 320);
        });
      });
    }
  }, 1200);
});

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
const value = 0;
const items = ["score", "paper"];
const load = ref(true);

const getScoreByStudentId = async () => {
  const result = await getScoreByStudentIdService(studentId, courseId);
  paperScoreList.value = result.data.data;
  try {
    if (result.data.data.length == 0) {
      total.value = 0;
    } else {
      total.value = 1;
    }
  } catch (e) {
    null;
  }
};

const findByStudentIdPaperStudent = async () => {
  const result = await findByStudentIdPaperStudentService(studentId, courseId);
  paperList.value = result.data.data;
};

const getPaperByStudentId = async () => {
  const result = await getPaperByStudentIdService(studentId, courseId);
  paperApprovalList.value = result.data.data;
};

const getScoreList = () => {
  if (paperScoreList.value.length == 0) return;

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

const loading = () => {
  loadChart = echarts.init(document.getElementById("load"));

  var option = {
    graphic: {
      elements: [
        {
          type: "text",
          left: "center",
          top: "center",
          style: {
            text: "Axier",
            fontSize: 80,
            fontWeight: "bold",
            lineDash: [0, 200],
            lineDashOffset: 0,
            fill: "transparent",
            stroke: "#000",
            lineWidth: 1,
          },
          keyframeAnimation: {
            duration: 1200,
            loop: true,
            keyframes: [
              {
                percent: 0.7,
                style: {
                  fill: "transparent",
                  lineDashOffset: 200,
                  lineDash: [200, 0],
                },
              },
              {
                percent: 0.8,
                style: {
                  fill: "transparent",
                },
              },
              {
                percent: 1,
                style: {
                  fill: "black",
                },
              },
            ],
          },
        },
      ],
    },
  };
  option && loadChart.setOption(option);
};

const score = () => {
  var myChart = scoreChart;
  var option;

  option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {},
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: [
      {
        type: "category",
        data: paperNameList.value,
      },
    ],
    yAxis: [
      {
        type: "value",
      },
    ],
    series: [
      {
        name: "得分",
        type: "bar",
        data: sumList.value,
        emphasis: {
          focus: "series",
        },
        markLine: {
          lineStyle: {
            type: "dashed",
            cap: "round",
          },
          symbol: "none",
          label: {
            show: true,
            position: "end",
            formatter: "{b}",
          },
          data: [
            {
              name: "平均线",
              // 支持 'average', 'min', 'max'
              type: "average",
            },
          ],
        },
      },
      {
        name: "单选",
        type: "bar",
        barWidth: 5,
        stack: "得分",
        emphasis: {
          focus: "series",
        },
        data: singleList.value,
      },
      {
        name: "多选",
        type: "bar",
        stack: "得分",
        emphasis: {
          focus: "series",
        },
        data: manyList.value,
      },
      {
        name: "填空",
        type: "bar",
        stack: "得分",
        emphasis: {
          focus: "series",
        },
        data: fillList.value,
      },
      {
        name: "简答",
        type: "bar",
        stack: "得分",
        emphasis: {
          focus: "series",
        },
        data: answerList.value,
      },
    ],
  };

  option && myChart.setOption(option);
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
</script>

<template>
  <!-- 加载 -->
  <VaCard
    stripe
    stripe-color="primary"
    v-if="load"
    style="width: 100%; height: 82vh"
  >
    <VaCardContent>
      <div id="load" style="width: 100%; height: 82vh"></div>
    </VaCardContent>
  </VaCard>

  <VaCard
    stripe
    stripe-color="primary"
    v-show="total == 0 && load == false"
    style="width: 100%; height: 82vh"
  >
    <VaCardContent>
      <el-empty v-if="total == 0" :image-size="400" />
    </VaCardContent>
  </VaCard>
  <!-- 内容 -->
  <VaCarousel
    v-show="total > 0 && load == false"
    style="width: 100%; height: 82vh"
    v-model="value"
    :items="items"
    arrows
    indicators
  >
    <template #indicator="{ index }">
      <span style="display: none"></span>
    </template>
    <template #default="{ item }">
      <div style="width: 100%; height: 82vh">
        <VaCard
          v-if="item == 'score'"
          stripe
          stripe-color="primary"
          style="width: 100%; height: 82vh"
        >
          <VaCardContent>
            <div
              id="score"
              style="width: 100%; height: 82vh; padding: 20px 36px 4px 36px"
            ></div>
          </VaCardContent>
        </VaCard>
        <VaCard
          v-else-if="item == 'paper'"
          stripe
          stripe-color="primary"
          style="width: 100%; height: 82vh"
        >
          <VaCardContent>
            <div
              id="paper"
              style="width: 100%; height: 82vh; padding: 20px 0px 0px 0px"
            ></div>
          </VaCardContent>
        </VaCard>
      </div>
    </template>
  </VaCarousel>
</template>

<style lang="scss" scoped>
.progress-item {
  padding-top: 6px;
}
#load {
  transition: all 0.5s ease-in-out;
}
</style>
