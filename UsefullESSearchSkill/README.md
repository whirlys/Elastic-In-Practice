# 23个最有用的ES检索技巧（Java API实现）



### 前言

本文是对[ `23个最有用的Elasticseaerch检索技巧` ](http://laijianfeng.org/2018/08/%E6%95%99%E4%BD%A0%E7%BC%96%E8%AF%91%E8%B0%83%E8%AF%95Elasticsearch-6-3-2%E6%BA%90%E7%A0%81/) 一文提到的ES检索技巧进行 Java API 的简单实现，但仅限于简单实现，并不考虑包括参数校验，异常处理，日志处理，安全等问题，仅供参考

代码见 [UsefullESSearchSkill](https://github.com/whirlys/elastic-example/tree/master/UsefullESSearchSkill) ,**原查询语句请对照原文**

#### 运行环境

JDK version : 10.0.2   
gradle version : 4.7
Elasticsearch version : 6.3.2
IDEA version : 2018.2

运行前请启动 ES 实例，并修改 `application.properties` 文件中的ES配置



### 类介绍

#### 实体类 Book
注意：日期 publish_date 的类型设置为 String 是避免 Java 到 ES 之间复杂的转换工作，在ES中该字段仍然被识别为 date 类型


```
public class Book {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String id;
    private String title;
    private List<String> authors;
    private String summary;
    private String publish_date;
    private Integer num_reviews;
    private String publisher;
    ...
}
```

#### 公共类 Constants
定义了一些常用的常量

```
public class Constants {
    // 字段名

    public static String ID = "id";
    public static String TITLE = "title";
    public static String AUTHORS = "authors";
    public static String SUMMARY = "summary";
    public static String PUBLISHDATE = "publish_date";
    public static String PUBLISHER = "publisher";
    public static String NUM_REVIEWS = "num_reviews";

    public static String TITLE_KEYWORD = "title.keyword";
    public static String PUBLISHER_KEYWORD = "publisher.keyword";


    // 过滤要返回的字段

    public static String[] fetchFieldsTSPD = {ID, TITLE, SUMMARY, PUBLISHDATE};
    public static String[] fetchFieldsTA = {ID, TITLE, AUTHORS};
    public static String[] fetchFieldsSA = {ID, SUMMARY, AUTHORS};
    public static String[] fetchFieldsTSA = {ID, TITLE, SUMMARY, AUTHORS};
    public static String[] fetchFieldsTPPD = {ID, TITLE, PUBLISHER, PUBLISHDATE};
    public static String[] fetchFieldsTSPN = {ID, TITLE, SUMMARY, PUBLISHER, NUM_REVIEWS};


    // 高亮

    public static HighlightBuilder highlightS = new HighlightBuilder().field(SUMMARY);
}
```

#### 公共类 EsConfig
创建 ES 客户端实例，ES 客户端用于与 ES 集群进行交互

```
@Configuration
public class EsConfig {

    @Value("${elasticsearch.cluster-nodes}")
    private String clusterNodes;

    @Value("${elasticsearch.cluster-name}")
    private String clusterName;

    @Bean
    public Client client() {
        Settings settings = Settings.builder().put("cluster.name", clusterName)
                .put("client.transport.sniff", true).build();

        TransportClient client = new PreBuiltTransportClient(settings);
        try {
            if (clusterNodes != null && !"".equals(clusterNodes)) {
                for (String node : clusterNodes.split(",")) {
                    String[] nodeInfo = node.split(":");
                    client.addTransportAddress(new TransportAddress(InetAddress.getByName(nodeInfo[0]), Integer.parseInt(nodeInfo[1])));
                }
            }
        } catch (UnknownHostException e) {
        }

        return client;
    }
}

```

#### 数据获取工具类 DataUtil
这里的数据也就是 `23个最有用的ES检索技巧` 文中用于实验的4条数据

```
public class DataUtil {

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

```

#### 公共查询工具类 CommonQueryUtils
对执行完ES查询请求后的数据进行解析

```
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
    ...
}
```

### 数据准备

#### BulkTests
创建索引，以及使用 bulk API 批量插入数据

```
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
```

### 开始查询

#### 控制类
查询接口

```
@RestController
@RequestMapping("basicmatch")
public class BasicMatchQueryController {

    @Autowired
    private BasicMatchQueryService basicMatchQueryService;

    /**
     * 1.1 对 "guide" 执行全文检索
     * 测试：http://localhost:8080/basicmatch/multimatch?query=guide
     */
    @RequestMapping("multimatch")
    public Response<List<Book>> multiMatch(@RequestParam(value = "query", required = true) String query) {
        return basicMatchQueryService.multiBatch(query);
    }

    /**
     * 1.2 指定特定字段检索
     * 测试：http://localhost:8080/basicmatch/match?title=in action&from=0&size=4
     */
    @RequestMapping("match")
    public ResponsePage<List<Book>> match(MatchForm form) {
        return basicMatchQueryService.match(form);
    }

    /**
     * 2 对 "guide" 执行多字段检索
     * 测试：http://localhost:8080/basicmatch/multifield?query=guide
     */
    @RequestMapping("multifield")
    public Response<List<Book>> multiField(@RequestParam(value = "query", required = true) String query) {
        return basicMatchQueryService.multiField(query);
    }

    /**
     * 3、 Boosting提升某字段得分的检索( Boosting): 将“摘要”字段的得分提高了3倍
     * 测试：http://localhost:8080/basicmatch/multifieldboost?query=elasticsearch guide
     */
    @RequestMapping("multifieldboost")
    public Response<List<Book>> multiFieldboost(@RequestParam(value = "query", required = true) String query) {
        return basicMatchQueryService.multiFieldboost(query);
    }

    /**
     * 4、Bool检索( Bool Query)
     * 测试：http://localhost:8080/basicmatch/bool?shouldTitles=Elasticsearch&shouldTitles=Solr&mustAuthors=clinton gormely&mustNotAuthors=radu gheorge
     */
    @RequestMapping("bool")
    public Response<List<Book>> bool(@ModelAttribute BoolForm form) {
        return basicMatchQueryService.bool(form);
    }

    /**
     * 5、 Fuzzy 模糊检索( Fuzzy Queries)
     */
    @RequestMapping("fuzzy")
    public Response<List<Book>> fuzzy(String query) {
        return basicMatchQueryService.fuzzy(query);
    }

    /**
     * 6、 Wildcard Query 通配符检索
     * 测试：http://localhost:8080/basicmatch/wildcard?pattern=t*
     */
    @RequestMapping("wildcard")
    public Response<List<Book>> wildcard(String pattern) {
        return basicMatchQueryService.wildcard(Constants.AUTHORS, pattern);
    }


    /**
     * 7、正则表达式检索( Regexp Query)
     * 测试：http://localhost:8080/basicmatch/regexp
     */
    @RequestMapping("regexp")
    public Response<List<Book>> regexp(String regexp) {
        // 由于Tomcat的原因，直接接收有特殊字符的 正则表达式 会异常，所以这里写死，不过多探究
        // 若
        regexp = "t[a-z]*y";
        return basicMatchQueryService.regexp(Constants.AUTHORS, regexp);
    }

    /**
     * 8、匹配短语检索( Match Phrase Query)
     * 测试：http://localhost:8080/basicmatch/phrase?query=search engine
     */
    @RequestMapping("phrase")
    public Response<List<Book>> phrase(String query) {
        return basicMatchQueryService.phrase(query);
    }

    /**
     * 9、匹配词组前缀检索
     * 测试：http://localhost:8080/basicmatch/phraseprefix?query=search en
     */
    @RequestMapping("phraseprefix")
    public Response<List<Book>> phrasePrefix(String query) {
        return basicMatchQueryService.phrasePrefix(query);
    }

    /**
     * 10、字符串检索（ Query String）
     * 测试：http://localhost:8080/basicmatch/querystring?query=(saerch~1 algorithm~1) AND (grant ingersoll)  OR (tom morton)
     */
    @RequestMapping("querystring")
    public Response<List<Book>> queryString(String query) {
        return basicMatchQueryService.queryString(query);
    }

    /**
     * 11、简化的字符串检索 （Simple Query String）
     * 测试：http://localhost:8080/basicmatch/simplequerystring?query=(saerch~1 algorithm~1) AND (grant ingersoll)  OR (tom morton)
     */
    @RequestMapping("simplequerystring")
    public Response<List<Book>> simplequerystring(String query) {
        // 这里写死，仅为测试
        query = "(saerch~1 algorithm~1) + (grant ingersoll)  | (tom morton)";
        return basicMatchQueryService.simpleQueryString(query);
    }

    /**
     * 12、Term=检索（指定字段检索）
     * 测试：http://localhost:8080/basicmatch/term?query=manning
     */
    @RequestMapping("term")
    public Response<List<Book>> term(String query) {
        return basicMatchQueryService.term(query);
    }

    /**
     * 13、Term排序检索-（Term Query - Sorted）
     * 测试：http://localhost:8080/basicmatch/termsort?query=manning
     */
    @RequestMapping("termsort")
    public Response<List<Book>> termsort(String query) {
        return basicMatchQueryService.termsort(query);
    }

    /**
     * 14、范围检索（Range query）
     * 测试：http://localhost:8080/basicmatch/range?startDate=2015-01-01&endDate=2015-12-31
     */
    @RequestMapping("range")
    public Response<List<Book>> range(String startDate, String endDate) {
        return basicMatchQueryService.range(startDate, endDate);
    }

    /**
     * 15. 过滤检索
     * 测试：http://localhost:8080/basicmatch/filter?query=elasticsearch&gte=20
     */
    @RequestMapping("filter")
    public Response<List<Book>> filter(String query, Integer gte, Integer lte) {
        return basicMatchQueryService.filter(query, gte, lte);
    }

    /**
     * 17、 Function 得分：Field值因子（ Function Score: Field Value Factor）
     * 测试：http://localhost:8080/basicmatch/fieldvaluefactor?query=search engine
     */
    @RequestMapping("fieldvaluefactor")
    public Response<List<Book>> fieldValueFactor(String query) {
        return basicMatchQueryService.fieldValueFactor(query);
    }

    /**
     * 18、 Function 得分：衰减函数( Function Score: Decay Functions )
     * 测试：http://localhost:8080/basicmatch/decay?query=search engines&origin=2014-06-15
     */
    @RequestMapping("decay")
    public Response<List<Book>> decay(String query, @RequestParam(value = "origin", defaultValue = "2014-06-15") String origin) {
        return basicMatchQueryService.decay(query, origin);
    }

    /**
     * 19、Function得分：脚本得分（ Function Score: Script Scoring ）
     * 测试：ES需要配置允许groovy脚本运行才可以
     */
    @RequestMapping("script")
    public Response<List<Book>> script(String query, @RequestParam(value = "threshold", defaultValue = "2015-07-30") String threshold) {
        return basicMatchQueryService.script(query, threshold);
    }
}

```

### 服务类 

```
@Service
public class BasicMatchQueryService {
    @Autowired
    private Client client;

    @Value("${elasticsearch.bookIndex}")
    private String bookIndex;

    @Value("${elasticsearch.bookType}")
    private String bookType;

    /**
     * 进行ES查询，执行请求前后打印出 查询语句 和 查询结果
     */
    private SearchResponse requestGet(String queryName, SearchRequestBuilder requestBuilder) {
        System.out.println(queryName + " 构建的查询：" + requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();
        System.out.println(queryName + " 搜索结果：" + searchResponse.toString());
        return searchResponse;
    }
    ...
}
```

#### 1.1 对 "guide" 执行全文检索 Multi Match Query

```
    public Response<List<Book>> multiBatch(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder);

        SearchResponse searchResponse = requestGet("multiBatch", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }
```

#### 1.2 在标题字段(title)中搜索带有 "in action" 字样的图书

```
    public ResponsePage<List<Book>> match(MatchForm form) {
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", form.getTitle());
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").fragmentSize(200);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(matchQueryBuilder)
                .setFrom(form.getFrom()).setSize(form.getSize())
                .highlighter(highlightBuilder)
                // 设置 _source 要返回的字段
                .setFetchSource(Constants.fetchFieldsTSPD, null);
        ...
    }
```

#### 多字段检索 (Multi-field Search)
```
    public Response<List<Book>> multiField(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query).field("title").field("summary");

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder);
        ...
    }
```

#### 3、 Boosting提升某字段得分的检索( Boosting),将“摘要”字段的得分提高了3倍
```
    public Response<List<Book>> multiFieldboost(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query).field("title").field("summary", 3);
        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder);
        ...
    }
```

#### 4、Bool检索( Bool Query) 
```
    /**
     * 在标题中搜索一本名为 "Elasticsearch" 或 "Solr" 的书，
     * AND由 "clinton gormley" 创作，但NOT由 "radu gheorge" 创作
     */
    public Response<List<Book>> bool(BoolForm form) {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // 搜索标题 should
        BoolQueryBuilder shouldTitleBool = new BoolQueryBuilder();
        form.getShouldTitles().forEach(title -> {
            shouldTitleBool.should().add(new MatchQueryBuilder("title", title));
        });
        boolQuery.must().add(shouldTitleBool);
        // match 作者
        form.getMustAuthors().forEach(author -> {
            boolQuery.must().add(new MatchQueryBuilder("authors", author));
        });
        // not match 作者
        form.getMustNotAuthors().forEach(author -> {
            boolQuery.mustNot().add(new MatchQueryBuilder("authors", author));
        });
        ...
    }
```

#### 5、 Fuzzy 模糊检索( Fuzzy Queries)

```
    public Response<List<Book>> fuzzy(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query)
                .field("title").field("summary")
                .fuzziness(Fuzziness.AUTO);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder)
                .setFetchSource(Constants.fetchFieldsTSPD, null)
                .setSize(2);
        ...
    }
```

####  6、 Wildcard Query 通配符检索
```
    /**
     * 要查找具有以 "t" 字母开头的作者的所有记录
     */
    public Response<List<Book>> wildcard(String fieldName, String pattern) {
        WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder(fieldName, pattern);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field(Constants.AUTHORS, 200);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(wildcardQueryBuilder)
                .setFetchSource(Constants.fetchFieldsTA, null)
                .highlighter(highlightBuilder);
    }
```
#### 7、正则表达式检索( Regexp Query)
```
    public Response<List<Book>> regexp(String fieldName, String regexp) {
        RegexpQueryBuilder queryBuilder = new RegexpQueryBuilder(fieldName, regexp);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field(Constants.AUTHORS);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setQuery(queryBuilder).setTypes(bookType).highlighter(highlightBuilder)
                .setFetchSource(Constants.fetchFieldsTA, null);
    }
```

#### 8、匹配短语检索( Match Phrase Query)
```
    public Response<List<Book>> phrase(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query)
                .field(Constants.TITLE).field(Constants.SUMMARY)
                .type(MultiMatchQueryBuilder.Type.PHRASE).slop(3);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder)
                .setFetchSource(Constants.fetchFieldsTSPD, null);
    }
```

#### 9、匹配词组前缀检索
```
    public Response<List<Book>> phrasePrefix(String query) {
        MatchPhrasePrefixQueryBuilder queryBuilder = new MatchPhrasePrefixQueryBuilder(Constants.SUMMARY, query)
                .slop(3).maxExpansions(10);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPD, null);
    }
```
#### 10、字符串检索（ Query String）
```
    public Response<List<Book>> queryString(String query) {
        QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder(query);
        queryBuilder.field(Constants.SUMMARY, 2).field(Constants.TITLE)
                .field(Constants.AUTHORS).field(Constants.PUBLISHER);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSA, null);
    }
```
#### 11、简化的字符串检索 （Simple Query String）
```
    public Response<List<Book>> simpleQueryString(String query) {
        SimpleQueryStringBuilder queryBuilder = new SimpleQueryStringBuilder(query);
        queryBuilder.field(Constants.SUMMARY, 2).field(Constants.TITLE)
                .field(Constants.AUTHORS).field(Constants.PUBLISHER);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSA, null)
                .highlighter(Constants.highlightS);
    }
```
#### 12、Term/Terms检索（指定字段检索）
```
    public Response<List<Book>> term(String query) {
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(Constants.PUBLISHER, query);

        // terms 查询
        /*String[] values = {"manning", "oreilly"};
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder(Constants.PUBLISHER, values);*/

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(termQueryBuilder)
                .setFetchSource(Constants.fetchFieldsTPPD, null);
    }
```
#### 13、Term排序检索-（Term Query - Sorted）
```
    public Response<List<Book>> termsort(String query) {
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(Constants.PUBLISHER, query);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(termQueryBuilder)
                .addSort(Constants.PUBLISHER_KEYWORD, SortOrder.DESC)
                .addSort(Constants.TITLE_KEYWORD, SortOrder.ASC)
                .setFetchSource(Constants.fetchFieldsTPPD, null);
    }
```
#### 14、范围检索（Range query）
```
    public Response<List<Book>> range(String startDate, String endDate) {
        RangeQueryBuilder queryBuilder = new RangeQueryBuilder(Constants.PUBLISHDATE)
                .gte(startDate).lte(endDate);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder)
                .setFetchSource(Constants.fetchFieldsTPPD, null);
    }
```
#### 15. 过滤检索
```
    public Response<List<Book>> filter(String query, Integer gte, Integer lte) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must().add(new MultiMatchQueryBuilder(query).field(Constants.TITLE).field(Constants.SUMMARY));
        if (gte != null || lte != null) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(Constants.NUM_REVIEWS);
            if (gte != null) {
                rangeQueryBuilder.gte(gte);
            }
            if (lte != null) {
                rangeQueryBuilder.lte(lte);
            }
            queryBuilder.filter().add(rangeQueryBuilder);
        }

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPN, null);
    }
```
#### 17、 Function 得分：Field值因子（ Function Score: Field Value Factor）
```
    public Response<List<Book>> fieldValueFactor(String query) {
        // query
        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(query)
                .field(Constants.TITLE).field(Constants.SUMMARY);
        // fieldValueFactor
        FieldValueFactorFunctionBuilder fieldValueFactor = ScoreFunctionBuilders.fieldValueFactorFunction(Constants.NUM_REVIEWS)
                .factor(2).modifier(FieldValueFactorFunction.Modifier.LOG1P);
        // functionscore
        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(multiMatchQueryBuilder, fieldValueFactor);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPN, null);
    }
```
#### 18、 Function 得分：衰减函数( Function Score: Decay Functions )

```
    public Response<List<Book>> decay(String query, String origin) {
        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(query)
                .field(Constants.TITLE).field(Constants.SUMMARY);
        ExponentialDecayFunctionBuilder exp = ScoreFunctionBuilders.exponentialDecayFunction(Constants.PUBLISHDATE, origin, "30d", "7d");

        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(multiMatchQueryBuilder, exp).boostMode(CombineFunction.REPLACE);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPN, null);
    }
```

#### 19、Function得分：脚本得分（ Function Score: Script Scoring ）

```
    public Response<List<Book>> script(String query, String threshold) {
        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(query)
                .field(Constants.TITLE).field(Constants.SUMMARY);
        // 参数
        Map<String, Object> params = new HashMap<>();
        params.put("threshold", threshold);
        // 脚本
        String scriptStr = "publish_date = doc['publish_date'].value; num_reviews = doc['num_reviews'].value; if (publish_date > Date.parse('yyyy-MM-dd', threshold).getTime()) { return log(2.5 + num_reviews) }; return log(1 + num_reviews);";
        Script script = new Script(ScriptType.INLINE, "painless", scriptStr, params);

        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder = ScoreFunctionBuilders.scriptFunction(script);

        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(multiMatchQueryBuilder, scriptScoreFunctionBuilder);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPN, null);
    }
```

