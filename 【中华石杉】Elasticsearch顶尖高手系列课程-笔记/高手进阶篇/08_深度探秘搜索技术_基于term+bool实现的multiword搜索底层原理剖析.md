## 08-深度探秘搜索技术-基于term+bool实现的multiword搜索底层原理剖析

#### 1、普通match如何转换为term+should

```json
{
    "match": { "title": "java elasticsearch"}
}
```



使用诸如上面的match query进行多值搜索的时候，es会在底层**自动将这个match query转换为bool的语法**
bool should，指定多个搜索词，同时使用term query

```json
{
  "bool": {
    "should": [
      { "term": { "title": "java" }},
      { "term": { "title": "elasticsearch"   }}
    ]
  }
}
```



#### 2、and match如何转换为term+must

```json
{
    "match": {
        "title": {
            "query":    "java elasticsearch",
            "operator": "and"
        }
    }
}
```

```json
{
  "bool": {
    "must": [
      { "term": { "title": "java" }},
      { "term": { "title": "elasticsearch"   }}
    ]
  }
}
```



#### 3、minimum_should_match如何转换

```json
{
    "match": {
        "title": {
            "query":                "java elasticsearch hadoop spark",
            "minimum_should_match": "75%"
        }
    }
}
```



```json
{
  "bool": {
    "should": [
      { "term": { "title": "java" }},
      { "term": { "title": "elasticsearch"   }},
      { "term": { "title": "hadoop" }},
      { "term": { "title": "spark" }}
    ],
    "minimum_should_match": 3 
  }
}
```



上一讲，为啥要讲解两种实现multi-value搜索的方式呢？实际上，就是给这一讲进行铺垫的。match query --> bool + term。


