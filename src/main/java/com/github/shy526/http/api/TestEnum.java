package com.github.shy526.http.api;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum TestEnum {

    postman("https://orion-http.gw.postman.co/v1/request", null,
            "https://ra.gw.postman.co/v1/handshake/token?agent=cloud", null, "GET", 1500000,
            "{}", "heander"

    );

    private String url;

    private String resultPath;

    private String tokenUrl;

    private String tokenPath;

    private String tokenMethod;

    private long tokenTtl;

    private String params;
    private String paramsMod;
    private final static Map<String, Token> TOKEN_MAP = new HashMap<>();

    TestEnum(String url, String resultPath, String tokenUrl, String tokenPath, String tokenMethod, long tokenTtl, String params, String paramsMethod) {
        this.url = url;
        this.resultPath = resultPath;
        this.tokenUrl = tokenUrl;
        this.tokenPath = tokenPath;
        this.tokenMethod = tokenMethod;
        this.tokenTtl = tokenTtl;
        this.params = params;
        this.paramsMod = paramsMethod;
    }

    public static void addToken(TestEnum testEnum, String tokenStr) {
        Token token = new Token();
        token.setToken(tokenStr);
        token.setTime(System.currentTimeMillis());
        TOKEN_MAP.put(testEnum.toString(), token);
    }

    public static String getToken(TestEnum testEnum) {
        Token token = TOKEN_MAP.get(testEnum.toString());
        if (token == null) {
            return null;
        }
        if (System.currentTimeMillis() - token.getTime() > testEnum.getTokenTtl()) {
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
