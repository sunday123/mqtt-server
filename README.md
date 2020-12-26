# mqtt

netty-codec-mqtt搭建mqtt服务器





***\*什么是 MQTT协议？\****

MQTT 全称(Message Queue Telemetry Transport)：一种基于发布/订阅（publish/subscribe）模式的轻量级通讯协议，通过订阅相应的主题来获取消息，是物联网（Internet of Thing）中的一个标准传输协议。

 

该协议将消息的发布者（publisher）与订阅者（subscriber）进行分离，因此可以在不可靠的网络环境中，为远程连接的设备提供可靠的消息服务，使用方式与传统的MQ有点类似。

TCP协议位于传输层，MQTT 协议位于应用层，MQTT 协议构建于TCP/IP协议上，也就是说只要支持TCP/IP协议栈的地方，都可以使用MQTT协议。





一个MQTT数据包由：固定头（Fixed header）、 可变头（Variable header）、 消息体（payload）三部分构成。



**固定头（Fixed header）：所有数据包中都有固定头，包含数据包类型及数据包的分组标识。**



**可变头（Variable header）：部分数据包类型中有可变头。**

可变头存在于这些类型的消息：PUBLISH (QoS > 0)、PUBACK、PUBREC、PUBREL、PUBCOMP、SUBSCRIBE、SUBACK、UNSUBSCRIBE、UNSUBACK







**内容消息体（Payload）：存在于部分数据包类，是客户端收到的具体消息内容**

消息体payload只存在于CONNECT、PUBLISH、SUBSCRIBE、SUBACK、UNSUBSCRIBE这几种类型的消息：
CONNECT：包含客户端的ClientId、订阅的Topic、Message以及用户名和密码。
PUBLISH：向对应主题发送消息。
SUBSCRIBE：要订阅的主题以及QoS。
SUBACK：服务器对于SUBSCRIBE所申请的主题及QoS进行确认和回复。
UNSUBSCRIBE：取消要订阅的主题。

消息的发送质量，发布者（publisher）和订阅者（subscriber）都可以指定qos等级，有QoS 0、QoS 1、QoS 2三个等级

Qos 0：At most once（至多一次）只发送一次消息

Qos 1：At least once（至少一次），相对于QoS 0而言Qos 1增加了ack确认机制

Qos 2：Exactly once（只有一次），相对于QoS 1，QoS 2升级实现了仅接受一次message，publisher 和 broker 同样对消息进行持久化，其中 **publisher 缓存了message和 对应的msgID，而 broker 缓存了 msgID**



这里面暂时没有具体去去实现三个等级











