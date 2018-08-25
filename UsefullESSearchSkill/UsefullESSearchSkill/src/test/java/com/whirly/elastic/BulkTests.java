package com.whirly.elastic;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whirly.elastic.bean.Book;
import com.whirly.elastic.utils.DateUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @program: elastic
 * @description: 通过Bulk将数据批量插入ES
 * @author: 赖键锋
 * @create: 2018-08-21 22:03
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BulkTests {

    // 在Test中 Autowired需要引入包 org.elasticsearch.plugin:transport-netty4-client:6.3.2，否则异常找不到Transport类
    @Autowired
    private Client client;

    @Value("${elasticsearch.bookIndex}")
    private String bookIndex;

    @Value("${elasticsearch.bookType}")
    private String bookType;

    private Gson gson = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    /**
     * 创建索引，设置 settings，设置mappings
     */
    @Test
    public void createIndex() {
        int settingShards = 1;
        int settingReplicas = 0;

        // 判断索引是否存在，存在则删除
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().prepareExists(bookIndex).get();

        if (indicesExistsResponse.isExists()) {
            System.out.println("索引 " + bookIndex + " 存在！");
            // 删除索引，防止报异常  ResourceAlreadyExistsException[index [bookdb_index/yL05ZfXFQ4GjgOEM5x8tFQ] already exists
            DeleteIndexResponse deleteResponse = client.admin().indices().prepareDelete(bookIndex).get();
            if (deleteResponse.isAcknowledged()){
                System.out.println("索引" + bookIndex + "已删除");
            }else {
                System.out.println("索引" + bookIndex + "删除失败");
            }


        } else {
            System.out.println("索引 " + bookIndex + " 不存在！");
        }

        // 设置Settings
        CreateIndexResponse response = client.admin().indices().prepareCreate(bookIndex)
                .setSettings(Settings.builder()
                        .put("index.number_of_shards", settingShards)
                        .put("index.number_of_replicas", settingReplicas))
                .get();

        // 查看结果
        GetSettingsResponse getSettingsResponse = client.admin().indices()
                .prepareGetSettings(bookIndex).get();
        System.out.println("索引设置结果");
        for (ObjectObjectCursor<String, Settings> cursor : getSettingsResponse.getIndexToSettings()) {
            String index = cursor.key;
            Settings settings = cursor.value;
            Integer shards = settings.getAsInt("index.number_of_shards", null);
            Integer replicas = settings.getAsInt("index.number_of_replicas", null);
            System.out.println("index:" + index + ", shards:" + shards + ", replicas:" + replicas);

            Assert.assertEquals(java.util.Optional.of(settingShards), java.util.Optional.of(shards));
            Assert.assertEquals(java.util.Optional.of(settingReplicas), java.util.Optional.of(replicas));
        }
    }

    /**
     * Bulk 批量插入数据
     */
    @Test
    public void bulk() {
        List<Book> list = DateUtil.batchData();

        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        // 添加index操作到 bulk 中
        list.forEach(book -> {
            // 新版的API中使用setSource时，参数的个数必须是偶数，否则需要加上 setSource(json, XContentType.JSON)
            bulkRequestBuilder.add(client.prepareIndex(bookIndex, bookType, book.getId()).setSource(gson.toJson(book), XContentType.JSON));
        });

        BulkResponse responses = bulkRequestBuilder.get();
        if (responses.hasFailures()) {
            // bulk有失败
            for (BulkItemResponse res : responses) {
                System.out.println(res.getFailure());
            }
            Assert.assertTrue(false);
        }
    }
}
