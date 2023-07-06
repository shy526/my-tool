package com.github.shy526.date;

import com.github.shy526.http.HttpClientFactory;
import com.github.shy526.http.HttpClientProperties;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
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



    @Test
    public void http(){
        HttpClientProperties httpClientProperties = new HttpClientProperties();
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(httpClientProperties);
        String url="https://steamcommunity.com/market/listings/570/Exalted%20Demon%20Eater";
        HttpResult httpResult = httpClientService.get(url);
        String entityStr = httpResult.getEntityStr();
        System.out.println("entityStr = " + entityStr);

    }
}