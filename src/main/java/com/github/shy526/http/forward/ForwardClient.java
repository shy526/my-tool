package com.github.shy526.http.forward;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.HttpClientService;
import com.github.shy526.http.HttpResult;
import com.github.shy526.http.RequestPack;
import com.github.shy526.string.ElAnalysis;
import lombok.Data;
import lombok.Getter;
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
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ForwardClient {

    private final static String GET_IP_URL = "https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo";
    @Getter
    private final BlockingQueue<ForwardInfo> forwardQueue = new LinkedBlockingQueue<>();
    private final static Map<String, Token> TOKEN_MAP = new HashMap<>();
    private final static Map<String, Set<String>> ERR_MAP = new HashMap<>();
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
        } catch (Exception ignored) {
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
            String result = forwardClient.exe(forwardInfo, GET_IP_URL, MethodEnum.GET, null);
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
            System.out.println(forwardInfo.getTargetUrl() + "-->" + addr);
            forwardClient.forwardQueue.add(forwardInfo);
        }
        return forwardClient;
    }

    public String exe(String url, MethodEnum method, Map<String, String> header) {
        ForwardInfo forwardInfo = null;
        String result = null;
        try {
            forwardInfo = forwardQueue.take();
            Set<String> has = ERR_MAP.get(forwardInfo.getTargetUrl());
            String host = newURL(url).getHost();
            if (has.contains(host)) {
                return null;
            }
            result = exe(forwardInfo, url, method, header);

        } catch (Exception ignored) {
        } finally {
            if (forwardInfo != null) {
                forwardQueue.offer(forwardInfo);
            }
        }
        return result;

    }

    public String exe(ForwardInfo forwardInfo, String url, MethodEnum method, Map<String, String> header) {
        String result = null;
        String targetUrl = forwardInfo.getTargetUrl();
        RequestPack requestPack = buildRequestPack(targetUrl, forwardInfo.getTargetMethod());
        setTargetHeader(forwardInfo, targetUrl, requestPack);
        buildParams(requestPack, forwardInfo, method, url, header);
        Integer httpStatus = 200;
        try (HttpResult httpResult = httpClientService.execute(requestPack)) {
            httpStatus = httpResult.getHttpStatus();
            result = parseJsonPath(httpResult.getEntityStr(), forwardInfo.getTargetPath());
        } catch (Exception ignored) {
        }
        if (StringUtils.isEmpty(result) || httpStatus != 200) {
            Set<String> hase = ERR_MAP.get(targetUrl);
            hase = hase == null ? new HashSet<>() : hase;
            hase.add(newURL(url).getHost());
            ERR_MAP.put(targetUrl, hase);
        }
        return result;
    }


    /**
     * 设置转发请求头
     * @param forwardInfo forwardInfo
     * @param targetUrl targetUrl
     * @param requestPack requestPack
     */
    private void setTargetHeader(ForwardInfo forwardInfo, String targetUrl, RequestPack requestPack) {
        Map<String, String> auto = new HashMap<>();
        auto.put("Referer", targetUrl);
        auto.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.61");
        parseHeader(forwardInfo.getTargetHeader(), auto);
        auto.put("Host", newURL(targetUrl).getHost());
        requestPack.setHeader(auto);
    }

    /**
     * 设置转发参数
     * @param requestPack requestPack
     * @param forwardInfo forwardInfo
     * @param method method
     * @param url url
     * @param header header
     */
    private void buildParams(RequestPack requestPack, ForwardInfo forwardInfo, MethodEnum method, String url, Map<String, String> header) {
        String token = getToken(forwardInfo);
        String headerStr = header2Str(header);
        String paramsEl = forwardInfo.getParamsEl();
        JSONObject elMap = new JSONObject();
        URL temp = newURL(url);
        elMap.put("host", temp.getHost());
        elMap.put("path", temp.getPath());
        elMap.put("protocol", temp.getProtocol());
        elMap.put("method", method.toString());
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

    /**
     * 将请求头Map转华为字符串
     * @param header header
     * @return  String
     */
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


    /**
     * 获取token
     * @param forwardInfo forwardInfo
     * @return String
     */
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
        parseHeader(forwardInfo.getTokenHeader(), header);
        requestPack.setHeader(header);
        try (HttpResult execute = httpClientService.execute(requestPack)) {
            token = parseJsonPath(execute.getEntityStr(), tokenPath);
        } catch (Exception ignored) {
        }
        if (StringUtils.isNotEmpty(token)) {
            addToken(tokenUrl, token);
        }
        return token;
    }

    /**
     * 字符串请求头解析为map
     * @param headerStr headerStr
     * @param header header
     */
    private void parseHeader(String headerStr, Map<String, String> header) {
        if (StringUtils.isEmpty(headerStr)) {
            return;
        }
        String[] split = headerStr.split(";");
        for (String kv : split) {
            String[] kvArr = kv.split(":");
            if (kvArr.length != 2) {
                continue;
            }
            header.put(kvArr[0], kvArr[1]);
        }
    }


    /**
     * 解析结果路径
     * @param json json
     * @param jsonPah 路径
     * @return String
     */
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
                return decode(str);
            }
            temp = JSON.parseObject(str);
        }
        return temp.toJSONString();
    }

    private String decode(String str) {
        try {
            return URLDecoder.decode(str, CharEncoding.UTF_8);
        } catch (Exception ignored) {
        }
        return str;
    }
    private URL newURL(String url) {
        try {
            return new URL(url);
        } catch (Exception ignored) {
        }
        return null;
    }
    protected RequestPack buildRequestPack(String url, String tokenMethod) {
        String method = tokenMethod.toUpperCase();
        if ("GET".equals(method)) {
            return RequestPack.produce(url, null, HttpGet.class);
        } else {
            return RequestPack.produce(url, null, HttpPost.class);
        }
    }

    protected void addToken(String key, String tokenStr) {
        Token token = new Token();
        token.setToken(tokenStr);
        token.setTime(System.currentTimeMillis() - 2000);
        TOKEN_MAP.put(key, token);
    }

    protected String getToken(String tokenUrl, long expiry) {
        Token token = TOKEN_MAP.get(tokenUrl);
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
