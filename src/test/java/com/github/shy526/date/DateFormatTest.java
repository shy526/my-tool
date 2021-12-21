package com.github.shy526.date;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class DateFormatTest {

    @Test
    public void defaultFormat() {
        int max=100;
        long s=System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(max);
        Date date = new Date();
        for (int i=0;i<=max;i++){
            new Thread(()->{
                String dateStr = DateFormat.format(date,"yyyyMMdd HH:mm:ss");
                String dateSt1= DateFormat.defaultFormat(date);
                System.out.println(dateStr);
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println(System.currentTimeMillis()-s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}