<script setup>
import { ref, nextTick } from 'vue'
import { Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import dayjs from 'dayjs' // 导入 dayjs

// API 服务导入
import {
  articleCategoryListService,
  articleListService,
  articleAddService,
  articleUpdateService,
  articleDeleteService,
  articleGenerateInnovationPointsService // <--- 导入新增的 API 服务
} from '@/api/article.js'
import { useTokenStore } from '@/stores/token.js' // Token Store

// =======================
// Refs and Reactive Variables
// =======================

// 搜索区
const categorys = ref([]) // 文章分类列表
const categoryId = ref('') // 搜索：选中的分类id
const state = ref('') // 搜索：选中的发布状态

// 表格和分页
const articles = ref([]) // 文章列表
const pageNum = ref(1)   // 当前页
const total = ref(0)     // 总条数
const pageSize = ref(10) // 每页条数

// 抽屉和表单
const visibleDrawer = ref(false) // 控制抽屉显示
const isEditing = ref(false)   // 标记是否为编辑状态
const quillEditorRef = ref(null); // Quill 编辑器实例引用
const isGeneratingPoints = ref(false); // AI 生成按钮加载状态

// 抽屉核心数据模型
const articleModel = ref({
  id: '',
  title: '',
  categoryId: '',
  coverImg: '',
  content: '',       // 正文/笔记
  abstractContent: '', // 摘要
  innovationPoints: '', // AI创新点
  state: ''
})

const tokenStore = useTokenStore() // Token Store 实例

// =======================
// Functions
// =======================

// --- 数据获取 ---

// 获取文章分类列表
const articleCategoryList = async () => {
  try {
    let result = await articleCategoryListService();
    if (result.code === 0) {
      categorys.value = result.data;
    } else {
      ElMessage.error(result.message || '获取分类失败');
    }
  } catch (error) {
    console.error("获取文章分类失败:", error);
    // ElMessage.error('获取分类列表异常'); // 可选，看拦截器是否处理
  }
}

// 获取文章列表数据
const articleList = async () => {
  let params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    categoryId: categoryId.value || null, // 空字符串转 null
    state: state.value || null // 空字符串转 null
  }
  try {
    let result = await articleListService(params);
    if (result.code === 0) {
      total.value = result.data.total;
      articles.value = result.data.items;

      // 附加 categoryName 到列表项 (优化写法)
      const categoryMap = categorys.value.reduce((map, cat) => {
        map[cat.id] = cat.categoryName;
        return map;
      }, {});
      articles.value.forEach(article => {
        article.categoryName = categoryMap[article.categoryId] || '未知分类';
        // 注意：确保 articles 列表包含 abstractContent 和 innovationPoints 字段
      });
    } else {
      ElMessage.error(result.message || '获取文章列表失败');
    }
  } catch (error) {
    console.error("获取文章列表失败:", error);
    // ElMessage.error('获取文章列表异常');
  }
}

// --- 抽屉操作 ---

// 打开添加抽屉
const showAddDrawer = () => {
  onDrawerClose(); // 先重置表单和状态
  isEditing.value = false;
  visibleDrawer.value = true;
}

// 打开编辑抽屉
const showEditDrawer = (row) => {
  isEditing.value = true;
  // 使用深拷贝或对象扩展符复制数据，确保包含新字段
  articleModel.value = { ...row };
  visibleDrawer.value = true;
  // 注意：Quill 编辑器的内容会在 v-model 更新时自动加载，
  // 但如果遇到问题，可能需要 nextTick + setContents
}

// 抽屉关闭时的清理
const onDrawerClose = () => {
  // 重置表单数据
  articleModel.value = {
    id: '', title: '', categoryId: '', coverImg: '',
    content: '', abstractContent: '', innovationPoints: '', state: ''
  };
  isEditing.value = false; // 重置编辑状态
  isGeneratingPoints.value = false; // 重置 AI 按钮加载状态

  // 清理 Quill 编辑器内容
  nextTick(() => {
    const editorInstance = quillEditorRef.value;
    if (editorInstance) {
      const quill = editorInstance.getQuill ? editorInstance.getQuill() : editorInstance;
      if (quill && typeof quill.setContents === 'function') {
        quill.setContents([]);
        console.log('Quill editor content cleared.');
      } else {
        console.warn("无法获取 Quill 实例或 setContents 方法");
        articleModel.value.content = ''; // 备用方案
      }
    } else {
      // 首次打开抽屉时 ref 可能还没准备好，这是正常的
      // console.warn("无法获取 Quill 编辑器实例 (quillEditorRef)");
    }
  });
}

