## 简单RPC框架(同步和异步实现)
1. 服务端提供了阻塞IO,异步IO(基于Selector,单线程)，异步+线程池３种实现
2. 异步+线程池实现(模仿netty)，一个服务控制线程负责监听新的连接事件，然后转交给Worker线程处理（线程池）,
    worker线程内部也用selector监听读ready和write ready，并触发读写操作。该实现提供了timeout超时设置
3. 提供了Provider端和Consumer端的hook
4. 使用Java自带序列化
#### 服务端
1. <code>mvn clean package</code>
2. <code>./server.sh yourport</code>
#### 客户端
1. <code>mvn clean package</code>
2. <code>./client yourhost yourport</code>

### TODO
修改一个channel一个selector的方式，使得一个线程内部共享一个selector,供多个channel使用


