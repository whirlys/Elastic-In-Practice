课程大纲

1、match all

GET /_search
{
    "query": {
        "match_all": {}
    }
}

2、match

GET /_search
{
    "query": { "match": { "title": "my elasticsearch article" }}
}

3、multi match

GET /test_index/test_type/_search
{
  "query": {
    "multi_match": {
      "query": "test",
      "fields": ["test_field", "test_field1"]
    }
  }
}

4、range query

GET /company/employee/_search 
{
  "query": {
    "range": {
      "age": {
        "gte": 30
      }
    }
  }
}

5、term query

GET /test_index/test_type/_search 
{
  "query": {
    "term": {
      "test_field": "test hello"
    }
  }
}

6、terms query

GET /_search
{
    "query": { "terms": { "tag": [ "search", "full_text", "nosql" ] }}
}

7、exist query（2.x中的查询，现在已经不提供了）


