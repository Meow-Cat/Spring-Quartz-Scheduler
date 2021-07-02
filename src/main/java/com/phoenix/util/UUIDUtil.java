package com.phoenix.util;

public class UUIDUtil {
    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }
}
