课程大纲

1、重建索引

一个field的设置是不能被修改的，如果要修改一个Field，那么应该重新按照新的mapping，建立一个index，然后将数据批量查询出来，重新用bulk api写入index中

批量查询的时候，建议采用scroll api，并且采用多线程并发的方式来reindex数据，每次scoll就查询指定日期的一段数据，交给一个线程即可

（1）一开始，依靠dynamic mapping，插入数据，但是不小心有些数据是2017-01-01这种日期格式的，所以title这种field被自动映射为了date类型，实际上它应该是string类型的

PUT /my_index/my_type/3
{
  "title": "2017-01-03"
}

{
  "my_index": {
    "mappings": {
      "my_type": {
        "properties": {
          "title": {
            "type": "date"
          }
        }
      }
    }
  }
}

（2）当后期向索引中加入string类型的title值的时候，就会报错

PUT /my_index/my_type/4
{
  "title": "my first article"
}

{
  "error": {
    "root_cause": [
      {
        "type": "mapper_parsing_exception",
        "reason": "failed to parse [title]"
      }
    ],
    "type": "mapper_parsing_exception",
    "reason": "failed to parse [title]",
    "caused_by": {
      "type": "illegal_argument_exception",
      "reason": "Invalid format: \"my first article\""
    }
  },
  "status": 400
}

（3）如果此时想修改title的类型，是不可能的

PUT /my_index/_mapping/my_type
{
  "properties": {
    "title": {
      "type": "text"
    }
  }
}

{
  "error": {
    "root_cause": [
      {
        "type": "illegal_argument_exception",
        "reason": "mapper [title] of different type, current_type [date], merged_type [text]"
      }
    ],
    "type": "illegal_argument_exception",
    "reason": "mapper [title] of different type, current_type [date], merged_type [text]"
  },
  "status": 400
}

（4）此时，唯一的办法，就是进行reindex，也就是说，重新建立一个索引，将旧索引的数据查询出来，再导入新索引

（5）如果说旧索引的名字，是old_index，新索引的名字是new_index，终端java应用，已经在使用old_index在操作了，难道还要去停止java应用，修改使用的index为new_index，才重新启动java应用吗？这个过程中，就会导致java应用停机，可用性降低

（6）所以说，给java应用一个别名，这个别名是指向旧索引的，java应用先用着，java应用先用goods_index alias来操作，此时实际指向的是旧的my_index

PUT /my_index/_alias/goods_index

（7）新建一个index，调整其title的类型为string

PUT /my_index_new
{
  "mappings": {
    "my_type": {
      "properties": {
        "title": {
          "type": "text"
        }
      }
    }
  }
}

（8）使用scroll api将数据批量查询出来

GET /my_index/_search?scroll=1m
{
    "query": {
        "match_all": {}
    },
    "sort": ["_doc"],
    "size":  1
}

{
  "_scroll_id": "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAADpAFjRvbnNUWVZaVGpHdklqOV9zcFd6MncAAAAAAAA6QRY0b25zVFlWWlRqR3ZJajlfc3BXejJ3AAAAAAAAOkIWNG9uc1RZVlpUakd2SWo5X3NwV3oydwAAAAAAADpDFjRvbnNUWVZaVGpHdklqOV9zcFd6MncAAAAAAAA6RBY0b25zVFlWWlRqR3ZJajlfc3BXejJ3",
  "took": 1,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": null,
    "hits": [
      {
        "_index": "my_index",
        "_type": "my_type",
        "_id": "2",
        "_score": null,
        "_source": {
          "title": "2017-01-02"
        },
        "sort": [
          0
        ]
      }
    ]
  }
}

（9）采用bulk api将scoll查出来的一批数据，批量写入新索引

POST /_bulk
{ "index":  { "_index": "my_index_new", "_type": "my_type", "_id": "2" }}
{ "title":    "2017-01-02" }

（10）反复循环8~9，查询一批又一批的数据出来，采取bulk api将每一批数据批量写入新索引

（11）将goods_index alias切换到my_index_new上去，java应用会直接通过index别名使用新的索引中的数据，java应用程序不需要停机，零提交，高可用

POST /_aliases
{
    "actions": [
        { "remove": { "index": "my_index", "alias": "goods_index" }},
        { "add":    { "index": "my_index_new", "alias": "goods_index" }}
    ]
}

（12）直接通过goods_index别名来查询，是否ok

GET /goods_index/my_type/_search

2、基于alias对client透明切换index

PUT /my_index_v1/_alias/my_index

client对my_index进行操作

reindex操作，完成之后，切换v1到v2

POST /_aliases
{
    "actions": [
        { "remove": { "index": "my_index_v1", "alias": "my_index" }},
        { "add":    { "index": "my_index_v2", "alias": "my_index" }}
    ]
}

