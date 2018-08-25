package com.whirly.elastic.form;

/**
 * @program: elastic
 * @description: match 查询参数
 * @author: 赖键锋
 * @create: 2018-08-23 14:56
 **/
public class MatchForm {

    private String title;

    private Integer size;

    private Integer from;

    public MatchForm(String title, Integer size, Integer from) {
        this.title = title;
        this.size = size;
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }
}
