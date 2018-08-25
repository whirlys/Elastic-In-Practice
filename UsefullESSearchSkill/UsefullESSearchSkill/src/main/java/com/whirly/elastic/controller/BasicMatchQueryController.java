package com.whirly.elastic.controller;

import com.whirly.elastic.bean.Book;
import com.whirly.elastic.common.Constants;
import com.whirly.elastic.common.Response;
import com.whirly.elastic.common.ResponsePage;
import com.whirly.elastic.form.BoolForm;
import com.whirly.elastic.form.MatchForm;
import com.whirly.elastic.service.BasicMatchQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: elastic
 * @description: Basic Match Query 全文搜索
 * @author: 赖键锋
 * @create: 2018-08-22 00:22
 **/
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
