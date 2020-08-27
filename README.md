---
typora-root-url: images
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

![image-20200827201157397](/C:/Users/hukun/AppData/Roaming/Typora/typora-user-images/image-20200827201157397.png)