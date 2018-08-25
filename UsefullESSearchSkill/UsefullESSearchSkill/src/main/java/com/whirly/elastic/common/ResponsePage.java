package com.whirly.elastic.common;

/**
 * @program: elastic
 * @description: Response 加入分页信息
 * @author: 赖键锋
 * @create: 2018-08-23 14:58
 **/
public class ResponsePage<T> extends Response {

    private Integer from;

    private Integer size;

    private Integer total;

    public ResponsePage() {
    }

    public ResponsePage(T result) {
        super(result);
    }

    public ResponsePage(ResponseCode responseCode) {
        super(responseCode);
    }

    public ResponsePage(ResponseCode responseCode, Integer from, Integer size, Integer total) {
        super(responseCode);
        this.setPageInfo(from, size, total);
    }

    public ResponsePage(ResponseCode responseCode, Object result) {
        super(responseCode, result);
    }

    public ResponsePage(ResponseCode responseCode, Object result, Integer from, Integer size, Integer total) {
        super(responseCode, result);
        this.setPageInfo(from, size, total);
    }

    public ResponsePage(int statusCode, String message) {
        super(statusCode, message);
    }

    public ResponsePage(int statusCode, String message, Integer from, Integer size, Integer total) {
        super(statusCode, message);
        this.setPageInfo(from, size, total);
    }

    public ResponsePage(int statusCode, String message, Object result) {
        super(statusCode, message, result);
    }

    public ResponsePage(int statusCode, String message, Object result, Integer from, Integer size, Integer total) {
        super(statusCode, message, result);
        this.setPageInfo(from, size, total);
    }

    public void setPageInfo(Integer from, Integer size, Integer total) {
        this.from = from;
        this.size = size;
        this.total = total;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
