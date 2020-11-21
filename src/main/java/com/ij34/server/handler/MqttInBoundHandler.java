package com.ij34.server.handler;


import com.ij34.server.global.ApplicationContext;
import com.ij34.server.handler.type.*;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import org.apache.log4j.Logger;

/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */
public class MqttInBoundHandler  extends ChannelInboundHandlerAdapter {
    Logger log = Logger.getLogger(MqttInBoundHandler.class);
    /*
     * channelAction
     *
     * channel 通道 action 活跃的
     *
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString() + " 通道已激活！");
    }

    /*
     * channelInactive
     *
     * channel 通道 Inactive 不活跃的
     *
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString() + " 通道不活跃！");
        ApplicationContext.getMqttConnectMsgs().remove(ctx.channel().remoteAddress().toString());

        // 关闭流

    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        MqttMessage msg = (MqttMessage) obj;
        log.info("channelRead:"+msg);
        MqttMessage mqttMessage = null;
        switch (msg.fixedHeader().messageType()){
            case CONNECT:   //连接
                mqttMessage= ConnectHandler.getInstance().doMessage(ctx.channel(), obj);
                break;
            case DISCONNECT: //断开连接
                DisconnectHandler.getInstance().deleteMessage(ctx.channel());
                ctx.close();
                break;
            case PINGREQ:
                mqttMessage= PingReqHandler.getInstance().doMessage(ctx.channel(), msg);
                break;
            case PUBLISH:       // 发布
                mqttMessage = PublishHandler.getInstance().doMessage(ctx.channel(),msg);
                break;

            case PUBACK:        // 发布回馈
                mqttMessage = PubAckHandler.getInstance().doMessage(msg);
                break;

            case SUBSCRIBE:     // 订阅
                mqttMessage = SubscribeHandler.getInstance().doMessage(ctx.channel(), msg);
                break;

            case UNSUBSCRIBE:   // 取消订阅
                mqttMessage = UnSubscribeHandler.getInstance().doMessage(ctx.channel(), msg);
                break;
            default:
                break;
        }

        if (mqttMessage != null) {//ack
            ctx.channel().writeAndFlush(mqttMessage);
        }


    }

    /**
     * 功能：读取完毕客户端发送过来的数据之后的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 第一种方法：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        // ctx.flush();
        // ctx.flush(); //
        // 第二种方法：在client端关闭channel连接，这样的话，会触发两次channelReadComplete方法。
        // ctx.flush().close().sync(); // 第三种：改成这种写法也可以，但是这中写法，没有第一种方法的好。
    }

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
        ApplicationContext.getMqttConnectMsgs().remove(ctx.channel().remoteAddress().toString());
        log.error( "异常信息：" , cause);
        MqttMessageFactory.newInvalidMessage(cause);
    }






}