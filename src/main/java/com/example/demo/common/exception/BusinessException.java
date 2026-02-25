package com.example.demo.common.exception;

import com.example.demo.common.constant.CoReturnFormat;

/**
 * @Description:
 * @Author cuilidong
 * @Date 2026/2/13 下午6:04
 */
public class BusinessException extends BaseException {

    public BusinessException(String errorCode) {
        super(errorCode, CoReturnFormat.getMessage(errorCode));
    }


    public BusinessException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }



    public BusinessException(String errorCode, Object errorData) {
        super(errorCode, CoReturnFormat.getMessage(errorCode), errorData);
    }

    public BusinessException(String errorCode, Throwable t) {
        super(errorCode, CoReturnFormat.getMessage(errorCode), t);
    }

    /**
     * 异常堆栈增加错误代码和绑定变量
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("业务异常，异常代码[").append(this.code).append("]\n");
        sb.append("异常信息:[").append(this.message).append("]\n");
        return sb.toString();
    }

}
