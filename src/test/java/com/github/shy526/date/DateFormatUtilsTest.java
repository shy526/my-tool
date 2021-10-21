package com.github.shy526.date;

import com.github.shy526.thread.ThreadPoolUtils;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class DateFormatUtilsTest {

    @Test
    public void defaultFormat() {
        int max=100;
        CountDownLatch countDownLatch = new CountDownLatch(max);
        Date date = new Date();
        for (int i=0;i<=max;i++){
            new Thread(()->{
                String dateStr = DateFormatUtils.format(date,"yyyyMMdd HH:mm:ss");
                String dateSt1= DateFormatUtils.defaultFormat(date);
                System.out.println(dateStr);
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}