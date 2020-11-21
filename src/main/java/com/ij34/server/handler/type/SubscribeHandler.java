package com.ij34.server.handler.type;

import com.ij34.server.msg.PackageIdGenerator;
import com.ij34.server.msg.PublishContainer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class SubscribeHandler {
    Logger log = Logger.getLogger(SubscribeHandler.class);

    private static SubscribeHandler ourInstance = new SubscribeHandler();

    public static SubscribeHandler getInstance() {
        return ourInstance;
    }

    private SubscribeHandler() {
    }

    public MqttMessage doMessage(Channel channel, Object obj) {
        MqttMessage msg = (MqttMessage) obj;
        String channelId = channel.id().asLongText();
        log.info("MQTT订阅 " + channelId);

        // --可变报头--
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = messageIdVariableHeader.messageId();

        // --有效载荷--
        MqttSubscribePayload subscribePayload = (MqttSubscribePayload) msg.payload();

        MqttQoS qos = null;
        String topicName;
        MqttPublishMessage message;

        for (MqttTopicSubscription  topicSubscription : subscribePayload.topicSubscriptions()) {

            qos = topicSubscription.qualityOfService();
            log.info("qos:"+qos);
            topicName = topicSubscription.topicName();

            // publish符合主题的消息至当前channel

            message = PublishContainer.getContainer().get(topicName);
            if (message != null) { //发送
                PublishContainer.setContainer(topicName,message);
                int packetId = PackageIdGenerator.generator();
                MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, qos, true, 0);
                MqttPublishVariableHeader publishVariableHeader = new MqttPublishVariableHeader(topicName, packetId);
                MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, publishVariableHeader, Unpooled.copiedBuffer(topicSubscription.topicName().getBytes()));
                PublishContainer.setContainer(topicName,message);
            }
        }
        
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false,msg.fixedHeader().qosLevel(), true, 0);
        messageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload subAckPayload = new MqttSubAckPayload(qos==null? MqttQoS.AT_LEAST_ONCE.value():qos.value());
        MqttSubAckMessage subAckMessage = new MqttSubAckMessage(fixedHeader, messageIdVariableHeader, subAckPayload);

        return subAckMessage;
    }
}
