课程大纲

1、root object

就是某个type对应的mapping json，包括了properties，metadata（_id，_source，_type），settings（analyzer），其他settings（比如include_in_all）

PUT /my_index
{
  "mappings": {
    "my_type": {
      "properties": {}
    }
  }
}

2、properties

type，index，analyzer

PUT /my_index/_mapping/my_type
{
  "properties": {
    "title": {
      "type": "text"
    }
  }
}

3、_source

好处

（1）查询的时候，直接可以拿到完整的document，不需要先拿document id，再发送一次请求拿document
（2）partial update基于_source实现
（3）reindex时，直接基于_source实现，不需要从数据库（或者其他外部存储）查询数据再修改
（4）可以基于_source定制返回field
（5）debug query更容易，因为可以直接看到_source

如果不需要上述好处，可以禁用_source

PUT /my_index/_mapping/my_type2
{
  "_source": {"enabled": false}
}

4、_all

将所有field打包在一起，作为一个_all field，建立索引。没指定任何field进行搜索时，就是使用_all field在搜索。

PUT /my_index/_mapping/my_type3
{
  "_all": {"enabled": false}
}

也可以在field级别设置include_in_all field，设置是否要将field的值包含在_all field中

PUT /my_index/_mapping/my_type4
{
  "properties": {
    "my_field": {
      "type": "text",
      "include_in_all": false
    }
  }
}

5、标识性metadata

_index，_type，_id



