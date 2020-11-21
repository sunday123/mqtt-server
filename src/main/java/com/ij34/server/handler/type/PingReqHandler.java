package com.ij34.server.handler.type;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.apache.log4j.Logger;

public class PingReqHandler {

    private static final Logger log = Logger.getLogger(PingReqHandler.class);

    private static PingReqHandler ourInstance = new PingReqHandler();

    public static PingReqHandler getInstance() {
        return ourInstance;
    }

    private PingReqHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        String channelId = channel.id().asLongText();
        log.debug("MQTT PING心跳包 " + channelId+"---"+channel.remoteAddress().toString());


        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage message = new MqttMessage(fixedHeader);
        return message;
    }

}
