---

typora-copy-images-to: images
typora-root-url: ./
---

# My-RPC-Framwork

## **Version 1.0**

实现了一个最简的RPC架构

项目结构

- rpc-api文件夹：服务端与客户端的公共调用接口
- rpc-common文件夹：项目中的一些通用的枚举类和工具类
- rpc-core文件夹：框架的核心实现
- test-client文件夹：测试用的客户端项目
- test-server文件夹：测试用的服务端项目



实现流程和原理

 1. rpc-api 中写的是服务端要实现的，以及客户端要使用的接口，具体实现在服务端实现

 2. rpc-common 里封装好Rpc请求对象以及Rpc响应对象

 3. rpc-core 框架的核心实现：

    ​	客户端没有接口的具体实现类，所以没法直接生成实例对象，我们可以通过动态代理的方式来生成实例，并且在调用方法时生成需要的Rpc请求对象并且发送给服务端

    version 1.0 使用原生Scoket实现，采用序列化字节流传输

    ​	服务端提供register方法注册接口， 简化了一下，暂时只能对外提供一个接口的调用服务，使用线程池来处理请求，

![](/images/image-20200827201157397.png)



## Version1.1



​	version1.0版本中，只能注册一个服务，这个设计非常不好，这个版本，我们把服务的注册和服务器启动分离，使得服务端可以提供多个服务。

所以我们可以创建一个容器ServiceRegistry，保存一些服务的信息，并且在获得一个服务的名字的时候可以返回这个服务。容器使用concurrentHashmap，方式使用synchronize来保证多线程下的安全。

注册服务时默认采用这个对象实现的接口的完整类名作为服务名，例如某个对象 A 实现了接口 X 和 Y，那么将 A 注册进去后，会有两个服务名 X 和 Y 对应于 A 对象。这种处理方式也就说明了某个接口只能有一个对象提供服务。



为了降低耦合度，我们也不把ServiceRegistry和某一个RpcServer绑定在一起，而是在创建RpcServer的时候传入ServiceRegistry作为整个服务的注册表

在创建 RpcServer 时需要传入一个已经注册好服务的 ServiceRegistry，而原来的 register 方法也被改成了 start 方法，因为服务的注册已经不由 RpcServer 处理了，它只需要启动就行了。

而在每一个请求处理线程（RequestHandlerThread）中也就需要传入 ServiceRegistry 了，这里把处理线程和处理逻辑分成了两个类：RequestHandlerThread 只是一个线程，从ServiceRegistry 获取到提供服务的对象后，就会把 RpcRequest 和服务对象直接交给 RequestHandler 去处理，反射等过程被放到了 RequestHandler 里。



调用流程图：

![](/images/image-20200828211747993.png)



## Version1.2

使用了netty实现了NIO的形式

为了保证通用性，我们可以把 Server 和 Client 抽象成两个接口，分别是 RpcServer 和 RpcClient

在 `DefaultServiceRegistry.java` 中，将包含注册信息的 serviceMap 和 registeredService 都改成了 static ，这样就能保证全局唯一的注册信息，并且在创建 RpcServer 时也就不需要传入了。

## 自定义协议与编解码器

![image-20200901163522580](/images/image-20200901163522580.png)

首先是 4 字节魔数，表识一个协议包。接着是 Package Type，标明这是一个调用请求还是调用响应，Serializer Type 标明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；Data Length 就是实际数据的长度，设置这个字段主要防止**粘包**，最后就是经过序列化后的实际数据，可能是 RpcRequest 也可能是 RpcResponse 经过序列化后的字节，取决于 Package Type。

JSON 序列化工具我使用的是 Jackson，在 `pom.xml` 中添加依赖即可。序列化和反序列化都比较循规蹈矩，把对象翻译成字节数组，和根据字节数组和 Class 反序列化成对象。这里有一个需要注意的点，就是在 RpcRequest 反序列化时，由于其中有一个字段是 Object 数组，在反序列化时序列化器会根据字段类型进行反序列化，而 Object 就是一个十分模糊的类型，会出现反序列化失败的现象，这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到 Object 数组中的每个实例的实际类，辅助反序列化，这就是 `handleRequest()` 方法的作用。

上面提到的这种情况不会在其他序列化方式中出现，因为其他序列化方式是转换成字节数组，会记录对象的信息，而 JSON 方式本质上只是转换成 JSON 字符串，会丢失对象的类型信息。