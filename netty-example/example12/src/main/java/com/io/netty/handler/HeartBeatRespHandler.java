package com.io.netty.handler;

import com.io.netty.codec.MessageType;
import com.io.netty.message.Header;
import com.io.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2017/6/4.
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 返回心跳应答消息
         */
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()){
            System.out.println("接收到客户端发送的心跳信息 : ---> " + message);
            NettyMessage heartBeat = buildHeatBeat();
            System.out.println("回复客户端心跳信息 : ---> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }else{
            /**
             * 透传给下一个ChannelHandler处理
             */
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeatBeat(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("心跳异常");
        super.exceptionCaught(ctx, cause);
    }
}
