package com.Kz.service;

import com.Kz.pojo.User;

public interface UserService {

    User findByUserName(String username);

    //注册
    void register(String username, String password);

    //更新信息
    void update(User user);

    //更新头像
    void updateAvatar(String avatarUrl);

    //更新密码
    void updatePwd(String newPwd);
}
