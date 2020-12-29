package com.ij34.server.handler.type;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class UnSubscribeHandler {
    static final Logger log = Logger.getLogger(UnSubscribeHandler.class);

    private static UnSubscribeHandler ourInstance = new UnSubscribeHandler();

    public static UnSubscribeHandler getInstance() {
        return ourInstance;
    }

    private UnSubscribeHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        String channelId = channel.id().asLongText();
        log.debug("MQTT 取消订阅 " + channelId);

        // --可变报头--
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = messageIdVariableHeader.messageId();

        // --有效载荷--
        MqttUnsubscribePayload mqttUnsubscribePayload = (MqttUnsubscribePayload) msg.payload();
        List<String> topics = mqttUnsubscribePayload.topics();
        for (String topic : topics) {
            log.info("topic:"+topic);
        }

        // --响应报文--
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        messageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttUnsubAckMessage unsubAckMessage = new MqttUnsubAckMessage(mqttFixedHeader, messageIdVariableHeader);

        return unsubAckMessage;
    }

}
