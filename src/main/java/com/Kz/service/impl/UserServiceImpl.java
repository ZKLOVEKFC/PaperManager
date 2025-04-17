package com.Kz.service.impl;
import com.Kz.mapper.UserMapper;
import com.Kz.pojo.User;
import com.Kz.service.UserService;
import com.Kz.utils.Md5Util;
import com.Kz.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByUserName(String username) {
        User u = userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password) {
        //MD5加密
        String md5string = Md5Util.getMD5String(password);
        //添加
        userMapper.add(username, md5string);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String, Object> map =ThreadLocalUtil.get();          //由于avatorUrl请求中并没有传入ID数据，
        // 由于在拦截器中解析的token，也就是用户数据已经放入threadlocal中，
        // 因此从ThreadLocal中导入ID，ThreadLocalUtil.get()是一个map集合,因此创建map接受用户数据
        Integer id = (Integer) map.get("id");              //获得token中的用户id,由于是Intger类型数据，所以要强转一下
        userMapper.updateAvatar(avatarUrl, id);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String, Object> map =ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");              //获得token中的用户id,原理同上
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),id);         //记得传入的是加密后的密码
    }
}
