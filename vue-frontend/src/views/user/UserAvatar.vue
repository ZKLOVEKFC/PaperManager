<script setup>
import { Plus, Upload } from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {ref} from 'vue'
import avatar from '@/assets/default.png'
const uploadRef = ref()

//导入token
import {useTokenStore} from "@/stores/token.js"
const tokenStore = useTokenStore()

//导入pinia获取用户头像，作为修改头像前的默认显示
import {useUserInfoStore} from "@/stores/userInfo.js";
const userInfoStore = useUserInfoStore();
//用户头像地址
const imgUrl = ref(userInfoStore.info.userPic)

//上传成功回调函数
const uploadSuccess = (result) =>{
  imgUrl.value = result.data
}

//修改头像
import {userAvatarUpdateService} from '@/api/user.js'
const updateAvatar = async () => {
  //调用接口
  let result = await userAvatarUpdateService(imgUrl.value)
  ElMessage.success(result.msg?result.msg:'修改头像成功')

  //修改pinia的数据
  userInfoStore.info.userPic = imgUrl.value
}
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>更换头像</span>
      </div>
    </template>
    <el-row>
      <el-col :span="12">
        <el-upload
            ref="uploadRef"
            class="avatar-uploader"
            :show-file-list="false"
            :auto-upload="true"
            :headers="{'Authorization': tokenStore.token}"
            action="/api/upload"
            name="file"
            :on-success="uploadSuccess"
        >

          <img v-if="imgUrl" :src="imgUrl" class="avatar" />
          <img v-else :src="avatar" width="278" />
        </el-upload>
        <br />
        <el-button type="primary" :icon="Plus" size="large"  @click="uploadRef.$el.querySelector('input').click()">
          选择图片
        </el-button>
        <el-button type="success" :icon="Upload" size="large" @click="updateAvatar">
          上传头像
        </el-button>
      </el-col>
    </el-row>
  </el-card>
</template>

<style>
.avatar-uploader {
  width: 278px;
  height: 278px;
  position: relative;

.avatar {
  position: absolute;
  max-width: 100%;
  max-height: 100%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  object-fit: contain;
}
}
</style>