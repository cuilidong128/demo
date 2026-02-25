package com.example.demo.common.constant;

import com.example.demo.common.utils.EmptyUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author cuilidong
 * @Date 2026/2/13 下午5:59
 */
public class CoReturnFormat {
    public static Map<String, String> messageMap = new HashMap<String, String>();


    //初始化状态码与文字说明,后期可以放入到数据库中，根据返回码，自定义返回信息
    static {

        put("00000", "success");

        put("400", "Bad Request!");
        put("401", "NotAuthorization");
        put("405", "Method Not Allowed");
        put("406", "Not Acceptable");
        put("500", "Internal Server Error");
        put("99999", "系统开小差,请稍后再试!");
    }

    /**
     * 获取业务返回码信息
     */
    public static String getMessage(String code) {
//    	String message = "业务编码未定义: " + code;
        String message = code;
        String obj = messageMap.get(code);
        return obj == null ? message : obj;
    }
    /**
     * 返回码放入map中
     */
    public static void putAll(Map<String, String> m) {
        messageMap.putAll(m);
    }

    /**
     * 放入到map中
     */
    public static void put(String key, String value) {
        if (EmptyUtils.isEmpty(key) || EmptyUtils.isEmpty(value)) {
            return;
        }
        messageMap.put(key, value);
    }

    /**
     * 成功
     */
    public static final String SUCCESS = "200";

    /**
     * 系统异常,请联系管理员!
     */
    public static final String SYS_ERROR = "99999";
}
