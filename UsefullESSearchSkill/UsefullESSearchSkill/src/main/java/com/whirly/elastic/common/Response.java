package com.whirly.elastic.common;

import java.io.Serializable;

/**
 * @program: elastic
 * @description: 响应类
 * @author: 赖键锋
 * @create: 2018-08-22 00:36
 **/
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 8345727877985282484L;
    private T result;
    private int statusCode;
    private String message;

    public Response() {
    }

    public Response(T result) {
        this.result = result;
        this.statusCode = ResponseCode.OK.getCode();
        this.message = ResponseCode.OK.getValue();
    }

    public Response(ResponseCode responseCode) {
        this.statusCode = responseCode.getCode();
        this.message = responseCode.getValue();
    }

    public Response(ResponseCode responseCode, T result) {
        this.statusCode = responseCode.getCode();
        this.message = responseCode.getValue();
        this.result = result;
    }

    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public Response(int statusCode, String message, T result) {
        this.result = result;
        this.statusCode = statusCode;
        this.message = message;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.statusCode = responseCode.getCode();
        this.message = responseCode.getValue();
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response{" +
                "result=" + result +
                ", statusCode='" + statusCode + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
