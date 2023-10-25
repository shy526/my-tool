package com.github.shy526.http.api;


import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.HttpClientFactory;
import com.github.shy526.http.HttpClientProperties;
import com.github.shy526.http.HttpClientService;
import org.junit.Test;

public class ApiClientTest   {

    @Test
    public void testExec() {

        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        for (int i=0;i<ApiTestEnum.values().length;i++) {
            JSONObject exec = ApiClient.exec("https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo", httpClientService);
            System.out.println(exec.getJSONObject("data").get("country")+"-"+exec.getJSONObject("data").get("province")+"->" + exec.getJSONObject("data").get("addr"));
        }

    }

    @Test
    public void testExec2() {
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        JSONObject exec = ApiClient.exec("https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo", httpClientService,ApiTestEnum.WXAI);
        System.out.println(exec.getJSONObject("data").get("country")+"-"+exec.getJSONObject("data").get("province")+"->" + exec.getJSONObject("data").get("addr"));

    }
}