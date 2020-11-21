package com.ij34.server;

/**
 * @Author: lyx
 * @Description:
 * @Date: 2020/11/21
 */

import com.ij34.server.handler.MqttInBoundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.internal.ObjectUtil;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

public class MQTTServer {
    private final int port;

    public MQTTServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {

        URL url=this.getClass().getClassLoader().getResource("log4j.properties");
        System.out.println("url:"+url);
        PropertyConfigurator.configure(url);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, group)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)//等待队列
                    .option(ChannelOption.SO_KEEPALIVE, true)
//             .childHandler(new  MQTTChannelInitializer());
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {


                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addFirst(MqttEncoder.INSTANCE);
                            pipeline.addLast(new MqttDecoder());
                            pipeline.addLast(new MqttInBoundHandler());




                        }
                    });
            // 服务器绑定端口监听  // 监听服务器关闭监听
            sb.bind(port).sync().channel().closeFuture().sync();


//            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
//            System.out.println(EchoServer.class + " 启动正在监听： " + cf.channel().localAddress());
//            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully(); // 释放线程池资源
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {

        new MQTTServer(1883).start(); // 启动
    }
}