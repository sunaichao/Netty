package com.io.netty.codec;

import com.io.netty.marshalling.MarshallingCodeCFactory;
import com.io.netty.message.Header;
import com.io.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/12.
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    NettyMarshallingDecoder nettyMarshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        nettyMarshallingDecoder = MarshallingCodeCFactory.buildNettyMessageDecoder();
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
        ByteBuf frame = (ByteBuf) super.decode(ctx,in);
        if (frame == null){
            return null;
        }

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int size = frame.readInt();
        if(size > 0){
            Map<String,Object> attach = new HashMap<String,Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;

            for (int i = 0; i < size; i++){
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key = new String(keyArray,"UTF-8");
                attach.put(key,nettyMarshallingDecoder.decode(ctx,frame));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attach);
        }
        if (frame.readableBytes() >4){
            message.setBody(nettyMarshallingDecoder.decode(ctx,frame));
        }
        message.setHeader(header);
        return message;
    }

}
