package com.vouov.vtools.document;

import com.alibaba.fastjson.JSONObject;

/**
 * JSON处理工具类
 *
 * @author yuminglong@gmail.com
 * @date 2015/3/9
 */
public class JsonUtils {
    /**
     * 获取对象的值,支持形如: user.address.city
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static String getJSONValue(JSONObject jsonObject, String key) {
        String value = null;
        String[] strings = key.split("\\.");
        JSONObject object = jsonObject;
        if (strings != null && strings.length > 0) {
            for (int i = 0; i < strings.length; i++) {
                if (i != (strings.length - 1)) {
                    object = object.getJSONObject(strings[i]);
                    if (object == null) return "";
                } else {
                    value = object.getString(strings[i]);
                }
            }
        }
        return value;
    }
}
