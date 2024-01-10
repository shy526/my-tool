package com.github.shy526.http.api;


import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.*;
import com.google.common.io.BaseEncoding;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClientTest {

    @Test
    public void testExec() {

        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        ApiTestEnum[] values = ApiTestEnum.values();
        for (int i = 0; i < values.length; i++) {
            ApiTestEnum value = values[i];
            JSONObject exec = ApiClient.exec("https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo", httpClientService, value);
            System.out.println(value.toString() + "->" + exec.getJSONObject("data").get("country") + "-" + exec.getJSONObject("data").get("province") + "->" + exec.getJSONObject("data").get("addr"));
        }

    }

    @Test
    public void testExec2() {
        //https://steamcommunity.com/market/search/render/?query=&start=10&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570
        //https://steamcommunity.com/market/search/render/?query=&start=%s&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570&currency=23
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        HashMap<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        String pageUrl = "https://steamcommunity.com/market/search/render/?query=&start=%s&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570&currency=23";
        File file = new File("C:\\Users\\sunda\\Desktop\\data2饰品信息.txt");
        {
            boolean temp = file.exists() && file.delete();
        }
        String proxyUrl = "https://proxy.shy526.top?targetUrls=%s";
        ApiClient.restApiTestEnumQueue(ApiTestEnum.SHULIJP);
        String targetUrls="";
        try {
            targetUrls = URLEncoder.encode("https://steamcommunity.com/market/search/render/?query=&start=30&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570","utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HttpResult httpResult1 = httpClientService.get(String.format(proxyUrl, targetUrls));
        JSONObject jsonObj = httpResult1.getJsonObj();
        JSONObject total = new JSONObject();
        int totalCount = total.getIntValue("total_count");
        List<String> urls = new ArrayList<>();
        StringBuilder tempSb = new StringBuilder();
        char t = ',';
        for (int i = 0; i <= totalCount; i += 10) {
            String tempUrl = String.format(pageUrl, i);
            urls.add(tempUrl);
            tempSb.append(tempUrl).append(t);
            if (urls.size() < 1) {
                continue;
            }
            tempSb.deleteCharAt(tempSb.length() - 1);
            String baseStr = null;
            JSONObject total1 = ApiClient.exec(String.format(proxyUrl, baseStr), httpClientService);
            tempSb.setLength(0);
            urls.clear();
        }

        PrintWriter fileOut = null;
        try {
            fileOut = new PrintWriter(new FileWriter(file));
        } catch (Exception ignored) {
        }
        while (true) {
            JSONObject json = null;
            String format = null; ///String.format(pageUrl, index);
            format = "https://yunhanshuceshi.919200345.workers.dev/";
            System.out.println("url->" + format);

            try (HttpResult httpResult = httpClientService.get(format, null, header)) {
                json = httpResult.getJsonObj();
            } catch (Exception ignored) {
            }
            // index += 10;
            if (json == null) {
                break;
            }
            Document resultsHtml = Jsoup.parse(json.getString("results_html"));

            Elements selects = resultsHtml.select(".market_listing_row_link");
            String url = "https://steamcommunity.com/market/priceoverview/?appid=570&country=CN&currency=23&market_hash_name=%s";
            //https://steamcommunity.com/market/itemordershistogram?country=CN&language=schinese&currency=23&item_nameid=176407302&two_factor=0
            Pattern reg = Pattern.compile("Market_LoadOrderSpread\\(\\s*(\\d+)\\s*\\);");

            for (Element select : selects) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ignored) {
                }
                String href = select.attr("href");
                String marketHashName = href.substring(href.lastIndexOf("/") + 1);
                String cnName = select.select(".market_listing_item_name").text();
                String itemId = null;
                try (HttpResult httpResult = httpClientService.get(href)) {
                    Element script = Jsoup.parse(httpResult.getEntityStr()).select("script").last();
                    Matcher matcher = reg.matcher(script.toString());
                    if (matcher.find()) {
                        itemId = matcher.group(1);
                    }
                } catch (Exception ignored) {
                }
                StringBuilder sb = new StringBuilder();
                sb.append(itemId).append(",").append(cnName).append(",").append(marketHashName);
                System.out.println(sb.toString());
                if (fileOut != null) {
                    fileOut.flush();
                    fileOut.println(sb.toString());
                }


 /*           try (HttpResult httpResult = httpClientService.get(String.format(url,marketHashName), null, header)) {
                JSONObject jsonObj = httpResult.getJsonObj();
                System.out.println("jsonObj = " + jsonObj);
            } catch (Exception ignored) {
            }
*/

            }
        }

        if (fileOut != null) {
            fileOut.flush();
            fileOut.close();
            fileOut = null;
        }
    }

    @Test
    public void testExec33(){

        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());

        ForwardClient forwardClient = ForwardClient.readForwardInfo("E:\\qq\\cache\\919200345\\FileRecv\\test.json", httpClientService);

        String get = forwardClient.exe("https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo", "GET", null);
        System.out.println("get = " + get);


    }
}