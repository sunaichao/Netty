package com.io.netty.marshalling;

import com.io.netty.protobuf.SubscribeReq;
import com.io.netty.protobuf.SubscribeResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2017/7/7.
 */
@ChannelHandler.Sharable
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if("sunaichao".equalsIgnoreCase(req.getUserName())){
            System.out.println("service accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    private SubscribeResp resp(int subReqID){
        SubscribeResp builder = new SubscribeResp();
        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("netty book order succeed, 3 days later, sent to the designated address");
        return builder;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
