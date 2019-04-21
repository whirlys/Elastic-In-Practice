课程大纲

搜索的时候，要依靠倒排索引；排序的时候，需要依靠正排索引，看到每个document的每个field，然后进行排序，所谓的正排索引，其实就是doc values

在建立索引的时候，一方面会建立倒排索引，以供搜索用；一方面会建立正排索引，也就是doc values，以供排序，聚合，过滤等操作使用

doc values是被保存在磁盘上的，此时如果内存足够，os会自动将其缓存在内存中，性能还是会很高；如果内存不足够，os会将其写入磁盘上


doc1: hello world you and me
doc2: hi, world, how are you

word		doc1		doc2

hello		*
world		*		*
you		*		*
and 		*
me		*
hi				*
how				*
are				*

hello you --> hello, you

hello --> doc1
you --> doc1,doc2

doc1: hello world you and me
doc2: hi, world, how are you

sort by age


doc1: { "name": "jack", "age": 27 }
doc2: { "name": "tom", "age": 30 }

document	name		age

doc1		jack		27
doc2		tom		30	




	