// --- 表单操作 ---

// 处理封面上传成功
const uploadSuccess = (result) => {
  if (result.code === 0 && result.data) {
    articleModel.value.coverImg = result.data;
    ElMessage.success('封面上传成功');
  } else {
    ElMessage.error(result.message || '封面上传失败');
  }
}

// AI 生成创新点按钮点击处理
const handleGeneratePoints = async () => {
  const abstract = articleModel.value.abstractContent?.trim(); // Optional chaining and trim
  if (!abstract) {
    ElMessage.warning('请先输入有效的文章摘要内容');
    return;
  }
  if (abstract.length < 200 || abstract.length > 500) { // 前端简单校验
    ElMessage.warning('摘要字数需在 200 到 500 字之间');
    return;
  }

  isGeneratingPoints.value = true;
  articleModel.value.innovationPoints = 'AI 正在努力生成中...';

  try {
    const payload = { abstractText: abstract };
    const result = await articleGenerateInnovationPointsService(payload);

    if (result.code === 0) {
      articleModel.value.innovationPoints = result.data;
      ElMessage.success('创新点生成成功！');
    } else {
      articleModel.value.innovationPoints = `生成失败: ${result.message || '未知错误'}`;
      ElMessage.error(result.message || '创新点生成失败');
    }
  } catch (error) {
    console.error("调用 AI 生成创新点失败:", error);
    articleModel.value.innovationPoints = '生成失败，请检查网络或联系管理员。';
    // 可以在这里显示更具体的错误信息给用户，如果需要的话
    // ElMessage.error('请求失败，请重试');
  } finally {
    isGeneratingPoints.value = false;
  }
}

// 提交文章（添加/修改）
const submitArticle = async (clickState) => {
  articleModel.value.state = clickState;
  console.log('Submitting article:', articleModel.value); // 提交前打印数据

  try {
    let result;
    if (isEditing.value) {
      if (!articleModel.value.id) {
        ElMessage.error('编辑错误：文章ID丢失');
        return;
      }
      result = await articleUpdateService(articleModel.value); // 使用 PATCH 或 PUT (取决于你 API 定义)
      ElMessage.success(result.message || '文章修改成功');
    } else {
      result = await articleAddService(articleModel.value);
      ElMessage.success(result.message || '文章添加成功');
    }

    visibleDrawer.value = false;
    articleList(); // 刷新列表

  } catch (error) {
    console.error("提交文章失败:", error);
    // 通常错误由拦截器处理，这里可以不加 ElMessage.error
  }
}

// --- 表格操作 ---

// 删除文章
const deleteArticle = (row) => {
  ElMessageBox.confirm('确定要删除这篇文章吗?', '提示', { /* ... */ })
      .then(async () => {
        try {
          await articleDeleteService(row.id);
          ElMessage.success('删除成功');
          // 优化：如果删除的是最后一页的唯一一条，页码可能需要向前调整
          if (articles.value.length === 1 && pageNum.value > 1) {
            pageNum.value--;
          }
          articleList(); // 刷新列表
        } catch (error) {
          console.error("删除文章失败:", error);
        }
      })
      .catch(() => { ElMessage.info('取消删除'); });
}

// 格式化日期时间
const formatDateTime = (row, column, cellValue) => {
  return cellValue ? dayjs(cellValue).format('YYYY-MM-DD HH:mm:ss') : '';
}

// --- 分页处理 ---
const onSizeChange = (size) => {
  pageSize.value = size;
  pageNum.value = 1; // 页大小改变时，通常回到第一页
  articleList();
}
const onCurrentChange = (num) => {
  pageNum.value = num;
  articleList();
}

// =======================
// Initial Load
// =======================
articleCategoryList();
articleList();

