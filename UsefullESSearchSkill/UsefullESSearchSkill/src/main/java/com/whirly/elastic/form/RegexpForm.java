package com.whirly.elastic.form;

/**
 * @program: elastic
 * @description: 接收 正则表达式 字符串
 * @author: 赖键锋
 * @create: 2018-08-23 23:44
 **/
public class RegexpForm {
    private String regexp;

    public RegexpForm(String regexp) {
        this.regexp = regexp;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }
}
