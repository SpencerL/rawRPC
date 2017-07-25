## 基于阻塞IO的简单RPC框架(同步和异步实现)
1. 服务端提供了阻塞IO和异步IO(基于Selector,单线程)两种实现
2. 提供了Provider端和Consumer端的hook
3. 使用Java自带序列化
#### 服务端
1. <code>mvn clean package</code>
2. <code>./server.sh yourport</code>
#### 客户端
1. <code>mvn clean package</code>
2. <code>./client yourhost yourport</code>

### TODO
结合线程池和Selector,一个线程池负责监听,注册connect事件，另一个线程池处理读写。