</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span style="font-family: SimHei, 黑体; font-size: 25px;">文章管理</span>
        <div class="extra">
          <el-button type="primary" :icon="Plus" @click="showAddDrawer">添加文章</el-button>
        </div>
      </div>
    </template>

    <el-form inline>
      <el-form-item label="文章分类：" style="width: 300px">
        <el-select placeholder="请选择" v-model="categoryId" clearable style="width: 100%">
          <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态：" style="width: 300px">
        <el-select placeholder="请选择" v-model="state" clearable style="width: 100%">
          <el-option label="已发布" value="已发布" />
          <el-option label="草稿" value="草稿" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="articleList()">搜索</el-button>
        <el-button @click="categoryId=''; state=''; articleList()">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="articles" style="width: 100%" v-loading="loading"> <el-table-column label="文章标题" width="400" prop="title" show-overflow-tooltip>
      <template #default="{ row }">
        <el-link type="primary" :underline="false">{{ row.title }}</el-link>
      </template>
    </el-table-column>
      <el-table-column label="分类" prop="categoryName" width="150"></el-table-column>
      <el-table-column label="摘要" prop="abstractContent" show-overflow-tooltip></el-table-column> <el-table-column label="AI创新点" prop="innovationPoints" show-overflow-tooltip></el-table-column> <el-table-column label="发表时间" prop="createTime" :formatter="formatDateTime" width="180"></el-table-column>
      <el-table-column label="状态" prop="state" width="100"></el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="showEditDrawer(row)" title="编辑"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="deleteArticle(row)" title="删除"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无数据" />
      </template>
    </el-table>

    <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[3, 5, 10, 15, 20]"
        layout="jumper, total, sizes, prev, pager, next"
        background
        :total="total"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
        v-if="total > 0"
    />

    <el-drawer
        v-model="visibleDrawer"
        :title="isEditing ? '修改文章' : '添加文章'"
        direction="rtl"
        size="50%"
        @close="onDrawerClose"
    >
      <el-form :model="articleModel" label-width="100px" style="padding-right: 20px;">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="articleModel.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="文章分类" prop="categoryId">
          <el-select placeholder="请选择" v-model="articleModel.categoryId" style="width: 100%;">
            <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="文章封面" prop="coverImg">
          <el-upload
              class="avatar-uploader"
              :auto-upload="true"
              :show-file-list="false"
              action="/api/upload"
              name="file"
              :headers="{'Authorization': tokenStore.token}"
              :on-success="uploadSuccess"
          >
            <img v-if="articleModel.coverImg" :src="articleModel.coverImg" class="avatar" alt="封面"/>
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="文章摘要" prop="abstractContent">
          <el-input
              v-model="articleModel.abstractContent"
              :rows="6" type="textarea"
              placeholder="请输入摘要内容 (200-500字)"
              maxlength="500" show-word-limit
          />
        </el-form-item>

        <el-form-item label="AI分析">
          <el-button type="primary" @click="handleGeneratePoints" :loading="isGeneratingPoints">
            生成创新点
          </el-button>
          <span style="margin-left: 10px; color: #999; font-size: 12px;">(基于上方摘要内容)</span>
        </el-form-item>

        <el-form-item label="创新点" prop="innovationPoints">
          <el-input
              v-model="articleModel.innovationPoints"
              :rows="4" type="textarea"
              placeholder="点击上方按钮由 AI 生成，生成后可按需修改"
          />
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <div class="editor">
            <quill-editor
                ref="quillEditorRef"
                theme="snow"
                v-model:content="articleModel.content"
                contentType="html"
                placeholder="输入文章正文或其他笔记..."
            >
            </quill-editor>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitArticle('已发布')">发布</el-button>
          <el-button type="info" @click="submitArticle('草稿')">存为草稿</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="flex: auto">
          <el-button @click="visibleDrawer = false">取消</el-button>
        </div>
      </template>
    </el-drawer>

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

/* 基本保持不变，确保样式应用 */
.avatar-uploader {
  :deep() { // 使用 :deep() 穿透 scoped 样式
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
      &:hover {
        border-color: var(--el-color-primary);
      }
    }
    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
    }
  }
}

.editor {
  width: 100%;
  :deep(.ql-editor) {
    min-height: 200px;
  }
}
</style>