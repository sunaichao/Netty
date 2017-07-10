package com.io.netty.book;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Created by Administrator on 2017/7/7.
 */
public class SubReqClient {

    public static void main(String[] args) throws Exception{
        int port = 8080;
        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (Exception e){

            }
        }
        new SubReqClient().connect("127.0.0.1",port);
    }

    private void connect(String host, int port) throws Exception{
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new SubReqClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {

            eventLoopGroup.shutdownGracefully();
        }
    }
}
