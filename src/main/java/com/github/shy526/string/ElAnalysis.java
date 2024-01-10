package com.github.shy526.string;

import com.alibaba.fastjson.JSONObject;

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
                String propVal = prop.getString(key);
                if (key.contains("==")) {
                    String[] tempAry = key.split("==");
                    propVal = prop.getString(tempAry[0]);
                    if (propVal != null) {
                        String[] tempAry2 = tempAry[1].split("\\?");
                        String[] reAry = tempAry2[1].split(":");
                        propVal = tempAry[0].equals(propVal) ? reAry[0] : reAry[1];
                    }
                } else {
                    String[] tempAry = key.split(":");
                    key = tempAry[0];
                    String defaultVal = tempAry.length > 1 ? tempAry[1] : null;
                    propVal = prop.getString(key);
                    propVal = propVal == null ? defaultVal : propVal;
                }
                int temp = index + SUFFIX.length();
                if (propVal != null) {
                    result.replace(startIndex, index + SUFFIX.length(), propVal);
                    temp = startIndex + propVal.length();
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
