import request from "@/utils/request";
//导入store
import {useTokenStore} from '@/stores/token.js'
//文章分类列表查询接口
export const articleCategoryListService = ()=>{
    //发送请求前先获取状态
    const tokenStore = useTokenStore();
    // tokenStore.token为pinia存储的token，所以要传给请求头中，另外在pinia中定义的响应式数据都无需.value，所以此处token无需.value
    return request.get('/category')
}

//添加文章分类
export const articleCategoryAddService = (categoryData)=>{
    return request.post('/category',categoryData)
}

//修改文章分类
export const articleCategoryUpdateService = (categoryData)=>{
    return request.put('/category',categoryData)
}

// 删除文章分类
export const articleCategoryDeleteService = (id) => {
    // id 仍然作为查询参数传递
    return request.delete('/category?id='+id);
}

//文章列表查询
export const articleListService = (params)=>{
    return request.get('/article',{params:params})
}

//文章添加
export const articleAddService = (articleData)=>{
    return request.post('/article',articleData)
}
//文章修改
export const articleUpdateService = (articleData)=>{
    return request.patch('/article',articleData)
}

//文章删除
export const articleDeleteService = (id)=>{
    return request.delete('/article?id='+id)
}

// 新增：调用后端生成创新点的接口
export const articleGenerateInnovationPointsService = (abstractData) => {
    return request.post('/article/ai/generateInnovationPoints', abstractData); // <--- 修改这里
}




















