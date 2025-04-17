<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { ref } from 'vue'
import {ElMessage} from 'element-plus'
const categorys = ref()

//声明一个异步函数
import {articleCategoryListService, articleCategoryAddService, articleCategoryUpdateService, articleCategoryDeleteService} from '@/api/article.js'
const articleCategoryList = async () => {
  let result = await articleCategoryListService()   // 调用接口
  categorys.value = result.data                     //接收结果
}
//调用方法
articleCategoryList()

//控制添加分类弹窗
const dialogVisible = ref(false)

//添加分类数据模型
const categoryModel = ref({
  categoryName: '',
  categoryAlias: ''
})
//添加分类表单校验
const rules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
  ],
  categoryAlias: [
    { required: true, message: '请输入分类别名', trigger: 'blur' },
  ]
}

//调用接口，添加表单
const addCategory = async () => {
  //调用接口
  let result = await articleCategoryAddService(categoryModel.value)
  ElMessage.success(result.msg ? result.msg : '添加成功')

  //添加成功以后需要刷新页面，这列直接调用获取文章分类的函数
  articleCategoryList()
  //刷新后,抹去弹窗
  dialogVisible.value = false
}
//定义变量，控制弹窗标题的展示
const title = ref('')

//定义弹窗显示方法
const showDialog = (row) => {
  dialogVisible.value = true; title.value='编辑分类'
  //数据拷贝,展示模型的数据
  categoryModel.value = {
    categoryName: row.categoryName,
    categoryAlias: row.categoryAlias,
    //扩展id属性，需要传给后台，完成分类的修改
    id: row.id
  }
}

//编辑分类
const updateCategory = async () => {
  //调用接口
  let result = await articleCategoryUpdateService(categoryModel.value)
  ElMessage.success(result.msg ? result.msg : '修改目录成功')

  //添加成功以后需要刷新页面，这列直接调用获取文章分类的函数
  articleCategoryList()
  //刷新后,抹去弹窗
  dialogVisible.value = false
}
  //清空模型的数据
  const clearModelData = () => {
  categoryModel.value = {
    categoryName: '',
    categoryAlias: ''}
  }

//删除分类
//展示一个由element-plus提供的确认框,提示用户
import {ElMessageBox} from 'element-plus'
const deleteCategory = (row)=>{
  ElMessageBox.confirm(
      '分类将被删除，是否继续?',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        //调用删除目录接口
        let result = await articleCategoryDeleteService(row.id)
        ElMessage({
          type: 'success',
          message: '删除成功',})
        //刷新列表
        articleCategoryList()
      })
      .catch(
          () => {
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
      }
      )
}
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span style="font-family: SimHei, 黑体; font-size: 20px;">文章分类</span>
        <div class="extra">
          <el-button type="primary" @click="dialogVisible = true; title='添加分类';clearModelData()">添加分类</el-button>
        </div>
      </div>
    </template>
    <el-table :data="categorys" style="width: 100%">
      <el-table-column label="序号" width="100" type="index"> </el-table-column>
      <el-table-column label="分类名称" prop="categoryName"></el-table-column>
      <el-table-column label="分类别名" prop="categoryAlias"></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="showDialog(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="deleteCategory(row)"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>

    <!-- 添加分类弹窗 -->
    <el-dialog v-model="dialogVisible" :title="title" width="30%">
      <el-form :model="categoryModel" :rules="rules" label-width="100px" style="padding-right: 30px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryModel.categoryName" minlength="1" maxlength="10"></el-input>
        </el-form-item>
        <el-form-item label="分类别名" prop="categoryAlias">
          <el-input v-model="categoryModel.categoryAlias" minlength="1" maxlength="15"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="title=='添加分类'?addCategory():updateCategory()"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>

  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  min-height: 100%;
  box-sizing: border-box;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>