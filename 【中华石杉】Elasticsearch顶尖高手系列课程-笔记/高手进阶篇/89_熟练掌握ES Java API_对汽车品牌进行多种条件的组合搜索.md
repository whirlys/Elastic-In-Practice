## 89-熟练掌握ES Java API-对汽车品牌进行多种条件的组合搜索

```java
QueryBuilder qb = boolQuery()
    .must(matchQuery("brand", "宝马"))    
    .mustNot(termQuery("name.raw", "宝马318")) 
    .should(termQuery("produce_date", "2017-01-02"))  
    .filter(rangeQuery("price").gte("280000").lt("350000"));

SearchResponse response = client.prepareSearch("car_shop")
        .setTypes("cars")
        .setQuery(qb)                
        .get();
```