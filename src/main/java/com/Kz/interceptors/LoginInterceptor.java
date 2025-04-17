package com.Kz.interceptors;

import com.Kz.utils.JwtUtil;
import com.Kz.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    //      注入一个redis对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //令牌验证
        String  token = request.getHeader("Authorization");
        try {
            Map<String,Object> claims = JwtUtil.parseToken(token);

            //验证token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);          //      获取redis中的token

            if (redisToken == null){
                throw new RuntimeException("token已失效");     //redis中token已失效，异常抛出被e拦截到，跳转到e方法
            }

            //将业务数据存入LocalThread中       ※※※※※※※※※※※※
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            //设置http状态码401
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空threadLocal中的数据
        ThreadLocalUtil.remove();       //防止内存泄漏
    }
}

