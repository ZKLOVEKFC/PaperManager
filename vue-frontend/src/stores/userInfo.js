import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useUserInfoStore = defineStore(
    //store��
    'userInfo',
    //store����,����״̬�������
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
    //������
    {persist:true})




























