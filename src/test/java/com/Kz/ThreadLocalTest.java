//package com.itheima;
//
//import org.junit.jupiter.api.Test;
//
//public class ThreadLocalTest {
//
//    @Test
//    public void testThreadLocalSetAndGet(){
//        //提供一个ThreadLocal对象
//        ThreadLocal tl = new ThreadLocal();
//
//        //开启两个线程
//        new Thread(()->{
//            tl.set("蓝色线程");
//            System.out.println(Thread.currentThread().getName()+"——"+"已被设置为"+tl.get());
//            System.out.println(Thread.currentThread().getName()+"——"+"已被设置为"+tl.get());
//            System.out.println(Thread.currentThread().getName()+"——"+"已被设置为"+tl.get());
//        },"小蓝").start();
//
//        //开启第二个线程
//        new Thread(()->{
//            tl.set("红色线程");
//            System.out.println(Thread.currentThread().getName()+"——"+"已被设置为"+tl.get());
//            System.out.println(Thread.currentThread().getName()+"——"+"已被设置为"+tl.get());
//            System.out.println(Thread.currentThread().getName()+"——"+"已被设置为"+tl.get());
//        },"小红").start();
//
//    }
//}
