package com.ij34.server.global;

import io.netty.handler.codec.mqtt.MqttMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class ApplicationContext {
    Logger log = Logger.getLogger(ApplicationContext.class);
    //KEY:127.0.0.1:50774 或msgID
    // VALUE:MqttConnectMessage[fixedHeader=MqttFixedHeader[messageType=CONNECT, isDup=false, qosLevel=AT_MOST_ONCE, isRetain=false, remainingLength=68],
    // variableHeader=MqttConnectVariableHeader[name=MQTT, version=4, hasUserName=true, hasPassword=true, isWillRetain=false, isWillFlag=false, isCleanSession=true, keepAliveTimeSeconds=60],
    // payload=MqttConnectPayload[clientIdentifier=0b7febdf-cd6e-4d67-bb91-490621ce22ea, willTopic=null, willMessage=null, userName=username, password=[112, 97, 115, 115, 119, 111, 114, 100]]]
    public static HashMap<String, MqttMessage> mqttConnectMsgs = new HashMap<String, MqttMessage>();

    public static HashMap<String, MqttMessage> getMqttConnectMsgs() {
        return mqttConnectMsgs;
    }

    public static void setMqttConnectMsgs(String address, MqttMessage msg) {
        ApplicationContext.mqttConnectMsgs.put(address,msg);
    }
}
