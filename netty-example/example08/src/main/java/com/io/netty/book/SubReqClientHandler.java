package com.io.netty.book;

import com.io.netty.protobuf.SubscribeReqProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2017/7/7.
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    public SubReqClientHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("receive server response : [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    private SubscribeReqProto.SubscribeReq subReq(int i){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(i);
        builder.setUserName("sunaichao");
        builder.setProductName("netty book for protobuf");
        builder.setAddress("haha bei");
        return  builder.build();
    }
}
