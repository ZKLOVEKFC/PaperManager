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
//@SpringBootTest         // ��Ԫ���Է���ִ��֮ǰ�����ȳ�ʼ��Spring����
//public class RedisTest {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    public void testSet() {
//        //��Redis�д洢һ����ֵ��        StringRedisTemplate
//        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
//        operations.set("username","zhangsan");      //      key==name,        value==zhangsan
//        operations.set("id", "1", 15, TimeUnit.SECONDS);
//    }
//
//    @Test
//    public void testGet() {
//        //��Redis�л�ȡһ����ֵ��
//        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
//        String username = operations.get("username");       //��Redis�л�ȡkeyΪusername��ֵ
//        System.out.println(username);
//    }
//}
