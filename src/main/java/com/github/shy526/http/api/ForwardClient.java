package com.github.shy526.http.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.github.shy526.http.RequestPack;
import com.github.shy526.string.ElAnalysis;
import lombok.Data;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ForwardClient {

    private final static String GET_IP_URL = "https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo";
    private final BlockingQueue<ForwardInfo> forwardQueue = new LinkedBlockingQueue<>();
    private final static Map<String, TestEnum.Token> TOKEN_MAP = new HashMap<>();
    private HttpClientService httpClientService;

    private ForwardClient() {
    }

    public static ForwardClient readForwardInfo(String path, HttpClientService httpClientService) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sb.length() <= 0) {
            return null;
        }
        String str = sb.toString();
        if (!JSON.isValidArray(str)) {
            return null;
        }
        List<ForwardInfo> forwardInfos = JSON.parseArray(str, ForwardInfo.class);
        if (forwardInfos.isEmpty()) {
            return null;
        }
        ForwardClient forwardClient = new ForwardClient();
        forwardClient.httpClientService = httpClientService;
        for (ForwardInfo forwardInfo : forwardInfos) {
            String result = forwardClient.exe(forwardInfo, GET_IP_URL, "GET", null);
            if (StringUtils.isEmpty(result)) {
                System.out.println(JSON.toJSONString(forwardInfo) + "-> err req");
                continue;
            }
            JSONObject jsonObject = JSON.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            String ip = data.getString("addr");
            forwardInfo.setIp(ip);
            String country = data.getString("country");
            String province = data.getString("province");
            if ("中国".equals(country)) {
                forwardInfo.setChineseMainland(!"香港".equals(province));
            } else {
                forwardInfo.setChineseMainland(false);
            }

            String addr = country + "-" + province + "-" + data.getString("city");
            forwardInfo.setAddr(addr);
            System.out.println(forwardInfo.getTargetUrl()+"-->"+addr);
            forwardClient.forwardQueue.add(forwardInfo);
        }
        return forwardClient;
    }

    public String exe(String url, String method, Map<String, String> header) {
        ForwardInfo forwardInfo = null;
        String result = null;
        try {
            forwardInfo = forwardQueue.take();
            result = exe(forwardInfo, url, method, header);

        } catch (Exception ignored) {
        } finally {
            if (forwardInfo != null) {
                forwardQueue.offer(forwardInfo);
            }
        }
        return result;

    }

    public String exe(ForwardInfo forwardInfo, String url, String method, Map<String, String> header) {
        String result = null;
        RequestPack requestPack = buildRequestPack(forwardInfo.getTargetUrl(), forwardInfo.getTargetMethod());
        Map<String, String> auto = new HashMap<>();
        auto.put("Referer", forwardInfo.getTargetUrl());
        auto.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.61");
        requestPack.setHeader(auto);
        buildParams(requestPack, forwardInfo, method, url, header);
        try (HttpResult httpResult = httpClientService.execute(requestPack)) {
            result = parseJsonPath(httpResult.getEntityStr(), forwardInfo.getTargetPath());
        } catch (Exception ignored) {
        }

        return result;
    }

    private void buildParams(RequestPack requestPack, ForwardInfo forwardInfo, String method, String url, Map<String, String> header) {
        String token = getToken(forwardInfo);
        String headerStr = header2Str(header);
        String paramsEl = forwardInfo.getParamsEl();
        JSONObject elMap = new JSONObject();
        elMap.put("method", method);
        elMap.put("url", url);
        elMap.put("token", token);
        elMap.put("header", headerStr);
        String paramMod = forwardInfo.getParamMod().toUpperCase();
        String params = ElAnalysis.process(paramsEl, elMap);

        JSONObject paramsJson = JSON.parseObject(params);
        Map<String, String> paramsMap = paramsJson.getInnerMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, item -> item.getValue().toString()));
        switch (paramMod) {
            case "X-WWW-FORM-URLENCODED":
                requestPack.setFormat(paramsMap, CharEncoding.UTF_8);
                break;
            case "FORM-DATA":
                ContentType contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (Map.Entry<String, String> item : paramsMap.entrySet()) {
                    multipartEntityBuilder.addPart(item.getKey(), new StringBody(item.getValue(), contentType));
                }
                ((HttpPost) requestPack.getRequestBase()).setEntity(multipartEntityBuilder.build());
                break;
            case "JSON":
                requestPack.setBodyStr(paramsJson.toJSONString());
            case "HEADER":
                requestPack.setHeader(paramsMap);
                break;
        }
    }

    private String header2Str(Map<String, String> header) {
        StringBuilder headerSb = new StringBuilder();
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                headerSb.append(key).append("=").append(value).append(",");
            }
            headerSb.deleteCharAt(headerSb.length() - 1);
        }
        return headerSb.toString();
    }

    private String getToken(ForwardInfo forwardInfo) {
        String tokenUrl = forwardInfo.getTokenUrl();
        String tokenPath = forwardInfo.getTokenPath();
        Long tokenExpiry = forwardInfo.getTokenExpiry();
        String tokenMethod = forwardInfo.getTokenMethod();
        if (StringUtils.isEmpty(tokenUrl)) {
            return null;
        }
        String token = getToken(tokenUrl, tokenExpiry);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        tokenMethod = StringUtils.isEmpty(tokenMethod) ? "GET" : tokenMethod;
        RequestPack requestPack = buildRequestPack(tokenUrl, tokenMethod);
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", "postman.sid=335fb260230aaff9b7c4a0fe7e2990f6325a033791006fa5dc362cd0030ca6218d0a2032a4edb51530fdc0a71c089c7559a73f6dc5ab7d7561c06317d4ef11875343cc5d393543b475a230a5436ce99cbdd45f0337b7e953c50b1878ce596c153b43a7ec9eafe266a1482a4c95be7eec64954fbca82c3b5f200bf010d1b719167d0134c3a62588a5151b715ddda1b0ef81e5dcd0338701eeab0c7619a1d63eb95aae27f58b6fd689b00355282dc7c926df5b2f98a4d6c6b01fb179ff9637287b1efffc786cbb4bc49de7210a96fa660915276dab31ddb48982e06ad6dbb68781e0d736dee4da3ca12cc478f9cc88468a702e7bcb38f3a73b5215c5383289bb3d4061b83e11abac25b47ca6e533686f4d266c752fb1837f7669253c1157e22997f3fe731a19c0979e12f912d2bbfe2d26d9b227aa2ec5959be01ebd0add994f163c816bd7b17c337bc3f6840e4a0ba0809158cbfbf184689bc64fcf58d4d0828e9c8d733026da0bf6b51ab936dcb11c5e379681a7b8e1d5bb881ba888f38f6df7bd6925647a90c012cca361d79331ea2d3b386f72af572640ae401a5b93d63a14b5a980269f8f43ea177b29eda8c4ddc5623d3263c38af9c2ce650734e904ceffe56fa579af05604059d9781daf5f93b1ed92107e6d6d20de75db54c8");

        requestPack.setHeader(header);
        try (HttpResult execute = httpClientService.execute(requestPack)) {
            token = parseJsonPath(execute.getEntityStr(), tokenPath);
        } catch (Exception ignored) {

        }
        return token;
    }

    protected String parseJsonPath(String json, String jsonPah) {
        if (StringUtils.isEmpty(jsonPah)) {
            return json;
        }
        if (!JSON.isValid(json)) {
            return json;
        }

        String[] split = jsonPah.split("\\.");
        JSONObject temp = JSON.parseObject(json);
        for (String key : split) {
            int tempVal = key.indexOf("[");
            String str = null;
            if (tempVal == -1) {
                str = temp.getString(key);
            } else {
                String index = key.substring(tempVal + 1, key.length() - 1);
                str = temp.getJSONArray("data").getString(Integer.parseInt(index));
            }
            if (!JSON.isValid(str)) {
                return str;
            }
            try {
                str = URLDecoder.decode(str, "UTF-8");
            } catch (Exception ignored) {
            }
            temp = JSON.parseObject(str);
        }
        return temp.toJSONString();
    }

    protected RequestPack buildRequestPack(String url, String tokenMethod) {
        String method = tokenMethod.toUpperCase();
        if ("GET".equals(method)) {
            return RequestPack.produce(url, null, HttpGet.class);
        } else {
            return RequestPack.produce(url, null, HttpPost.class);
        }
    }

    protected void addToken(TestEnum testEnum, String tokenStr) {
        TestEnum.Token token = new TestEnum.Token();
        token.setToken(tokenStr);
        token.setTime(System.currentTimeMillis() - 2000);
        TOKEN_MAP.put(testEnum.toString(), token);
    }

    protected String getToken(String tokenUrl, long expiry) {
        TestEnum.Token token = TOKEN_MAP.get(tokenUrl);
        if (token == null) {
            return null;
        }
        if (System.currentTimeMillis() - token.getTime() > expiry) {
            return null;
        }
        return token.getToken();
    }

    @Data
    public static class Token implements Serializable {
        private String token;
        private long time;
    }

}
