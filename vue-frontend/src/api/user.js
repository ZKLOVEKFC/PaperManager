//导入request.js请求工具
import request from "../utils/request";

//提供调用注册接口的函数
export const userRegisterService = (registerData)=>{

    const params = new URLSearchParams();

    //遍历registerData，将键值对分装到params中
    for(let key in registerData){
        params.append(key,registerData[key])
    }
    //此时键值对已经封装完成，可以发送请求，params为键值对
    return request.post('user/register',params);
}

//提供调用登录接口的函数
export const userLoginService = (loginData)=>{

    const params =new URLSearchParams();
    for (let key in loginData){
        params.append(key,loginData[key])
    }
    return request.post('user/login',params)
}

//获取用户信息
export const userInfoService = ()=>{
    return request.get('user/userInfo')
}

//修改用户信息
export const updateUserInfoService = (userInfoData) =>{
    return request.put('/user/update',userInfoData)
}

//修改头像
export const userAvatarUpdateService = (avatarUrl)=>{
    const params = new URLSearchParams();
    params.append('avatarUrl',avatarUrl)
    return request.patch('/user/updateAvatar',params)
}

// 重置密码
export const userPasswordUpdateService = (passwordData) => {
    return request.patch('/user/updatePwd', passwordData);
}




















