package com.io.netty;

import com.io.netty.codec.NettyMessageDecoder;
import com.io.netty.codec.NettyMessageEncoder;
import com.io.netty.codec.ProtocalContants;
import com.io.netty.handler.HeartBeatReqHandler;
import com.io.netty.handler.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/7/12.
 */
public class ProtocolClient {
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host, final int localPort) throws Exception{
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024,4,4,-8,0));
                            ch.pipeline().addLast("MessageEncoder",new NettyMessageEncoder());
                            ch.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(50));
                            ch.pipeline().addLast("LoginAuthHandler",new LoginAuthReqHandler());
                            ch.pipeline().addLast("HeartBeatHandler",new HeartBeatReqHandler());
                        }
                    });


            /**
             * 发起异步操作
             */
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port),new InetSocketAddress(ProtocalContants.LOCALIP,localPort)).sync();
            future.channel().closeFuture().sync();
        }finally {

            /**
             * 断线重连操作
             */
            executorService.execute(new Runnable() {
                public void run() {
                    try{
                        TimeUnit.SECONDS.sleep(5);
                        System.out.println("断线重连开始");
                        try{
                            connect(ProtocalContants.PORT,ProtocalContants.REMOTEIP,localPort);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public static void main(String[] args){
        try{

            new ProtocolClient().connect(ProtocalContants.PORT,ProtocalContants.REMOTEIP,7395);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
