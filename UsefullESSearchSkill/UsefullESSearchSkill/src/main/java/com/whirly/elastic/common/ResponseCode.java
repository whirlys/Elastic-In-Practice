package com.whirly.elastic.common;

/**
 * @program: elastic
 * @description: 响应码
 * @author: 赖键锋
 * @create: 2018-08-22 00:45
 **/
public enum ResponseCode {

    OK(200, "请求成功"),
    MOVEDPERMANENTLY(301, "重定向"),
    NOTFOUND(404, "资源不存在"),
    INTERNALSERVERERROR(500, "服务器内部错误"),
    FAILEDSHARDS(551, "ES有shard请求失败，数据可能不完整"),
    ESTIMEOUT(552, "ES超时");


    private int code;
    private String value;

    ResponseCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
