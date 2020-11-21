package com.ij34.server.handler.type;

import com.ij34.server.global.ApplicationContext;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class DisconnectHandler {
    Logger log = Logger.getLogger(DisconnectHandler.class);
    private static DisconnectHandler ourInstance = new DisconnectHandler();

    public static DisconnectHandler getInstance() {
        return ourInstance;
    }

    private DisconnectHandler() {
    }

    public void deleteMessage(Channel channel) {
        String channelId = channel.id().asLongText();
        log.info("MQTT DISCONNECT " + channelId+"|"+channel.remoteAddress().toString());
        ApplicationContext.getMqttConnectMsgs().remove(channel.remoteAddress());
    }
}
