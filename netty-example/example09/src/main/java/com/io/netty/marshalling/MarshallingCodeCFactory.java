package com.io.netty.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * Created by Administrator on 2017/7/7.
 */
public class MarshallingCodeCFactory {
    /**
     * 创建JBoss Marshalling解码器MarshallingDecoder
     */
    public static MarshallingEncoder buidMarshallingEncoder(){
        /**
         * 创建Java序列化工厂
         */
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory,configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }

    public static MarshallingDecoder buildMarshallingDecoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,configuration);
        MarshallingDecoder decoder = new MarshallingDecoder(provider,1024);
        return decoder;
    }
}
