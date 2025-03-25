package com.ns.device_state_manager.utils;

import java.util.UUID;

public class UUIDUtil {
    /**
     * UUID随机生成方法
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");// 把-替换为空
    }
}
