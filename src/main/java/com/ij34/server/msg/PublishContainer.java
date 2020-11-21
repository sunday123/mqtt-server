package com.ij34.server.msg;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: lyx
 * @Description:发布消息容器
 * @Date: 2020/11/21
 */
public class PublishContainer {
    static Logger log = Logger.getLogger(PublishContainer.class);
    static HashMap<String, MqttPublishMessage> container = new HashMap<String, MqttPublishMessage>();

    public static HashMap<String, MqttPublishMessage> getContainer() {
        return container;
    }

    public static  void setContainer(String topic ,MqttPublishMessage msg) {
        log.info("发布："+topic);
        container.put(topic,msg);
    }
}
