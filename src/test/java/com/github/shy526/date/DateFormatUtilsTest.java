package com.github.shy526.date;

import com.github.shy526.thread.ThreadPoolUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateFormatUtilsTest {

    @Test
    public void defaultFormat() {
        Date date = new Date();
        for (int i=0;i<=100;i++){
            new Thread(()->{
                String stt = DateFormatUtils.defaultFormat(date);
            }).start();
        }
    }
}