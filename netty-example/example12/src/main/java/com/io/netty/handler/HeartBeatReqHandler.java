package com.io.netty.handler;

import com.io.netty.codec.MessageType;
import com.io.netty.message.Header;
import com.io.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 客户端发送心跳请求消息
 * * Written by Mr. *
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        /**
         * 握手成功，主动发送心跳消息
         */
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            System.out.println("客户端接收到服务端发送的登录回复消息 : ---> " + message);

            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx),0,5000, TimeUnit.MILLISECONDS);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()){
            System.out.println("客户端接收到服务端发送的心跳消息 : ---> " + message);
        }else{
            ctx.fireChannelRead(msg);
        }

    }

    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx){
            this.ctx = ctx;
        }
        public void run() {
            NettyMessage heartMsg = buildHeartBeat();
            System.out.println("Client send heart beat message to server : --->" + heartMsg.toString());

            ctx.writeAndFlush(heartMsg);
        }
    }

    private NettyMessage buildHeartBeat(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_REQ.value());
        message.setHeader(header);
        return message;
    }

}
