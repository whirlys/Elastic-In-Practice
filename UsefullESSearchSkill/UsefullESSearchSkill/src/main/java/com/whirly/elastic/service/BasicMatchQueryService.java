package com.whirly.elastic.service;

import com.whirly.elastic.bean.Book;
import com.whirly.elastic.common.Constants;
import com.whirly.elastic.common.Response;
import com.whirly.elastic.common.ResponsePage;
import com.whirly.elastic.form.BoolForm;
import com.whirly.elastic.form.MatchForm;
import com.whirly.elastic.utils.CommonQueryUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.fielddata.IndexNumericFieldData;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: elastic
 * @description: BasicMatchQueryService
 * @author: 赖键锋
 * @create: 2018-08-22 00:24
 **/
@Service
public class BasicMatchQueryService {
    @Autowired
    private Client client;

    @Value("${elasticsearch.bookIndex}")
    private String bookIndex;

    @Value("${elasticsearch.bookType}")
    private String bookType;

    /**
     * 进行ES查询
     */
    private SearchResponse requestGet(String queryName, SearchRequestBuilder requestBuilder) {
        System.out.println(queryName + " 构建的查询：" + requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();
        System.out.println(queryName + " 搜索结果：" + searchResponse.toString());
        return searchResponse;
    }

    /**
     * 1.1 对 "guide" 执行全文检索
     */
    public Response<List<Book>> multiBatch(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder);

        SearchResponse searchResponse = requestGet("multiBatch", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }


    /**
     * 1.2 在标题字段(title)中搜索带有 "in action" 字样的图书
     */
    public ResponsePage<List<Book>> match(MatchForm form) {
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", form.getTitle());
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").fragmentSize(200);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType)
                .setQuery(matchQueryBuilder)
                .setFrom(form.getFrom())
                .setSize(form.getSize())
                .highlighter(highlightBuilder)
                // 设置 _source 要返回的字段
                .setFetchSource(Constants.fetchFieldsTSPD, null);

        SearchResponse searchResponse = requestGet("match", requestBuilder);

        int total = (int) searchResponse.getHits().getTotalHits();
        return CommonQueryUtils.buildResponsePage(searchResponse, form.getFrom(), form.getSize(), total);
    }

    /**
     * 2、多字段检索 (Multi-field Search)
     */
    public Response<List<Book>> multiField(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query).field("title").field("summary");

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder);

        SearchResponse searchResponse = requestGet("multiField", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 3、 Boosting提升某字段得分的检索( Boosting)
     * 将“摘要”字段的得分提高了3倍
     */
    public Response<List<Book>> multiFieldboost(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query).field("title").field("summary", 3);
        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder);

