课程大纲

1、定制dynamic策略

true：遇到陌生字段，就进行dynamic mapping
false：遇到陌生字段，就忽略
strict：遇到陌生字段，就报错

PUT /my_index
{
  "mappings": {
    "my_type": {
      "dynamic": "strict",
      "properties": {
        "title": {
          "type": "text"
        },
        "address": {
          "type": "object",
          "dynamic": "true"
        }
      }
    }
  }
}

PUT /my_index/my_type/1
{
  "title": "my article",
  "content": "this is my article",
  "address": {
    "province": "guangdong",
    "city": "guangzhou"
  }
}

{
  "error": {
    "root_cause": [
      {
        "type": "strict_dynamic_mapping_exception",
        "reason": "mapping set to strict, dynamic introduction of [content] within [my_type] is not allowed"
      }
    ],
    "type": "strict_dynamic_mapping_exception",
    "reason": "mapping set to strict, dynamic introduction of [content] within [my_type] is not allowed"
  },
  "status": 400
}

PUT /my_index/my_type/1
{
  "title": "my article",
  "address": {
    "province": "guangdong",
    "city": "guangzhou"
  }
}

GET /my_index/_mapping/my_type

{
  "my_index": {
    "mappings": {
      "my_type": {
        "dynamic": "strict",
        "properties": {
          "address": {
            "dynamic": "true",
            "properties": {
              "city": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "province": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              }
            }
          },
          "title": {
            "type": "text"
          }
        }
      }
    }
  }
}

2、定制dynamic mapping策略

（1）date_detection

默认会按照一定格式识别date，比如yyyy-MM-dd。但是如果某个field先过来一个2017-01-01的值，就会被自动dynamic mapping成date，后面如果再来一个"hello world"之类的值，就会报错。可以手动关闭某个type的date_detection，如果有需要，自己手动指定某个field为date类型。

PUT /my_index/_mapping/my_type
{
    "date_detection": false
}

（2）定制自己的dynamic mapping template（type level）

PUT /my_index
{
    "mappings": {
        "my_type": {
            "dynamic_templates": [
                { "en": {
                      "match":              "*_en", 
                      "match_mapping_type": "string",
                      "mapping": {
                          "type":           "string",
                          "analyzer":       "english"
                      }
                }}
            ]
}}}

PUT /my_index/my_type/1
{
  "title": "this is my first article"
}

PUT /my_index/my_type/2
{
  "title_en": "this is my first article"
}

title没有匹配到任何的dynamic模板，默认就是standard分词器，不会过滤停用词，is会进入倒排索引，用is来搜索是可以搜索到的
title_en匹配到了dynamic模板，就是english分词器，会过滤停用词，is这种停用词就会被过滤掉，用is来搜索就搜索不到了

（3）定制自己的default mapping template（index level）

PUT /my_index
{
    "mappings": {
        "_default_": {
            "_all": { "enabled":  false }
        },
        "blog": {
            "_all": { "enabled":  true  }
        }
    }
}

