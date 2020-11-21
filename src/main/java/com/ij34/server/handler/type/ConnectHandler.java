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
         MqttConnectMessage msg = (MqttConnectMessage) obj;
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
             log.error("非规范MQTT协议");
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
         return response(MqttConnectReturnCode.CONNECTION_ACCEPTED, true);
     }

    // 设置响应报文
    private MqttConnAckMessage response(MqttConnectReturnCode connectReturnCode, boolean sessionPresent) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);

        return connAckMessage;
    }


}
