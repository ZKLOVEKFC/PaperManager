<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

//控制注册与登录表单的显示， 默认显示注册
const isRegister = ref(false)
//定义注册数据模型
const registerData = ref({
    username: '',
    password: '',
    rePassword: ''
})

//定义确认密码校验的函数   (函数中rule为规则、value为值，callback为回调函数)
const checkRePassword = (rule, value, callback) => {
    //如果没有输入确认密码
    if (value == ''){
        callback(new Error('请输入确认密码'))
    }
    //两次密码不一致
    else if (value != registerData.value.password){
        callback(new Error('两次密码不一致'))
    }
    //验证通过
    else {
        callback()
    }
}
//定义注册表单校验规则
const rules = {
  username:[
      {required:true, message:"输入用户名", trigger:'blur'},
      {min:5, max:16, message:'长度为5-16为非空字符', trigger:'blur'},
  ],
  password:[
    {required:true, message:"输入密码", trigger:'blur'},
    {min:5, max:16, message:'长度为5-16为非空字符', trigger:'blur'},
  ],
  rePassword:[
    {validator:checkRePassword, trigger:'blur'}
  ],
}

//调用后台接口,完成注册
import {userRegisterService} from '../api/user.js'    //导入js文件中的注册接口
const register = async () => {
  let result = await userRegisterService(registerData.value);   //异步调用注册接口
  ElMessage.success(result.msg?result.msg:'注册成功')
}
//复用注册表单的数据模型来绑定数据
//表单数据校验
//登录函数login
import {userLoginService} from '../api/user.js'    //导入js文件中的注册接口
//导入路由
import {useRouter} from 'vue-router'
//导入store
import {useTokenStore} from '@/stores/token.js'

const router = useRouter()

const login = async ()=>{
  let result = await userLoginService(registerData.value);   //异步调用登录接口
  ElMessage.success(result.msg?result.msg:'登录成功')

  //将token存储到pinia的store中
  const tokenStore = useTokenStore()
  tokenStore.setToken(result.data)

  //借助路由跳转到首页
  router.push('/')
}
//定义函数,清空数据模型的数据(这样在切换登录/注册时，账户和密码会重置)
const clearRegisterData = ()=>{
  registerData.value = {
    username: '',
    password: '',
    rePassword: ''
  }
}
</script>

<template>
    <el-row class="login-page">
        <el-col :span="12" class="bg"></el-col>
        <el-col :span="6" :offset="3" class="form">
            <!-- 注册表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-if="isRegister"
                     :model="registerData" :rules="rules">
                <!-- 绑定注册数据模型，用rules属性绑定表单参数的校验规则 -->
                <el-form-item>
                    <h1>注册</h1>
                </el-form-item>
                <el-form-item prop="username">
                  <!--用prop属性绑定规则对象中的username对应校验规则-->
                    <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                  <!--绑定rules 规则对象中的password对应校验规则-->

                  <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item prop="rePassword">
                  <!--绑定rules 规则对象中的rePassword对应校验规则-->
                  <el-input :prefix-icon="Lock" type="password" placeholder="请输入再次密码" v-model="registerData.rePassword"></el-input>
                </el-form-item>
                <!-- 注册按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space
                               @click="register">
                        注册
                    </el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = false">
                        ← 返回
                    </el-link>
                </el-form-item>
            </el-form>
            <!-- 登录表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-else
                     :model="registerData" :rules="rules">
                <el-form-item>
                    <h1>登录</h1>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码" v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item class="flex">
                    <div class="flex">
                        <el-checkbox>记住我</el-checkbox>
                        <el-link type="primary" :underline="false">忘记密码？</el-link>
                    </div>
                </el-form-item>
                <!-- 登录按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = true;clearRegisterData()">
                        注册 →
                    </el-link>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
/* 样式 */
.login-page {
    height: 100vh;
    background-color: #fff;

    .bg {
        background: url('src/assets/newlocal.png') no-repeat 60% center / 240px auto,
            url('src/assets/background.png') no-repeat center / cover;
        border-radius: 0 20px 20px 0;
    }

    .form {
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;

        .title {
            margin: 0 auto;
        }

        .button {
            width: 100%;
        }

        .flex {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }
    }
}
</style>