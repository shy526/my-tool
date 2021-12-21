package com.github.shy526.poll;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class SimpleGenericObjPoolTest {
    @Test
    public void testLeaseObject() {
        SimpleGenericObjPool<SimpleDateFormat> poll = new SimpleGenericObjPool<>(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        int max = 100;
        long s = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(max);
        Date date = new Date();
        for (int i = 0; i <= max; i++) {
            new Thread(() -> {
                poll.leaseObject((item) -> {
                    System.out.println(item.format(date));
                });
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println(System.currentTimeMillis() - s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(poll);
    }

    public void testTestLeaseObject() {
    }


}