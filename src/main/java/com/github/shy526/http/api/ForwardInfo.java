package com.github.shy526.http.api;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ForwardInfo implements Serializable {
    private String paramsEl;
    private String paramMod;
    private String tokenUrl;
    private String tokenPath;
    private Long tokenExpiry;
    private String tokenMethod;
    private String targetUrl;
    private String targetPath;
    private String targetMethod;
    private List<String> supportMethod;

    private String ip;

    private String addr;
    private boolean chineseMainland;

}
