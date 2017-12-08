package me.robbie.limiter;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.atomic.AtomicInteger;

//http://blog.csdn.net/cloud_ll/article/details/43602325
public class RateLimiterDemo {
    // 每秒不超过100个任务被提交
    RateLimiter limiter = RateLimiter.create(100);

    AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) {
        //不限流
        testNoRateLimiter();
        //限流 每秒100个并发，超过的部分丢掉
        testWithRateLimiter();
    }

    public static void testNoRateLimiter() {
        Long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            System.out.println("call execute.." + i);

        }
        Long end = System.currentTimeMillis();

        System.out.println(end - start);

    }

    public static void testWithRateLimiter() {

        final RateLimiterDemo demo = new RateLimiterDemo();

        Long start = System.currentTimeMillis();

        //总请求数
        for (int i = 0; i < 200; i++) {
            final int num = i;

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    demo.doAction(num);
                }
            });

            System.out.println("thread:" + thread.getId());
            thread.start();

            try {
                Thread.sleep(5); //每隔5毫秒一个请求。 一秒200个并发
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Long end = System.currentTimeMillis();

        System.out.println(end - start);

    }


    public boolean doAction(int i) {

        if (limiter.tryAcquire()) {
            System.out.println("call execute.." + i + "，id=" + Thread.currentThread().getId() + ",success count =" + atomicInteger.addAndGet(1));

            try {
                // 做业务逻辑预计50毫秒做完
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        // 抛弃过载请求
        System.out.println("not execute.." + i);
        return false;
    }

}