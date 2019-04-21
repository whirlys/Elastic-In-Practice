## 第25节：图解partial update乐观锁并发控制原理以及相关操作讲解

（1）partial update内置乐观锁并发控制   
（2）retry_on_conflict   
（3）_version   

```json
post /index/type/id/_update?retry_on_conflict=5&version=6
```