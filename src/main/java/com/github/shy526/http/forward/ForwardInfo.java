package com.github.shy526.http.forward;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ForwardInfo implements Serializable {

    /**
     * 转发需要的参数
     */
    private String paramsEl;
    /**
     * 转发参数的提交方式 支持 X-WWW-FORM-URLENCODED,FORM-DATA,JSON,HEADER
     */
    private String paramMod;
    /**
     * 获取转发需要token的url
     */
    private String tokenUrl;
    /**
     * 获取token的解析路径
     */
    private String tokenPath;
    /**
     * token的存活时间
     */
    private Long tokenExpiry;
    /**
     * 请求token的方式 get post
     */
    private String tokenMethod;

    /**
     * 转发的url
     */
    private String targetUrl;
    /**
     * 转发结果的解析路径
     */
    private String targetPath;
    /**
     * 转发的请求方式 get post
     */
    private String targetMethod;

    private String targetCode;
    /**
     * 该转发支持什么 请求 get set 暂时不支持
     */
    private List<String> supportMethod;

    /**
     * 转发时需要携带的头文件 需要登录时可以使用
     */
    private String targetHeader;
    /**
     * 获取tokenHeader时需要携带的头文件
     */
    private String tokenHeader;

    /**
     * 转发时需要的header 格式 默认 kev:val
     * kev ${kev} val ${val}
     */
    private String headerFormat;
    /**
     * 转发时不同头文件的间隔
     */
    private String headerSpace;

    /**
     * 该转发的ip
     */
    private String ip;
    /**
     * 该转发的物理地址
     */

    private String addr;

    /**
     * 是否在中国大陆
     */
    private Boolean chineseMainland = true;

    private Boolean headerFlag = false;
    private Boolean testFlag = false;
}
