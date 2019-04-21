课程大纲

SearchResponse response = client.prepareSearch("index1", "index2")
        .setTypes("type1", "type2")
        .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
        .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
        .setFrom(0).setSize(60)
        .get();

需求：

（1）搜索职位中包含technique的员工
（2）同时要求age在30到40岁之间
（3）分页查询，查找第一页

GET /company/employee/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "position": "technique"
          }
        }
      ],
      "filter": {
        "range": {
          "age": {
            "gte": 30,
            "lte": 40
          }
        }
      }
    }
  },
  "from": 0,
  "size": 1
}

告诉大家，为什么刚才一边运行创建document，一边搜索什么都没搜索到？？？？

近实时！！！

默认是1秒以后，写入es的数据，才能被搜索到。很明显刚才，写入数据不到一秒，我门就在搜索。




