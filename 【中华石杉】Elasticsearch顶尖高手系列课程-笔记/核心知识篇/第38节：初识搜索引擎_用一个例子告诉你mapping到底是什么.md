课程大纲

插入几条数据，让es自动为我们建立一个索引

PUT /website/article/1
{
  "post_date": "2017-01-01",
  "title": "my first article",
  "content": "this is my first article in this website",
  "author_id": 11400
}

PUT /website/article/2
{
  "post_date": "2017-01-02",
  "title": "my second article",
  "content": "this is my second article in this website",
  "author_id": 11400
}

PUT /website/article/3
{
  "post_date": "2017-01-03",
  "title": "my third article",
  "content": "this is my third article in this website",
  "author_id": 11400
}

尝试各种搜索

GET /website/article/_search?q=2017			3条结果             
GET /website/article/_search?q=2017-01-01        	3条结果
GET /website/article/_search?q=post_date:2017-01-01   	1条结果
GET /website/article/_search?q=post_date:2017         	1条结果

查看es自动建立的mapping，带出什么是mapping的知识点
自动或手动为index中的type建立的一种数据结构和相关配置，简称为mapping
dynamic mapping，自动为我们建立index，创建type，以及type对应的mapping，mapping中包含了每个field对应的数据类型，以及如何分词等设置
我们当然，后面会讲解，也可以手动在创建数据之前，先创建index和type，以及type对应的mapping

GET /website/_mapping/article

{
  "website": {
    "mappings": {
      "article": {
        "properties": {
          "author_id": {
            "type": "long"
          },
          "content": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "post_date": {
            "type": "date"
          },
          "title": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          }
        }
      }
    }
  }
}

搜索结果为什么不一致，因为es自动建立mapping的时候，设置了不同的field不同的data type。不同的data type的分词、搜索等行为是不一样的。所以出现了_all field和post_date field的搜索表现完全不一样。
