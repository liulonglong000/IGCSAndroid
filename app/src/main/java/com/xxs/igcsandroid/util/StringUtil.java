package com.xxs.igcsandroid.util;

public class StringUtil {
    public static boolean isStringNullOrEmpty(String strSrc) {
        if (strSrc == null) {
            return true;
        }
        if (strSrc.length() == 0) {
            return true;
        }
        return false;
    }

    public static String getStringNotNull(String src) {
        if (src == null) {
            return "";
        }
        return src;
    }
}
