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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        for (; ; ) {
            try {
                HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
                HttpResult httpResult = httpClientService.get("http://www.857ip.cn/getIP/txt/admin/admin/1");
                String entityStr = httpResult.getEntityStr();
                String[] hostPorts = entityStr.split("\n");
                System.out.println("hostPorts = " + hostPorts[0]);

                RequestPack requestPack = RequestPack.produce("https://steamcommunity.com/market/listings/570/Lycosidae%27s%20Favor", null, HttpGet.class).setProxy(hostPorts[0], "http", "ww2y8b:dni2DfeH");
                HttpResult result = httpClientService.execute(requestPack);
                System.out.println(result.getHttpStatus());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    @Test
    public void http2() {
        HttpClientProperties httpClientProperties = new HttpClientProperties();
        HttpClientService instance = HttpClientFactory.getHttpClientService(httpClientProperties);
        HttpResult httpResult = instance.get("https://picshack.net");
        CloseableHttpResponse cre = httpResult.getResponse();
        Header[] headers = cre.getHeaders("Set-Cookie");
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            for (String item : header.getValue().split(";")) {
                String[] split = item.split("=");
                if (split[0].equals("_session")) {
                    sb.append(item).append(";");
                } else if (split[0].equals("XSRF-TOKEN")) {
                    sb.append(item).append(";");
                }
            }
        }
        Document doc = Jsoup.parse(httpResult.getEntityStr());
        Elements select = doc.select("meta[name=csrf-token]");
        String csrf = select.attr("content");
        String csrfToken = sb.substring(0, sb.length());


        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        File file = new File("C:/Users/Administrator/Pictures/QQ截图20230929165346 - 副本.png");
        builder.addBinaryBody("uploads", file, ContentType.MULTIPART_FORM_DATA, file.getName());

        /// builder.addPart("uploads", fileBody);
        RequestPack produce = RequestPack.produce("https://picshack.net/upload", null, HttpPost.class);
        HttpPost postRequest = (HttpPost) produce.getRequestBase();
        HttpEntity build = builder.build();

        postRequest.setEntity(build);
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("X-Csrf-Token", csrf);
        postRequest.addHeader("X-Requested-With", "XMLHttpRequest");
        postRequest.addHeader("Cache-Control", "no-cache");
        postRequest.addHeader("Cookie", csrfToken);
        postRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.43");
        HttpResult execute = instance.execute(postRequest);
        System.out.println(execute.getHttpStatus());
        System.out.println(execute.getEntityStr());


    }

}