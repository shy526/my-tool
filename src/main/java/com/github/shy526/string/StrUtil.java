package com.github.shy526.string;


import java.util.regex.Pattern;


/**
 * 收集一些常见的字符串处理方式
 *
 * @author shy526
 */
public class StrUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");

    private static final Pattern IP_PATTERN = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");

    public static boolean assertPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean assertIp(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }

    public static String phoneHide(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
