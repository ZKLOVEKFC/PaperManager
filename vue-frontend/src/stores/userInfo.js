import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useUserInfoStore = defineStore(
    //store名
    'userInfo',
    //store函数,定义状态相关内容
    ()=>{

        const info = ref({})

        const setInfo = (newInfo)=>{
            info.value = newInfo
        }

        const removeInfo = ()=>{
            info.value = {}
        }
        return {info,setInfo,removeInfo}

},
    //配置项
    {persist:true})




























