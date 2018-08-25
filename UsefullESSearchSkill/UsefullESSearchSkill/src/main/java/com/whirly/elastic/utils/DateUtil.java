package com.whirly.elastic.utils;

import com.whirly.elastic.bean.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: elastic
 * @description: 获取数据工具类
 * @author: 赖键锋
 * @create: 2018-08-21 20:53
 **/
public class DateUtil {

    public static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 模拟获取数据
     */
    public static List<Book> batchData() {
        List<Book> list = new LinkedList<>();


        Book book1 = new Book("1", "Elasticsearch: The Definitive Guide", Arrays.asList("clinton gormley", "zachary tong"),
                "A distibuted real-time search and analytics engine", "2015-02-07", 20, "oreilly");

        Book book2 = new Book("2", "Taming Text: How to Find, Organize, and Manipulate It", Arrays.asList("grant ingersoll", "thomas morton", "drew farris"),
                "organize text using approaches such as full-text search, proper name recognition, clustering, tagging, information extraction, and summarization",
                "2013-01-24", 12, "manning");

        Book book3 = new Book("3", "Elasticsearch in Action", Arrays.asList("radu gheorge", "matthew lee hinman", "roy russo"),
                "build scalable search applications using Elasticsearch without having to do complex low-level programming or understand advanced data science algorithms",
                "2015-12-03", 18, "manning");


        Book book4 = new Book("4", "Solr in Action", Arrays.asList("trey grainger", "timothy potter"), "Comprehensive guide to implementing a scalable search engine using Apache Solr",
                "2014-04-05", 23, "manning");

        list.add(book1);
        list.add(book2);
        list.add(book3);
        list.add(book4);

        return list;
    }

    public static Date parseDate(String dateStr) {
        try {
            return dateFormater.parse(dateStr);
        } catch (ParseException e) {

        }
        return null;
    }
}
