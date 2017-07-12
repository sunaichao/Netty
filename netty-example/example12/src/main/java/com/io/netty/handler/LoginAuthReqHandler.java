package com.io.netty.handler;

import com.io.netty.codec.MessageType;
import com.io.netty.message.Header;
import com.io.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2017/6/4.
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;

        /**
         * 如果是握手应答消息，需要判断是否认证成功
         */
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            byte loginResult = (Byte)message.getBody();
            //握手失败 关闭连接
            if (loginResult != (byte)0){
                System.out.println("登录失败，关闭连接 : " + message);
                ctx.close();
            }else{
                System.out.println("登录成功 : " + message);
                ctx.fireChannelRead(msg);
            }
        }else{
            /**
             * 如果不是握手应答消息，直接透传给后面的ChannelHandler处理消息
             */
            ctx.fireChannelRead(msg);
        }

    }

    private NettyMessage buildLoginReq(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
