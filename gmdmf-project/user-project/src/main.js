import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'

// element-plus 
import ElementPlus from "element-plus"; // 导入 element-plus
import "element-plus/dist/index.css"; // 导入 element-plus 样式
import locale from "element-plus/dist/locale/zh-cn.js"; // 导入中文语言包
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

// pinia
import { createPinia } from "pinia";
import { createPersistedState } from "pinia-persistedstate-plugin";

// router
import router from "@/router";

// jquery
import $ from "jquery";
import jQuery from 'jquery';

window.$ = $;
window.jQuery = jQuery

// 使用 vuestic-ui
import { createVuestic, createIconsConfig } from "vuestic-ui";
import "vuestic-ui/css";
import '@vuestic/ag-grid-theme'
import 'vuestic-ui/styles/essential.css';
import 'vuestic-ui/styles/typography.css';
import "material-design-icons-iconfont/dist/material-design-icons.min.css";
import { registerVuesticWebComponents } from 'vuestic-ui/web-components'
registerVuesticWebComponents()

const app = createApp(App);

// element-plus 
app.use(ElementPlus, { locale });
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// pinia
const pinia = createPinia();
const persist = createPersistedState();
pinia.use(persist);
app.use(pinia);

// router
app.use(router);

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

app.mount('#app')
