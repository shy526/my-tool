package com.github.shy526.http.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.*;
import com.github.shy526.string.ElAnalysis;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ApiClient {

    private final static BlockingQueue<ApiTestEnum> QUEUE = new LinkedBlockingQueue<>();


    static {
        QUEUE.addAll(Lists.newArrayList(ApiTestEnum.values()));
    }

    public static void restApiTestEnumQueue(ApiTestEnum... apiTestEnums) {
        QUEUE.clear();
        QUEUE.addAll(Arrays.asList(apiTestEnums));
    }

    public static JSONObject exec(String testApi, HttpClientService httpClientService, ApiTestEnum value) {
        JSONObject prop = new JSONObject();
        prop.put("url", testApi);
        prop.put("method", MethodEnum.GET.toString());
        return getResult(httpClientService, value, buildRequestPack(prop, value));
    }


    public static JSONObject exec(String testApi, HttpClientService httpClientService) {
        JSONObject prop = new JSONObject();
        prop.put("url", testApi);
        prop.put("method", MethodEnum.GET.toString());
        ApiTestEnum value = null;
        JSONObject result = new JSONObject();
        try {
            value = QUEUE.take();
            result = getResult(httpClientService, value, buildRequestPack(prop, value));
        } catch (Exception ignored) {
        } finally {
            boolean temp = value != null && QUEUE.offer(value);
        }

        return result;
    }


    private static JSONObject getResult(HttpClientService httpClientService, ApiTestEnum value, RequestPack produce) {
        JSONObject result = null;
        try (HttpResult execute = httpClientService.execute(produce)) {
            JSONObject jsonObj = execute.getJsonObj();
            String resultPath = value.getResultPath();
            if (StringUtils.isEmpty(resultPath)) {
                result = jsonObj;
            } else {
                String[] split = resultPath.split("\\.");
                JSONObject temp = jsonObj;
                for (String s : split) {
                    int tempVal = s.indexOf("[");
                    if (tempVal == -1) {
                        String decode = URLDecoder.decode(temp.getString(s), "UTF-8");
                        if (JSON.isValid(decode)){
                            temp = JSON.parseObject(decode);
                        }else {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("result",decode);
                            jsonObject.put("apiTestError",true);
                            temp=jsonObject;
                            break;

                        }

                    } else {
                        String index = s.substring(tempVal + 1, s.length() - 1);
                        temp = temp.getJSONArray("data").getJSONObject(Integer.parseInt(index));
                    }

                }
                result = temp;
            }
        } catch (Exception e) {
            System.err.println(value + ":" + e.getMessage());
        }
        return result;
    }

    private static RequestPack buildRequestPack(JSONObject prop, ApiTestEnum value) {
        String url = value.getUrl();
        RequestPack produce = RequestPack.produce(url, null, HttpPost.class);
        String aipParams = value.getAipParams();
        aipParams = ElAnalysis.process(aipParams, prop);
        JSONObject tempJson = JSON.parseObject(aipParams);
        Map<String, String> format = tempJson.getInnerMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, item -> item.getValue().toString()));
        switch (value.getEnctype()) {
            case URLENCODED:
                produce.setFormat(format, CharEncoding.UTF_8);
                break;
            case MULTIPART:
                ContentType contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (Map.Entry<String, String> item : format.entrySet()) {
                    multipartEntityBuilder.addPart(item.getKey(), new StringBody(item.getValue(), contentType));
                }
                ((HttpPost) produce.getRequestBase()).setEntity(multipartEntityBuilder.build());
                break;
            case JSON:
                produce.setBodyStr(tempJson.toJSONString());
                break;
        }

        Map<String, String> header = new HashMap<>();
        header.put("Referer", url);
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.61");
        produce.setHeader(header);
        return produce;
    }
}
