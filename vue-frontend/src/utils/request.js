import axios from 'axios';
import { ElMessage } from 'element-plus'

//使用代理配置解决跨域问题，代理服务器在vite.config.js中配置
const baseURL = '/api';
const instance = axios.create({baseURL});

//导入路由器
import router from "@/router";

// 请求响应拦截器
//导入useTokenStore
import {useTokenStore} from '@/stores/token.js';
instance.interceptors.request.use(
    (config)=>{
        //请求前回调,添加token
        const tokenStore = useTokenStore();
        //判断有没有token,有的话则添加统一的请求头
        if (tokenStore.token){
            config.headers.Authorization = tokenStore.token;
        }
        return config;
    },
    (err)=>{
        //请求失败回调
        Promise.reject(err)
    }
)

// 请求响应拦截器
instance.interceptors.response.use(
    result =>{
        //操作成功
        if (result.data.code == 0){
            return result.data;
        }
        //操作失败
        ElMessage.error(result.data.msg?result.data.msg:'服务异常');
        return Promise.reject(result.data);
    },
    err =>{
        //判断响应状态码，401则未登录，提示请登录并跳转登录页面
        if (err.response.status == 401){
            ElMessage.error('请先登录');
            //跳转到登录页面
            router.push('/login');
        }else {
            ElMessage.error('服务异常');
        }
        return Promise.reject(err);     //异步状态转为失败状态
    }
);

export default instance;