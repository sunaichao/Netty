package com.io.netty;

import com.google.protobuf.InvalidProtocolBufferException;
import com.io.netty.protobuf.SubscribeReqProto;

/**
 * Created by Administrator on 2017/7/10.
 */
public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }


    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("sunaichao");
        builder.setProductName("netty book");
        builder.setAddress("nan jin");
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("before encode : " + req.toString());
        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("after decode : " + req.toString());
        System.out.println("assert equal : -->" + req2.equals(req));
    }
}
