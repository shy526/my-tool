package com.github.shy526.date;

import com.github.shy526.http.*;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DateFormatTest {

    @Test
    public void defaultFormat() {
        int max = 100;
        long s = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(max);
        Date date = new Date();
        for (int i = 0; i <= max; i++) {
            new Thread(() -> {
                String dateStr = DateFormat.format(date, "yyyyMMdd HH:mm:ss");
                String dateSt1 = DateFormat.defaultFormat(date);
                System.out.println(dateStr);
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println(System.currentTimeMillis() - s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void http() {
     /*   HttpClientProperties httpClientProperties = new HttpClientProperties();
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(httpClientProperties);
        String url="https://steamcommunity.com/market/listings/570/Exalted%20Demon%20Eater";
        HttpResult httpResult = httpClientService.get(url);
        String entityStr = httpResult.getEntityStr();
        System.out.println("entityStr = " + entityStr);
                CloseableHttpClient build = HttpClients.custom().build();
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userData.id, userData.password);
        build.getState().setProxyCredentials(AuthScope.ANY, creds);
       "ww2y8b", "jEKeFf4K"
*/
        for (int i = 0; i < 100;i++){
            try {
                HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
                HttpResult httpResult = httpClientService.get("http://www.857ip.cn/getIP/txt/admin/admin/1");
                String entityStr = httpResult.getEntityStr();
                String[] hostPorts = entityStr.split("\n");
                System.out.println("hostPorts = " + hostPorts[0]);

                RequestPack requestPack = RequestPack.produce("https://store.steampowered.com/", null, HttpGet.class).setProxy(hostPorts[0], "http", "ww2y8b:jEKeFf4K");
                HttpResult result = httpClientService.execute(requestPack);
                System.out.println(result.getHttpStatus());
            }catch (Exception e){
                System.out.println( e.getMessage());
            }
        }
    }
}