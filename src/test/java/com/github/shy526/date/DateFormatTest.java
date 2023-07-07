package com.github.shy526.date;

import com.github.shy526.http.HttpClientFactory;
import com.github.shy526.http.HttpClientProperties;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
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
        String result = "";
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse res = null;
        try {
            HttpClientContext context = HttpClientContext.create();

            // 创建一个httpClient对象
            //登陆 从配置文件中读取url(也可以写成参数)
            HttpGet httpPost = new HttpGet("https://steamcommunity.com/market/listings/730/AK-47%20%7C%20Head%20Shot%20(Minimal%20Wear)");

            // 设置代理HttpHost115.207.63.114:40048
            HttpHost proxy = new HttpHost("1.195.202.223", 40022,"https");
            // 设置认证
      
            httpclient=HttpClients.custom().setSSLSocketFactory(HttpClientFactory.getSslConnectionSocketFactory()).build();
            RequestConfig config = RequestConfig.custom().setProxy(proxy)
                    .setSocketTimeout(2 * 1000)
                    .setConnectTimeout(2 * 1000)
                    .setConnectionRequestTimeout(2 * 1000)
                    .build();
            // 设置参数
            httpPost.setConfig(config);
            // 得到响应状态码
            res = httpclient.execute(httpPost,context);
            //  获取返回对象
            HttpEntity entity = res.getEntity();
            // 通过EntityUtils获取返回结果内容
            result = EntityUtils.toString(entity);
            System.out.println( res.getStatusLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}