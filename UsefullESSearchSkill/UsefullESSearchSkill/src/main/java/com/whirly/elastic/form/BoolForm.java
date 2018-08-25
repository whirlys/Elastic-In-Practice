package com.whirly.elastic.form;

import java.util.List;

/**
 * @program: elastic
 * @description: 4、Bool检索( Bool Query) 的参数类
 * @author: 赖键锋
 * @create: 2018-08-23 21:08
 **/
public class BoolForm {
    private List<String> shouldTitles;
    private List<String> mustAuthors;
    private List<String> mustNotAuthors;

    public BoolForm(List<String> shouldTitles, List<String> mustAuthors, List<String> mustNotAuthors) {
        this.shouldTitles = shouldTitles;
        this.mustAuthors = mustAuthors;
        this.mustNotAuthors = mustNotAuthors;
    }

    public List<String> getShouldTitles() {
        return shouldTitles;
    }

    public void setShouldTitles(List<String> shouldTitles) {
        this.shouldTitles = shouldTitles;
    }

    public List<String> getMustAuthors() {
        return mustAuthors;
    }

    public void setMustAuthors(List<String> mustAuthors) {
        this.mustAuthors = mustAuthors;
    }

    public List<String> getMustNotAuthors() {
        return mustNotAuthors;
    }

    public void setMustNotAuthors(List<String> mustNotAuthors) {
        this.mustNotAuthors = mustNotAuthors;
    }
}
