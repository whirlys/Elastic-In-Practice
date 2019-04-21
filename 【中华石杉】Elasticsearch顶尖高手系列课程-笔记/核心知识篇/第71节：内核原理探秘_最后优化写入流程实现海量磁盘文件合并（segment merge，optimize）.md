课程大纲

每秒一个segment file，文件过多，而且每次search都要搜索所有的segment，很耗时

默认会在后台执行segment merge操作，在merge的时候，被标记为deleted的document也会被彻底物理删除

每次merge操作的执行流程

（1）选择一些有相似大小的segment，merge成一个大的segment
（2）将新的segment flush到磁盘上去
（3）写一个新的commit point，包括了新的segment，并且排除旧的那些segment
（4）将新的segment打开供搜索
（5）将旧的segment删除

POST /my_index/_optimize?max_num_segments=1，尽量不要手动执行，让它自动默认执行就可以了