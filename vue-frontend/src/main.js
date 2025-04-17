import './assets/main.scss'
import App from './App.vue'
import { createApp } from 'vue'

import ElementPlus from 'element-plus'      //导入element-plus
import 'element-plus/dist/index.css'        //导入element-plus的样式
import router from '@/router'    //导入配置的路由器
import {createPinia} from 'pinia'   //导入pinia
import {createPersistedState} from 'pinia-persistedstate-plugin'        //导入pinia的持久化插件
import locale from 'element-plus/dist/locale/zh-cn'

const app = createApp(App);

const pinia = createPinia()
const persist = createPersistedState()

pinia.use(persist)          //在pinia中添加持久化插件
app.use(pinia)
app.use(router)
app.use(ElementPlus, {locale});
app.mount('#app')
