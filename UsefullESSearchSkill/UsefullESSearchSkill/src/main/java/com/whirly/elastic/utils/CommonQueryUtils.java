package com.whirly.elastic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whirly.elastic.bean.Book;
import com.whirly.elastic.common.Response;
import com.whirly.elastic.common.ResponseCode;
import com.whirly.elastic.common.ResponsePage;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.util.LinkedList;
import java.util.List;

/**
 * @program: elastic
 * @description: 抽取出公共的执行查询和解析数据的代码
 * @author: 赖键锋
 * @create: 2018-08-23 15:46
 **/
public class CommonQueryUtils {

    public static Gson gson = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    /**
     * 处理ES返回的数据，封装
     */
    public static List<Book> parseResponse(SearchResponse searchResponse) {
        List<Book> list = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // 用gson直接解析
            Book book = gson.fromJson(hit.getSourceAsString(), Book.class);
            // 自己解析
            /*Book book = new Book();
            Map<String, Object> source = hit.getSourceAsMap();
            book.setId(EsSourceFieldParser.mapToString(source, "id"));
            book.setTitle(EsSourceFieldParser.mapToString(source, "title"));
            book.setAuthors(EsSourceFieldParser.matToList(source, "authors"));
            book.setNum_reviews(EsSourceFieldParser.mapToInteger(source, "num_reviews"));
            book.setPublish_date(EsSourceFieldParser.mapToDate(source, "publish_date", DateUtil.dateFormater));
            book.setPublisher(EsSourceFieldParser.mapToString(source, "publisher"));
            book.setSummary(EsSourceFieldParser.mapToString(source, "summary"));*/
            list.add(book);
        }
        return list;
    }

    /**
     * 解析完数据后，构建 Response 对象
     */
    public static Response<List<Book>> buildResponse(SearchResponse searchResponse) {
        // 超时处理
        if (searchResponse.isTimedOut()) {
            return new Response<>(ResponseCode.ESTIMEOUT);
        }
        // 处理ES返回的数据
        List<Book> list = parseResponse(searchResponse);
        // 有shard执行失败
        if (searchResponse.getFailedShards() > 0) {
            return new Response<>(ResponseCode.FAILEDSHARDS, list);
        }
        return new Response<>(ResponseCode.OK, list);
    }

    /**
     * 解析完数据后，构建 ResponsePage 对象
     */
    public static ResponsePage<List<Book>> buildResponsePage(SearchResponse searchResponse, Integer from, Integer size, Integer total) {
        // 超时
        if (searchResponse.isTimedOut()) {
            return new ResponsePage<>(ResponseCode.ESTIMEOUT, from, size, total);
        }
        // 处理ES返回的数据
        List<Book> list = parseResponse(searchResponse);
        // 有shard执行失败
        if (searchResponse.getFailedShards() > 0) {
            return new ResponsePage<>(ResponseCode.FAILEDSHARDS, list, from, size, total);
        }
        return new ResponsePage<>(ResponseCode.OK, list, from, size, total);
    }
}
