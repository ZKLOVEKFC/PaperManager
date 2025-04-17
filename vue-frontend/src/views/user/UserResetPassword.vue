<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>重置密码</span>
      </div>
    </template>
    <el-row>
      <el-col :span="12">
        <el-form
            :model="pwdForm"
            :rules="rules"
            ref="formRef"
            label-width="100px"
            size="large"
        >
          <el-form-item label="原密码" prop="old_pwd">
            <el-input v-model="pwdForm.old_pwd" type="password" placeholder="请输入原密码" show-password></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new_pwd">
            <el-input v-model="pwdForm.new_pwd" type="password" placeholder="请输入新密码" show-password></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="re_pwd">
            <el-input v-model="pwdForm.re_pwd" type="password" placeholder="请再次输入新密码" show-password></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword">修改密码</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
// 确保导入了更新后的 userPasswordUpdateService
import { userPasswordUpdateService } from '@/api/user.js';
import { useTokenStore } from '@/stores/token.js';
import { useUserInfoStore } from '@/stores/userInfo.js';
import { useRouter } from 'vue-router';

const formRef = ref(null);
// 确保 pwdForm 的键名与后端 Map<String, String> params 期望的键名一致
const pwdForm = ref({
  old_pwd: '',
  new_pwd: '',
  re_pwd: '',
});

const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const router = useRouter();

// 自定义校验规则：新密码不能和原密码相同
const validatePasswordSame = (rule, value, callback) => {
  if (value && value === pwdForm.value.old_pwd) { // 添加 value 存在判断
    callback(new Error('新密码不能和原密码相同!'));
  } else {
    callback(); // 校验通过
  }
};

// 自定义校验规则：确认密码必须和新密码相同
const validatePasswordConfirm = (rule, value, callback) => {
  if (value !== pwdForm.value.new_pwd) {
    callback(new Error('两次输入的新密码不一致!'));
  } else {
    callback(); // 校验通过
  }
};

// 校验规则：确保 prop 值与 pwdForm 键名匹配
const rules = ref({
  old_pwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' },
  ],
  new_pwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' },
    // 使用自定义校验规则，在 blur 时触发
    { validator: validatePasswordSame, trigger: 'blur' }
  ],
  re_pwd: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    // 确认密码的校验最好也在 blur 时触发，或者在提交时由 validatePasswordConfirm 确保
    { validator: validatePasswordConfirm, trigger: 'blur' }
  ],
});

// 修改密码处理函数
const updatePassword = async () => {
  // 1. 校验表单
  try {
    await formRef.value.validate();
  } catch (validationError) {
    // 如果校验不通过，validate() 会 reject，这里可以捕获或让其阻止后续执行
    console.log("表单校验失败:", validationError);
    return; // 阻止提交
  }

  // 2. 调用接口 (发送 JSON 数据)
  try {
    // 直接传递 pwdForm.value，它包含 { old_pwd, new_pwd, re_pwd }
    await userPasswordUpdateService(pwdForm.value);
    ElMessage.success('密码修改成功，请重新登录');

    // 清除本地存储的 token 和用户信息
    tokenStore.removeToken();
    userInfoStore.removeInfo();

    // 跳转到登录页
    router.push('/login');

  } catch (error) {
    console.error("密码更新失败 (API 调用):", error);
  }
};

// 重置表单
const resetForm = () => {
  formRef.value.resetFields(); // 清空表单并移除校验状态
};
</script>

<style scoped>
.page-container {
  min-height: 100%;
  box-sizing: border-box;
  padding: 20px;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.el-col {
  max-width: 500px;
}
</style>