        SearchResponse searchResponse = requestGet("multiFieldboost", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 4、Bool检索( Bool Query) :
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

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(boolQuery);

        SearchResponse searchResponse = requestGet("bool", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 5、 Fuzzy 模糊检索( Fuzzy Queries)
     */
    public Response<List<Book>> fuzzy(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query)
                .field("title").field("summary")
                .fuzziness(Fuzziness.AUTO);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(queryBuilder)
                .setFetchSource(Constants.fetchFieldsTSPD, null)
                .setSize(2);

        SearchResponse searchResponse = requestGet("fuzzy", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 6、 Wildcard Query 通配符检索
     * 要查找具有以 "t" 字母开头的作者的所有记录
     */
    public Response<List<Book>> wildcard(String fieldName, String pattern) {
        WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder(fieldName, pattern);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field(Constants.AUTHORS, 200);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setTypes(bookType).setQuery(wildcardQueryBuilder)
                .setFetchSource(Constants.fetchFieldsTA, null)
                .highlighter(highlightBuilder);

        SearchResponse searchResponse = requestGet("wildcard", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 7、正则表达式检索( Regexp Query)
     */
    public Response<List<Book>> regexp(String fieldName, String regexp) {
        RegexpQueryBuilder queryBuilder = new RegexpQueryBuilder(fieldName, regexp);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field(Constants.AUTHORS);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex)
                .setQuery(queryBuilder)
                .setTypes(bookType).highlighter(highlightBuilder)
                .setFetchSource(Constants.fetchFieldsTA, null);

        SearchResponse searchResponse = requestGet("regexp", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 8、匹配短语检索( Match Phrase Query)
     */
    public Response<List<Book>> phrase(String query) {
        MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder(query)
                .field(Constants.TITLE).field(Constants.SUMMARY)
                .type(MultiMatchQueryBuilder.Type.PHRASE).slop(3);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder)
                .setFetchSource(Constants.fetchFieldsTSPD, null);

        SearchResponse searchResponse = requestGet("phrase", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 9、匹配词组前缀检索
     */
    public Response<List<Book>> phrasePrefix(String query) {
        MatchPhrasePrefixQueryBuilder queryBuilder = new MatchPhrasePrefixQueryBuilder(Constants.SUMMARY, query)
                .slop(3).maxExpansions(10);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPD, null);

        SearchResponse searchResponse = requestGet("phrasePrefix", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 10、字符串检索（ Query String）
     */
    public Response<List<Book>> queryString(String query) {
        QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder(query);
        queryBuilder.field(Constants.SUMMARY, 2).field(Constants.TITLE)
                .field(Constants.AUTHORS).field(Constants.PUBLISHER);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSA, null);

        SearchResponse searchResponse = requestGet("queryString", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);

    }

    /**
     * 11、简化的字符串检索 （Simple Query String）
     */
    public Response<List<Book>> simpleQueryString(String query) {
        SimpleQueryStringBuilder queryBuilder = new SimpleQueryStringBuilder(query);
        queryBuilder.field(Constants.SUMMARY, 2).field(Constants.TITLE)
                .field(Constants.AUTHORS).field(Constants.PUBLISHER);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSA, null)
                .highlighter(Constants.highlightS);

        SearchResponse searchResponse = requestGet("simpleQueryString", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);

    }


    /**
     * 12、Term/Terms检索（指定字段检索）
     */
    public Response<List<Book>> term(String query) {
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(Constants.PUBLISHER, query);

        // terms 查询
        /*String[] values = {"manning", "oreilly"};
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder(Constants.PUBLISHER, values);*/

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(termQueryBuilder)
                .setFetchSource(Constants.fetchFieldsTPPD, null);

        SearchResponse searchResponse = requestGet("term", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 13、Term排序检索-（Term Query - Sorted）
     */
    public Response<List<Book>> termsort(String query) {
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(Constants.PUBLISHER, query);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(termQueryBuilder)
                .addSort(Constants.PUBLISHER_KEYWORD, SortOrder.DESC)
                .addSort(Constants.TITLE_KEYWORD, SortOrder.ASC)
                .setFetchSource(Constants.fetchFieldsTPPD, null);

        SearchResponse searchResponse = requestGet("termsort", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 14、范围检索（Range query）
     */
    public Response<List<Book>> range(String startDate, String endDate) {
        RangeQueryBuilder queryBuilder = new RangeQueryBuilder(Constants.PUBLISHDATE)
                .gte(startDate).lte(endDate);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder)
                .setFetchSource(Constants.fetchFieldsTPPD, null);

        SearchResponse searchResponse = requestGet("range", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 15. 过滤检索
     */
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

        SearchResponse searchResponse = requestGet("filter", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }


    /**
     * 17、 Function 得分：Field值因子（ Function Score: Field Value Factor）
     */
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

        SearchResponse searchResponse = requestGet("fieldValueFactor", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }

    /**
     * 18、 Function 得分：衰减函数( Function Score: Decay Functions )
     */
    public Response<List<Book>> decay(String query, String origin) {
        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(query)
                .field(Constants.TITLE).field(Constants.SUMMARY);
        ExponentialDecayFunctionBuilder exp = ScoreFunctionBuilders.exponentialDecayFunction(Constants.PUBLISHDATE, origin, "30d", "7d");

        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(multiMatchQueryBuilder, exp).boostMode(CombineFunction.REPLACE);

        SearchRequestBuilder requestBuilder = client.prepareSearch(bookIndex).setTypes(bookType)
                .setQuery(queryBuilder).setFetchSource(Constants.fetchFieldsTSPN, null);

        SearchResponse searchResponse = requestGet("decay", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);

    }

    /**
     * 19、Function得分：脚本得分（ Function Score: Script Scoring ）
     */
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

        SearchResponse searchResponse = requestGet("decay", requestBuilder);

        return CommonQueryUtils.buildResponse(searchResponse);
    }
}
