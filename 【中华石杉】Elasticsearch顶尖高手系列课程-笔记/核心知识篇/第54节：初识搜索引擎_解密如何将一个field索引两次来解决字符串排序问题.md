课程大纲

如果对一个string field进行排序，结果往往不准确，因为分词后是多个单词，再排序就不是我们想要的结果了

通常解决方案是，将一个string field建立两次索引，一个分词，用来进行搜索；一个不分词，用来进行排序

PUT /website 
{
  "mappings": {
    "article": {
      "properties": {
        "title": {
          "type": "text",
          "fields": {
            "raw": {
              "type": "string",
              "index": "not_analyzed"
            }
          },
          "fielddata": true
        },
        "content": {
          "type": "text"
        },
        "post_date": {
          "type": "date"
        },
        "author_id": {
          "type": "long"
        }
      }
    }
  }
}

PUT /website/article/1
{
  "title": "first article",
  "content": "this is my second article",
  "post_date": "2017-01-01",
  "author_id": 110
}

{
  "took": 2,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": 1,
    "hits": [
      {
        "_index": "website",
        "_type": "article",
        "_id": "2",
        "_score": 1,
        "_source": {
          "title": "first article",
          "content": "this is my first article",
          "post_date": "2017-02-01",
          "author_id": 110
        }
      },
      {
        "_index": "website",
        "_type": "article",
        "_id": "1",
        "_score": 1,
        "_source": {
          "title": "second article",
          "content": "this is my second article",
          "post_date": "2017-01-01",
          "author_id": 110
        }
      },
      {
        "_index": "website",
        "_type": "article",
        "_id": "3",
        "_score": 1,
        "_source": {
          "title": "third article",
          "content": "this is my third article",
          "post_date": "2017-03-01",
          "author_id": 110
        }
      }
    ]
  }
}

GET /website/article/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "title.raw": {
        "order": "desc"
      }
    }
  ]
}



