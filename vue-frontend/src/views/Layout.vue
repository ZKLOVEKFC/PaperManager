<script setup>
import {
  Management,
  Promotion,
  UserFilled,
  User,
  Crop,
  EditPen,
  SwitchButton,
  CaretBottom
} from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'

import {userInfoService} from '@/api/user.js'
import {useUserInfoStore} from '@/stores/userInfo.js'
import {useTokenStore} from '@/stores/token.js'

const userInfoStore = useUserInfoStore()

//调用接口，获得用户信息
const getUserInfo = async () => {
  let result = await userInfoService()
  //将获取的数据存储至pinia中
  userInfoStore.setInfo(result.data)
}
getUserInfo()

//条目被点击后触发的函数
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from "element-plus";
const tokenStore = useTokenStore()
const router = useRouter()
const handleCommand = (command) => {
  //判断指令
  if (command === 'logout'){
    // userInfoStore.logout()
    ElMessageBox.confirm(
        '确认要退出码?',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(async () => {
          //退出登录
          //1、清除pinia的信息和token
          tokenStore.removeToken()
          userInfoStore.removeInfo()
          //2、跳转到登录页面
          ElMessage({
            type: 'success',
            message: '退出登录',})

        })
        .catch(
            () => {
              ElMessage({
                type: 'info',
                message: '取消退出',
              })
            }
        )
  }
  else {
    router.push('/user/'+command)
  }
}
</script>

<template>
<!--  element-plus中的容器-->
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
<!--      element-plus的菜单标签-->
      <el-menu active-text-color="#ffd04b" background-color="#232323"  text-color="#fff"
               router>
        <el-menu-item index="/article/category">
          <el-icon>
            <Management />
          </el-icon>
          <span>文章分类</span>
        </el-menu-item>

        <el-menu-item index="/article/manage">
          <el-icon>
            <Promotion />
          </el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-sub-menu >
          <template #title>
            <el-icon>
              <UserFilled />
            </el-icon>
            <span>个人中心</span>
<!--            个人中心的三个子菜单-->
          </template>

          <el-menu-item index="/user/info">
            <el-icon>
              <User />
            </el-icon>
            <span>基本资料</span>
          </el-menu-item>

          <el-menu-item index="/user/avatar">
            <el-icon>
              <Crop />
            </el-icon>
            <span>更换头像</span>
          </el-menu-item>

          <el-menu-item index="/user/resetPassword">
            <el-icon>
              <EditPen />
            </el-icon>
            <span>重置密码</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header>
        <div>用户：<strong>{{userInfoStore.info.username}}</strong></div>
        <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.info.userPic?userInfoStore.info.userPic:avatar" />
                        <el-icon>
                            <CaretBottom />
                        </el-icon>
  </span>
          <!--下拉菜单-->
          <!--command:条目被点击后触发，在事件函数上可以声明一个函数，接受条目对应的指令-->
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
              <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
              <el-dropdown-item command="resetPassword" :icon="EditPen">重置密码</el-dropdown-item>
              <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <!-- 中间区域 -->
      <el-main>
<!--        内容展示区-->
        <router-view></router-view>
      </el-main>
      <!-- 底部区域 -->
      <el-footer>PaperManger期刊协同记录系统 ©2025 Created by Kz</el-footer>
    </el-container>
  </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .el-aside {
    background-color: #232323;

    &__logo {
      height: 150px;
      background: url('src/assets/logoX.png') no-repeat center / 200px auto;
    }

    .el-menu {
      border-right: none;
    }
  }

  .el-header {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-dropdown__box {
      display: flex;
      align-items: center;

      .el-icon {
        color: #999;
        margin-left: 10px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }

  .el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666;
  }
}
</style>