## 说明：
## 项目要求：
1、任务实时性要求比较高;
2、同时执行任务的数量比较大。、

## 问题：
    基于数据库的持久化任务，当任务数量比较多时，任务的触发会有延时。
    同时执行的任务比较多的时候，由于执行的任务耗时较长时，执行任务的线程池线程不够时，会有阻塞，导致任务排队，有些任务超过指定的执行时间过长，错过了执行。
    
## 解决方法：
    采用redis来保存任务，服务器启动时，刚从redis中获取任务，加载到内存中，基于内存来运行。
    任务触发时，将触发的任务通过netty通道，来将要执行的任务转发出去，避免在job中执行业务造成线程阻塞的问题。

## 修改后的测试结果：
    3秒内能够执行的任务数能够达到28000左右。
