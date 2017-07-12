package com.io.netty.handler;

import com.io.netty.codec.MessageType;
import com.io.netty.message.Header;
import com.io.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 握手接入和安全认证代码
 * Created by Administrator on 2017/6/4.
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<String,Boolean>();

    private String[] whiteList = {"127.0.0.1","10.37.100.29"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        System.out.println("接收到客户端的登录请求");
        /**
         * 如果是握手请求消息，那就处理。
         * 如果是其他消息，就直接透传
         */
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            /**
             * 重复登录 拒绝
             */
            if (nodeCheck.containsKey(nodeIndex)){
                System.out.println("客户端重复登录");
                loginResp = buildResponse((byte)-1);
            }else{
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for(String WIP : whiteList){
                    if(WIP.equals(ip)){
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte)-1);
                if (isOK)
                    nodeCheck.put(nodeIndex,true);
            }
            //System.out.println("the login response is : " + loginResp + " body [" + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte result){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * 删除缓存系统
         */
        System.out.println("异常信息，删除缓存系统");
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}