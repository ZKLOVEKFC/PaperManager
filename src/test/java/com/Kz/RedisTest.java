//package com.itheima;
//
//import net.sf.jsqlparser.statement.select.KSQLWindow;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import java.util.concurrent.TimeUnit;
//
//import java.sql.Time;
//
//@SpringBootTest         // 单元测试方法执行之前，会先初始化Spring容器
//public class RedisTest {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    public void testSet() {
//        //往Redis中存储一个键值对        StringRedisTemplate
//        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
//        operations.set("username","zhangsan");      //      key==name,        value==zhangsan
//        operations.set("id", "1", 15, TimeUnit.SECONDS);
//    }
//
//    @Test
//    public void testGet() {
//        //从Redis中获取一个键值对
//        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
//        String username = operations.get("username");       //从Redis中获取key为username的值
//        System.out.println(username);
//    }
//}
