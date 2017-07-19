## 基于阻塞IO的简单RPC框架
提供了Provider端和Consumer端的hook
#### 服务端
1. <code>mvn clean package</code>
2. <code>java -cp target/rawrpc-1.0-SNAPSHOT.jar rpc.test.RpcProvider  <port></code>
#### 客户端
1. <code>mvn clean package</code>
2. <code>java -cp target/rawrpc-1.0-SNAPSHOT.jar rpc.test.RpcConsumer <host> <port></code>

### TODO
采用netty NIO 代替阻塞IO


