package com.io.netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty时间服务器客户端
 * Created by Administrator on 2017/7/4.
 */
public class TimeClient {

    public void connect(String host, int port) throws Exception{
        /**
         * 配置客户端NIO线程组
         */
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            /**
             * 发起异步连接操作
             */
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",port).sync();
            /**
             * 等待客户端链路关闭
             */
            channelFuture.channel().closeFuture().sync();
        }finally {
            /**
             * 优雅退出，释放NIO线程组
             */
            eventLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception{
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new TimeClient().connect("127.0.0.1",port);
    }
}