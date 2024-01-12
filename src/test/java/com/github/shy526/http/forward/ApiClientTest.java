package com.github.shy526.http.forward;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.HttpClientFactory;
import com.github.shy526.http.HttpClientProperties;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.google.common.collect.Lists;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClientTest {


    @Test
    public void testExec2() {
        //https://steamcommunity.com/market/search/render/?query=&start=10&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570
        //https://steamcommunity.com/market/search/render/?query=&start=%s&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570&currency=23
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        String pageUrl = "https://steamcommunity.com/market/search/render/?query=&start=%s&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570&currency=23";
        File file = new File("C:\\Users\\sunda\\Desktop\\data2饰品信息.txt");
        {
            boolean temp = file.exists() && file.delete();
        }
        String proxyUrl = "https://proxy.shy526.top?targetUrls=%s";
        ///  ApiClient.restApiTestEnumQueue(ApiTestEnum.SHULIJP);
        String targetUrls = "";
        try {
            targetUrls = URLEncoder.encode("https://steamcommunity.com/market/search/render/?query=&start=30&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570", "utf-8");
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
            //   JSONObject total1 = ApiClient.exec(String.format(proxyUrl, baseStr), httpClientService);
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


    private int pageParse(HttpClientService httpClientService, String proxyUrl, StringBuilder sb) {
        String hash = null;
        try {
            hash = URLEncoder.encode(sb.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
        }
        HttpResult httpResult = httpClientService.get(String.format(proxyUrl, hash));
        JSONObject jsonObj = httpResult.getJsonObj();
        JSONArray tempArr = jsonObj.getJSONArray("result");
        int total = 0;
        for (Object o : tempArr) {
            JSONObject json = (JSONObject) o;
            JSONObject data = json.getJSONObject("result").getJSONObject("data");
            if (data == null) {
                System.out.println();
            }
            total = data.getIntValue("total_page");
            JSONArray items = data.getJSONArray("items");
            for (int i = 0; i < items.size(); i++) {
                json = items.getJSONObject(i);
                Integer buffId = json.getInteger("id");
                String marketHashName = json.getString("market_hash_name");
                String cnName = json.getString("name");
                BigDecimal price = json.getBigDecimal("sell_min_price");
                System.out.println(buffId + "," + marketHashName + "," + cnName + "," + price);
            }
        }
        return total;
    }


    @Test
    public void testExec3() {
        String str = "测试数据";
        byte[] bytes = str.getBytes();
        List<Integer> colors = new ArrayList<>();
        List<Integer> rgb = new ArrayList<>(3);
        int length = bytes.length;
        int a = ((length >> 24) & 0xff);
        int r = ((length >> 16) & 0xff);
        int g = ((length >> 8) & 0xff);
        int b = (length & 0xff);
        int color = getColor(Lists.newArrayList(r, g, b));
        colors.add(color);
        colors.add(getColor(Lists.newArrayList(a, 0, 0)));
        for (byte aByte : bytes) {
            rgb.add(Byte.toUnsignedInt(aByte));
            if (rgb.size() == 3) {
                colors.add(getColor(rgb));
                rgb.clear();
            }
        }
        BufferedImage br = new BufferedImage(colors.size(), 1, BufferedImage.TYPE_INT_RGB);
        int width = br.getWidth();
        int height = br.getHeight();
        Iterator<Integer> iterator = colors.iterator();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                br.setRGB(x, y, iterator.next());
            }
        }
        File file = new File("D:\\个人文件\\图片\\test.png");
        try {
            boolean png = ImageIO.write(br, "png", new FileOutputStream(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void testExec4() {
        try {
            BufferedImage br = ImageIO.read(new File("D:\\个人文件\\图片\\test.png"));
            int width = br.getWidth();
            int height = br.getHeight();
            int rgb1 = br.getRGB(0, 0);
            byte[] colorRGB = getColorRGB(rgb1);
            int color1 = getColor(Lists.newArrayList((int) colorRGB[0], (int) colorRGB[1], (int) colorRGB[2]));
            int rgb2 = br.getRGB(1, 0);
            colorRGB = getColorRGB(rgb2);
            int lent = color1 | ((int) colorRGB[0]) << 24;
            System.out.println("rgb2 = " + rgb2);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int color = br.getRGB(x, y);
                    byte[] rgb = getColorRGB(color);
                    bos.write(rgb);
                }
            }
            bos.write(0);
            bos.write(0);
            byte[] bytes = bos.toByteArray();
            String string = new String(bytes, 6, 12);
            System.out.println("string = " + string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void testExec5() {
        int v = 100;
        byte a = (byte) ((v >> 24) & 0xff);
        byte b = (byte) ((v >> 16) & 0xff);
        byte c = (byte) ((v >> 8) & 0xff);
        byte d = (byte) (v & 0xff);

        System.out.println("d = " + d);
    }

    private static int getColor(List<Integer> rgb) {
        int r = rgb.get(0);
        int g = rgb.get(1);
        int b = rgb.get(2);
        return (r << 16) | (g << 8) | b;
    }

    public static byte[] getColorRGB(int color) {
        byte r = (byte) ((color >> 16) & 0xff);
        byte g = (byte) ((color >> 8) & 0xff);
        byte b = (byte) (color & 0xff);
        return new byte[]{r, g, b};
    }


    @Test
    public void testExec444() {
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
     //   ForwardClient steamPageClient1 = ForwardClient.readForwardInfo("D:\\个人文件\\图片\\test.json", httpClientService);
        ForwardClient steamPageClient = ForwardClient.readForwardInfo("D:\\个人文件\\图片\\steamPageForward.json", httpClientService);
        ForwardClient steamIdClient = ForwardClient.readForwardInfo("D:\\个人文件\\图片\\steamIdForward.json", httpClientService);
        String pageUrl = "https://steamcommunity.com/market/search/render/?query=&start=%s&count=100&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=570&currency=233&norender=1";
        String getSteamIdUrl = "https://steamcommunity.com/market/listings/570/%s";
        Pattern reg = Pattern.compile("Market_LoadOrderSpread\\(\\s*(\\d+)\\s*\\);");
        File file = new File("D:\\个人文件\\图片\\data2Info.txt");
        Set<String> hash = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                hash.add(split[2]);
            }
        } catch (Exception ignored) {
        }
        PrintWriter fileOut = null;
        try {
            fileOut = new PrintWriter(new FileWriter(file,true),true);
        } catch (Exception ignored) {
        }
        for (int index = 0; ; index += 100) {
            sleep(5000);
            String result = steamPageClient.exe(String.format(pageUrl, index), MethodEnum.GET, header);
            JSONObject parse = JSONObject.parseObject(result);
            JSONArray results = parse.getJSONArray("results");
            if (results.isEmpty()) {
                return;
            }
            for (int i = 0; i < results.size(); i++) {
                JSONObject jsonObject = results.getJSONObject(i);
                String name = jsonObject.getString("name");
                String hashName = jsonObject.getString("hash_name");
                try {
                    hashName = URLEncoder.encode(hashName, CharEncoding.UTF_8).replace("+", "%20");
                } catch (Exception ignored) {
                }
                if (hash.contains(hashName)) {
                    continue;
                }
                String steamIdHtml = steamIdClient.exe(String.format(getSteamIdUrl, hashName), MethodEnum.GET, header);
                if (StringUtils.isEmpty(steamIdHtml)) {
                    System.out.println(name + "," + hashName + "not get  id");
                    continue;
                }
                String itemId = null;
                Matcher matcher = reg.matcher(steamIdHtml);
                if (matcher.find()) {
                    itemId = matcher.group(1);
                    fileOut.println(itemId + "," + name + "," + hashName);
                } else {
                    System.out.println(name + "," + hashName + "not get  id");
                }
               // sleep(2000);

            }
        }
    }

    private void sleep(long timeOut) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeOut);
        } catch (Exception ignored) {
        }
    }
}