package com.example.demo.common.result;

import lombok.Data;
import com.example.demo.common.constant.CoReturnFormat;
import com.example.demo.common.exception.BusinessException;
/**
 * @Description:
 * @Author cuilidong
 * @Date 2026/2/13 下午5:55
 */
@Data
public class Result<T> {

    public Result() {
        this.code = "";
        this.message = "";
    }

    public Result(T data) {
        this.code = "";
        this.data = data;
    }

    public Result(T data, String name, String describe, String json) {
        this.code = "";
        this.data = data;
        this.name = name;
        this.describe = describe;
        this.json = json;
    }

    public Result(String name, String describe, String json) {
        this.code = "";
        this.message = "";
        this.name = name;
        this.describe = describe;
        this.json = json;
    }

    public Result(T data, String code) {
        this.code = code;
        this.message = CoReturnFormat.getMessage(code);
        this.data = data;
    }

    public Result(T data, String code, String name, String describe, String json) {
        this.code = code;
        this.message = CoReturnFormat.getMessage(code);
        this.data = data;
        this.name = name;
        this.describe = describe;
        this.json = json;
    }

    public Result(String code, BusinessException e) {
        this.code = code;
        this.message = e.getMessage();
        this.errorData = e.getErrorData();
    }

    public Result(String code, Object data) {
        this.code = code;
        this.message = CoReturnFormat.getMessage(code);
        this.errorData = data;
    }

    public Result(String code) {
        this.code = code;
        this.message = CoReturnFormat.getMessage(code);
    }

    public Result(String code, String name, String describe, String json) {
        this.code = code;
        this.message = CoReturnFormat.getMessage(code);
        this.name = name;
        this.describe = describe;
        this.json = json;
    }

    /**
     * 返回编码code
     */

    private String code;

    /**
     * 返回数据data
     */

    private T data;

    /**
     * 错误数据
     */

    private Object errorData;

    /**
     * 返回信息
     */

    private String message;

    /**
     * 请求用户名
     */
    private String name;

    /**
     * 请求描述
     */
    private String describe;

    /**
     * 请求参数json
     */
    private String json;

}
