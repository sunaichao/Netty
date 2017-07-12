package com.io.netty.codec;

import com.io.netty.marshalling.MarshallingCodeCFactory;
import com.io.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/12.
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage>{

    NettyMarshallingEncoder nettyMarshallingEncoder;

    public NettyMessageEncoder() throws IOException{
        this.nettyMarshallingEncoder = MarshallingCodeCFactory.buildNettyMessageEncoder();
    }

    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        if(msg == null || msg.getHeader() == null){
            throw new Exception("The encoder Message is null!");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeader().getCrcCode());//消息验证码
        sendBuf.writeInt(msg.getHeader().getLength());//消息长度
        sendBuf.writeLong(msg.getHeader().getSessionID());// 会话ID
        sendBuf.writeByte(msg.getHeader().getType());//消息类型
        sendBuf.writeByte(msg.getHeader().getPriority());//消息优先级
        sendBuf.writeInt(msg.getHeader().getAttachment().size());//附加信息长度

        String key = null;
        byte[] keyArray = null;
        Object value = null;

        for (Map.Entry<String,Object> param : msg.getHeader().getAttachment().entrySet()){
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            nettyMarshallingEncoder.encode(ctx,value,sendBuf);
        }

        key = null;
        keyArray = null;
        value = null;

        if (msg.getBody() != null){
            nettyMarshallingEncoder.encode(ctx,msg.getBody(),sendBuf);
        }else{
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4,sendBuf.readableBytes());
        int num = sendBuf.readableBytes();
        out.add(sendBuf);
    }

}
