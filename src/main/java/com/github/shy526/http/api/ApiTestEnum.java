package com.github.shy526.http.api;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum ApiTestEnum {
    TOOL("http://tool.pfan.cn/apitest/request", "程序员工具集",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST), "{\n" +
            "    \"_apiurl_\":\"${url}\",\n" +
            "    \"_apimethod_\":\"${method}\"\n" +
            "}", "response.body", EnctypeEnum.URLENCODED),
    WP56("http://www.56wp.com/121api.php", "56瓶碗",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST), "{\n" +
            "    \"url\":\"${url}\",\n" +
            "    \"m\":\"${method}\"\n" +
            "}", null, EnctypeEnum.URLENCODED),

    POST_JSON("http://coolaf.com/tool/ajaxgp", "PostJson",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST), "{\n" +
            "    \"url\":\"${url}\",\n" +
            "    \"seltype\":\"${method}\",\n" +
            "    \"j\":1,\n" +
            "    \"ct\":\"application/x-www-form-urlencoded\",\n" +
            "    \"code\":\"utf8\",\n" +
            "    \"ck\":\"\",\n" +
            "    \"header\":\"\",\n" +
            "    \"parms\":\"\",\n" +
            "    \"proxy\":\"\"\n" +
            "}", "data.response"
            , EnctypeEnum.MULTIPART),

    ME_TOOL("http://www.metools.info/res/serv/httppost-s.php", "meTool",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST), "{\n" +
            "    \"url\":\"${url}\",\n" +
            "    \"seltype\":\"${method}\",\n" +
            "    \"cy\":1,\n" +
            "    \"ct\":\"application/x-www-form-urlencoded\"\n" +
            "}", "data.response"
            , EnctypeEnum.URLENCODED),


    FLY63("https://api.fly63.com/home/static/php/http/api.php", "FLY63",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST), "{\n" +
            "    \"url\": \"${url}\",\n" +
            "    \"methods\": \"${method}\",\n" +
            "    \"code\": \"UTF-8\",\n" +
            "    \"header\": {\n" +
            "        \"content-type\": \"application/x-www-form-urlencoded\"\n" +
            "    },\n" +
            "    \"parm\": {},\n" +
            "    \"cookie\": \"\",\n" +
            "    \"proxy\": \"\"\n" +
            "}", "data"
            , EnctypeEnum.JSON),

    EC_JSON("https://www.ecjson.com/apitool/httpurl","ecjson",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{\"url\":\"${url}\",\"type\":\"${method}\"}","value.content",EnctypeEnum.URLENCODED),

    HELP_BJ("https://help.bj.cn/API/PostMan/","help_bj",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{url: \"${url}\"," +
            "mobile: \"PC\",\n" +
            "charset: \"UTF-8\"}","data[0].body",EnctypeEnum.URLENCODED),

    YZC_OPEN("http://www.yzcopen.com/seo/getinter","yzc_open",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{url: \"${url}\"," +
            "type: 3 }","restparm.body",EnctypeEnum.URLENCODED),

    NONG_PIN("http://www.nongpin88.com/tools/http/post.php","nongpin88",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{url: \"${url}\"," +
            "method: \"${method}\",cookies:\"\" }","message",EnctypeEnum.URLENCODED),
    SHULIJP("http://www.shulijp.com/tool/ajaxpost","云间",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{url: \"${url}\"," +
            "seltype: \"${method}\",ck:\"\",header:\"\",parms:\"\",proxy:\"\",code:\"utf8\",cy:1,ct:\"\" }","data.response",EnctypeEnum.MULTIPART),

    TOOLFK("https://www.toolfk.com/toolfk-http-curl","toolfk",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{\n" +
            "  \"modules\": \"\",\n" +
            "  \"hide-method \": \",${hide-method}\",\n" +
            "  \"hide-link\": \"${url}\",\n" +
            "  \"body_json \": \"\",\n" +
            "  \"result \": \"\"\n" +
            "}","data",EnctypeEnum.URLENCODED),
    VISO3("https://c.3viso.cn/http.php?execute=1","VISO3",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST),"{url: \"${url}\"," +
            "method: \"${method}\",\"crypt_code\":\"\" }","message",EnctypeEnum.URLENCODED),


    WXAI("https://post.wxai.club/httpapi.php", "https://post.wxai.club/",
            Lists.newArrayList(MethodEnum.GET, MethodEnum.POST), "{\"tourl\":\"${url}\",\"parms\":\"\",\"header\":\"Content-Type:application/x-www-form-urlencoded\"," +
            "\"seltype\":\"${method}\",\"code\":\"utf8\",\"jieya\":\"1\"}", "body"
            , EnctypeEnum.JSON),
    ;



    private String url;
    private String remark;

    private List<MethodEnum> methods;

    private String aipParams;

    private String resultPath;

    private EnctypeEnum enctype;

    ApiTestEnum(String url, String remark, List<MethodEnum> methods, String aipParams, String resultPath, EnctypeEnum enctype) {
        this.url = url;
        this.remark = remark;
        this.methods = methods;
        this.aipParams = aipParams;
        this.resultPath = resultPath;
        this.enctype = enctype;
    }
}
