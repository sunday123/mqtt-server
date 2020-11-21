package com.ij34.server.handler.type;

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.apache.log4j.Logger;

public class PubAckHandler {

    private static final Logger log = Logger.getLogger(PubAckHandler.class);

    private static PubAckHandler ourInstance = new PubAckHandler();

    public static PubAckHandler getInstance() {
        return ourInstance;
    }

    private PubAckHandler() {
    }

    public MqttMessage doMessage(MqttMessage msg) {
        log.info("MQTT PUBACK");

        MqttPublishVariableHeader publishVariableHeader = (MqttPublishVariableHeader) msg.variableHeader();
        int packetId = publishVariableHeader.packetId();
       log.info("pub删除："+packetId);

        return null;
    }

}