package com.ij34.server.msg;

import io.netty.handler.codec.mqtt.MqttSubscribeMessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @Author: lyx
 * @Description:订阅key=topic,value=LinkedList,LinkedList=SubscribeMessage1,SubscribeMessage2...SubscribeMessageN
 * @Date: 2020/11/21
 */
public class SubscribeContainer {
   public  static HashMap<String,MqttSubscribeMessage> subContainer = new HashMap<String, MqttSubscribeMessage>();

}
