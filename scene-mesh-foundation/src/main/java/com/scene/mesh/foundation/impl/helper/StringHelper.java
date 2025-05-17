package com.scene.mesh.foundation.impl.helper;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

/**
 * 字符串工具类
 */
public class StringHelper {

    /**
     * netty bytebuf 转换 string
     * @param byteBuf
     * @return
     */
    public static String nettyByteBuf2String(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String format(String pattern, Object... args) {
        return MessageFormat.format(pattern, args);
    }
}
