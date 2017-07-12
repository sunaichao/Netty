package com.io.netty;

import com.io.netty.codec.NettyMessageDecoder;
import com.io.netty.codec.NettyMessageEncoder;
import com.io.netty.codec.ProtocalContants;
import com.io.netty.handler.HeartBeatRespHandler;
import com.io.netty.handler.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Administrator on 2017/7/12.
 */
public class ProtocolServer {

    public void bind() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try{
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024,4,4,-8,0));
                            ch.pipeline().addLast(new NettyMessageEncoder());
                            ch.pipeline().addLast(new LoginAuthRespHandler());
                            ch.pipeline().addLast(new HeartBeatRespHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(ProtocalContants.REMOTEIP,ProtocalContants.PORT).sync();
            System.out.println("服务端系统启动成功 : " + (ProtocalContants.REMOTEIP + " : " + ProtocalContants.PORT));
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args){
        try{
            new ProtocolServer().bind();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
