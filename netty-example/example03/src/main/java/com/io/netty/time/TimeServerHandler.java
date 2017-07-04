package com.io.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * @TimeServerHandler 继承自 @ChannelHandlerAdapter
 * 用于对网络事件进行读写操作
 * Created by Administrator on 2017/7/4.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    /**
     * 获取客户端上报的数据
     * 如果上报的数据是 QUERY TIME ORDER 字符串，则将服务器当前时间异步发送给客户端
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String body = new String(bytes,"UTF-8");
        System.out.println("the time server receive order : " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /**
         * 将消息发送队列中的消息写入到SocketChannel中发送给对方
         * 为了防止频繁的唤醒Selector进行消息发送，Netty的write方法并不直接将消息写入SocketChannel中
         * 调用write方法只是把待发送的消息放到发送缓冲数组中，再通过调用flush方法，将发送缓冲区中的消息全部写到
         * SocketChannel中
         */
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * 关闭ChannelHandlerContext，释放和ChannelHandlerContext相关联的句柄等资源
         */
        ctx.close();
    }
}
