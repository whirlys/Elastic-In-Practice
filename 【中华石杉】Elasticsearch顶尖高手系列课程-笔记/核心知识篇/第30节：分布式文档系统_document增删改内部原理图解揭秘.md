课程大纲

（1）客户端选择一个node发送请求过去，这个node就是coordinating node（协调节点）
（2）coordinating node，对document进行路由，将请求转发给对应的node（有primary shard）
（3）实际的node上的primary shard处理请求，然后将数据同步到replica node
（4）coordinating node，如果发现primary node和所有replica node都搞定之后，就返回响应结果给客户端