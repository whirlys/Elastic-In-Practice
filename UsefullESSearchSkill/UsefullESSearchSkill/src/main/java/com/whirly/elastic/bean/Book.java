package com.whirly.elastic.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @program: UsefullESSearchSkill
 * @description: Book实体类
 * @author: 赖键锋
 * @create: 2018-08-21 20:44
 **/
public class Book {

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String id;

    private String title;

    private List<String> authors;

    private String summary;

    private String publish_date;

    private Integer num_reviews;

    private String publisher;

    public Book(String id, String title, List<String> authors, String summary, String publish_date, Integer num_reviews, String publisher) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.summary = summary;
        this.publish_date = publish_date;
        this.num_reviews = num_reviews;
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublish_date() {
        return publish_date;
    }


    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public Integer getNum_reviews() {
        return num_reviews;
    }

    public void setNum_reviews(Integer num_reviews) {
        this.num_reviews = num_reviews;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", summary='" + summary + '\'' +
                ", publish_date=" + publish_date +
                ", num_reviews=" + num_reviews +
                ", publisher='" + publisher + '\'' +
                '}';
    }
}
