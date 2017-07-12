package com.io.netty.marshalling;

import com.io.netty.codec.NettyMarshallingDecoder;
import com.io.netty.codec.NettyMarshallingEncoder;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * Created by Administrator on 2017/7/12.
 */
public class MarshallingCodeCFactory {
    public static NettyMarshallingEncoder buildNettyMessageEncoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory,configuration);
        NettyMarshallingEncoder encoder = new NettyMarshallingEncoder(provider);
        return encoder;
    }

    public static NettyMarshallingDecoder buildNettyMessageDecoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,configuration);
        NettyMarshallingDecoder decoder = new NettyMarshallingDecoder(provider,1024 * 1024);
        return decoder;
    }

}
