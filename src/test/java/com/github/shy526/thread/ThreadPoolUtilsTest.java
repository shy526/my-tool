package com.github.shy526.thread;

import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class ThreadPoolUtilsTest {

    @Test
    public void getThreadPool() {
        ExecutorService threadPool = ThreadPoolUtils.getThreadPool();
        CountDownLatch countDown = new CountDownLatch(1000);
        for (int i=0;i<1000;i++){
            threadPool.execute(()->{
                System.out.println(Thread.currentThread().getName());
                countDown.countDown();
            });
        }
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSingleExecutor() {
        ExecutorService single = ThreadPoolUtils.getSingleExecutor("single");
        CountDownLatch countDown = new CountDownLatch(5);
        for (int i=0;i<10;i++){
            int x=i;
            single.execute(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+":"+x);
                countDown.countDown();
            });
        }
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getScheduledExecutor() {
        ScheduledExecutorService scheduled = ThreadPoolUtils.getScheduledExecutor("scheduled", 1, true);
        CountDownLatch countDown = new CountDownLatch(3);
        scheduled.scheduleAtFixedRate(()->{
            long name=System.currentTimeMillis();
            System.out.println("定时任务开始:" +name );
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDown.countDown();
            System.out.println("定时任务结束:" +name +"->"+System.currentTimeMillis());
        },0,100, TimeUnit.MILLISECONDS);
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}