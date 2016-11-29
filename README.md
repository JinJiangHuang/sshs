# Problem
很多情况下我们想要通过ssh连接的机器都在局域网中，传统的ssh连接无法通过设置ip和端口号进行连接--因为局域网中的一般机器是无法直接访问的。
#Solution
如果你有一个公网上的服务器，并且你需要连接一台无法直接连接的局域网内的机器。那么下面这个思路会比较适合你。

1. 公网服务器作为连接请求分发中心
2. 局域网机器轮询服务器获取连接请求操作
3. 局域网机器发现有连接请求后和服务器建立socket长连接
4. 局域网机器在本地打开终端(通过编程语言)，然后获取终端输入和输出流
5. 将终端的输入、输出流和 socket的输入、输出流关联起来
6. 公网服务器就能间接获取到局域网机器本地终端的输入输出流

以上解决方案涉及到socket 编程和IO操作

#Usage
sshs 主要使用的两个类分别是服务端的使用的```Server```类和客户端（被连接的机器）使用的```Client```类。```sshs.properties```为默认配置文件。

sshs 主要实现的是客户端机器打开终端、获取终端输入出流、与服务端建立socket和服务端获取socket输入出入流操作。不包含解决方案中的1,2步骤。因为这些操作需要调用sshs的使用者来完成

##basic connect process
1. 服务端打开端口等待客户端建立socket连接

````
MessageHandler handler = new SystemOutHandler();//终端输出字符处理器接口，SystemOutHandler将结果输出到控制台
Server server = new Server(handler);
server.start(); //非阻塞方式启动
int port = server.getSocket().getPort();//获取soket端口号，客户端需要获取此端口号来进行socket连接

````

2.客户端根据用户名和密码打开本地终端，并根据服务器的地址和端口打开socket的连接
````
String userName = "username";//linux用户名
String password = "password";//linux用户密码
String serverAddress = "localhost";//服务端地址
String serverPort = 6000;//服务端端口号
ClientConfigure conf = new ClientConfigure(userName,password,serverAddress,serverPort);
Client client = new Client(conf);
client.start();

````

3.sshs 反向连接建立后，就可以在控制台看到终端的输出

4.给终端发送命令，命令是以字符串发送的

````

String shell = "ls";
server.getSocket().send(shell);

````
#finally

最后大家遇到任何相关问题都可以给我的邮箱```cw961590280@hotmail.com```发送邮件一起讨论。
