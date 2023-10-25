package com.github.shy526.string;

import com.alibaba.fastjson.JSONObject;

import java.util.Properties;
import java.util.Set;

public class ElAnalysis {

    private final static String PREFIX = "${";
    private final static String SUFFIX = "}";

    public static String process(String str, JSONObject prop) {
        StringBuilder result = new StringBuilder(str);
        int startIndex = result.indexOf(PREFIX);
        int index = startIndex + PREFIX.length();
        while (index < result.length() && startIndex != -1) {
            if (matchSubStr(result, index, SUFFIX)) {
                String key = result.substring(startIndex + PREFIX.length(), index);
                String[] tempAry = key.split(":");
                key = tempAry[0];
                String defaultVal = tempAry.length > 1 ? tempAry[1] : null;
                String propVal = prop.getString(key);
                propVal = propVal == null ? defaultVal : propVal;
                int temp = index + SUFFIX.length();
                if (propVal != null) {
                    result.replace(startIndex, index + SUFFIX.length(), propVal);
                    temp = index + propVal.length();
                }
                startIndex = result.indexOf(PREFIX, temp);
                index = startIndex + PREFIX.length();
            } else {
                index += SUFFIX.length();
            }
        }
        return result.toString();
    }

    private static boolean matchSubStr(CharSequence source, int index, CharSequence subStr) {
        if (index + subStr.length() > source.length()) {
            return false;
        }
        for (int i = 0; i < subStr.length(); i++) {
            if (source.charAt(index + i) != subStr.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
