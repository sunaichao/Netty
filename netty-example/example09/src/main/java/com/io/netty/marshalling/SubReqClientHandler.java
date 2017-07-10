package com.io.netty.marshalling;

import com.io.netty.protobuf.SubscribeReq;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2017/7/7.
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

//    public SubReqClientHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++){
//            System.out.println(subReq(i).toString());
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

    private SubscribeReq subReq(int i){
        SubscribeReq builder = new SubscribeReq();
        builder.setSubReqID(i);
        builder.setUserName("sunaichao");
        builder.setProductName("netty book for protobuf");
        builder.setAddress("haha bei");
        return  builder;
    }
}
