package com.ij34.server.handler.type;

import com.ij34.server.msg.PackageIdGenerator;
import com.ij34.server.msg.PublishContainer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class PublishHandler {
    private static final Logger log = Logger.getLogger(PublishHandler.class);

    private static PublishHandler ourInstance = new PublishHandler();

    public static PublishHandler getInstance() {
        return ourInstance;
    }

    private PublishHandler() {
    }


    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        log.info("MQTT 发布");

        // --固定报头--
        MqttFixedHeader fixedHeader = msg.fixedHeader();

        boolean isRetain = fixedHeader.isRetain();  // 1-保留主题；0-不保留主题；
        MqttQoS qoS = fixedHeader.qosLevel();

        // --可变报头--
        MqttPublishVariableHeader publishVariableHeader = (MqttPublishVariableHeader) msg.variableHeader();
        log.info(publishVariableHeader==null);
        if(publishVariableHeader==null){
           log.info(msg);
        }



        String topicName = publishVariableHeader.topicName();
        ByteBuf payload = (ByteBuf) msg.payload();
        String playLoadStr=payload.toString(Charset.forName("UTF-8"));
        log.info("发布主题:" + topicName + ",payload:" + playLoadStr);

        int uuid = PackageIdGenerator.generator();
        MqttFixedHeader fixedHeader2 = new MqttFixedHeader(MqttMessageType.PUBLISH, false, qoS, isRetain, 0);
        MqttPublishVariableHeader publishVariableHeader2 = new MqttPublishVariableHeader(topicName, uuid);
        MqttPublishMessage publishMessage2 = new MqttPublishMessage(fixedHeader2, publishVariableHeader2, payload);
       channel.writeAndFlush(publishMessage2);
        if (isRetain) {      // 保存需要retain的主题
           log.info("保留："+topicName+"|"+playLoadStr+"|"+ fixedHeader.qosLevel());
           PublishContainer.setContainer(topicName,publishMessage2);
        }else {
            PublishContainer.getContainer().remove(topicName);
            log.info("不保留");
        }

        // 回复PUBACK报文
            log.info("PUBREC报文:"+qoS);
            fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE/*qoS*/, isRetain, 0);
            MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, MqttMessageIdVariableHeader.from(1));
            return pubAckMessage;

    }
}
