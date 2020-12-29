package com.ij34.server.handler.type;

import com.ij34.server.global.ApplicationContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.apache.log4j.Logger;

import java.net.SocketAddress;


/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class ConnectHandler {
    Logger log = Logger.getLogger(ConnectHandler.class);
    private static ConnectHandler instance = new ConnectHandler();

    public static ConnectHandler getInstance() {

        return instance;
    }

    public MqttMessage doMessage(Channel channel, Object obj) throws Exception {
        MqttMessage msg = (MqttMessage) obj;
        SocketAddress address= channel.remoteAddress();
        String  channelID = channel.id().asLongText();
        log.info("连接ID："+channelID+"|"+address.toString());

        if(ApplicationContext.getMqttConnectMsgs().containsKey(address)){
            log.error("重复发送连接命令，关闭已存在的channel");
             throw new MqttIdentifierRejectedException("重复发送连接命令");
        }

         // --可变报头--
         MqttConnectVariableHeader connectVariableHeader = (MqttConnectVariableHeader) msg.variableHeader();
         String protocolName = connectVariableHeader.name();
         if (!protocolName.equalsIgnoreCase("MQTT")) { // 非规范MQTT协议
             log.error("非规范MQTT协议:"+protocolName);
             throw new MqttIdentifierRejectedException("非规范MQTT协议");   // 直接网络关闭连接
         }
         // 检查MQTT协议版本
         if (connectVariableHeader.version() != MqttVersion.MQTT_3_1_1.protocolLevel()) {    // MQTT协议版本不支持
             log.error("检查MQTT协议版本false");
             return response(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false);
         }

         // --有效载荷--
         MqttConnectPayload connectPayload = (MqttConnectPayload) msg.payload();

         String clientId = connectPayload.clientIdentifier();
         // 非法clientId
         if (clientId == null || clientId.length() <= 0) {
             log.error("非法clientId");
             return response(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
         }

         ApplicationContext.setMqttConnectMsgs(address.toString(),  msg);
         log.info("成功加入："+address.toString());
        MqttConnAckMessage ackMessage =response(MqttConnectReturnCode.CONNECTION_ACCEPTED, true);
/*
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE){
            log.info("收到的客户端的是QoS 0 不回复");
        }else if(msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE){
            log.info("收到的是QoS 1"); // 相对于QoS 0而言Qos 1增加了ack确认机制，发送者（publisher）推送消息到MQTT代理（broker）时，两者自身都会先持久化消息，只有当publisher 或者 Broker分别收到 PUBACK确认时，才会删除自身持久化的消息，否则就会重发。
//但有个问题，尽管我们可以通过确认来保证一定收到客户端 或 服务器的message，可我们却不能保证仅收到一次message，也就是当客户端publisher没收到Broker的puback或者 Broker没有收到subscriber的puback，那么就会一直重发
            channel.writeAndFlush(ackMessage);
        }else if(msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE){
            log.info("收到的是QoS 2");
            //暂时没处理
        }
*/





         return ackMessage;
     }

    // 设置响应报文
    private MqttConnAckMessage response(MqttConnectReturnCode connectReturnCode, boolean sessionPresent) {
        //isDup 保证消息可靠传输，默认为0，只占用一个字节，表示第一次发送。不能用于检测消息重复发送等。只适用于客户端或服务器端尝试重发PUBLISH, PUBREL, SUBSCRIBE 或 UNSUBSCRIBE消息
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);

        return connAckMessage;
    }


}
