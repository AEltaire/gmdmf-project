
// main.js
import { createApp } from "vue";

import App from "./App.vue";
const app = createApp(App);

// 使用 vuestic-ui
import { createVuestic, createIconsConfig } from "vuestic-ui";
import "vuestic-ui/css";
import 'vuestic-ui/styles/essential.css';
import 'vuestic-ui/styles/typography.css';
import '@vuestic/ag-grid-theme'

import ElementPlus from "element-plus"; // 导入 element-plus
import "element-plus/dist/index.css"; // 导入 element-plus 样式
import locale from 'element-plus/dist/locale/zh-cn.js'  // 导入中文语言包

import { createPinia } from "pinia";  // 导入 Pinia
import { createPersistedState } from 'pinia-persistedstate-plugin'

const pinia = createPinia();  // 创建 Pinia 实例
const persist = createPersistedState()
pinia.use(persist)
app.use(pinia)  // 挂载 Pinia

// index.js 文件可以省略书写，默认导入
import router from '@/router/index.js'
// 挂载 router
app.use(router);

app.use(createVuestic())
app.use(ElementPlus, { locale }); // 使用 element-plus

import store from "@/stores/store";
app.use(store)

// vuestic-ui
app.use(
  createVuestic({
    config: {

      icons: createIconsConfig({
        aliases: [
          {
            name: "bell",
            color: "#FFD43A",
            to: "fa4-bell",
          },
          {
            name: "ru",
            to: "flag-icon-ru small",
          },
        ],
        fonts: [
          {
            name: "fa4-{iconName}",
            resolve: ({ iconName }) => ({ class: `fa fa-${iconName}` }),
          },
          {
            name: "flag-icon-{countryCode} {flagSize}",
            resolve: ({ countryCode, flagSize }) => ({
              class: `flag-icon flag-icon-${countryCode} flag-icon-${flagSize}`,
            }),
          },
        ],
      }),
    },
  })
)

app.mount("#app");