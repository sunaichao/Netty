package com.io.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;

/**
 * Created by Administrator on 2017/7/11.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>{

    private WebSocketServerHandshaker handshaker;

    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //WebSocket接入
        if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx,(WebSocketFrame)msg);
        }else if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }

    }

    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{
        //如果Http解码失败 返回Http异常
        if(!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //构造握手响应回应
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket",null,false);
        handshaker = webSocketServerHandshakerFactory.newHandshaker(req);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }


    public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭链路的指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),(CloseWebSocketFrame)frame.retain());
            return;
        }
        //判断是否是Ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }
        //返回应答消息
        String request = ((TextWebSocketFrame)frame).text();

        ctx.channel().write(new TextWebSocketFrame(request + " , 欢迎使用Netty Websocket服务，现在时刻：" + new Date().toString()));

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse rep){
        //返回应答给客户端
        if(rep.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(rep.status().toString(), CharsetUtil.UTF_8);
            rep.content().writeBytes(buf);
            buf.release();
            setContentLength(rep,rep.content().readableBytes());
        }
        //如果是非 keep-alive，关闭连接
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(rep);
        if(!isKeepAlive(req) || rep.status().code() != 200){
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
