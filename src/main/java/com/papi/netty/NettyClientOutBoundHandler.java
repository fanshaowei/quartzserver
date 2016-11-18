package com.papi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class NettyClientOutBoundHandler extends ChannelOutboundHandlerAdapter{
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,ChannelPromise promise) throws Exception {		

    	byte[] sendMsg = ((String)msg).getBytes();
    	ByteBuf sendBuf = Unpooled.buffer(sendMsg.length);
    	sendBuf.writeBytes(sendMsg);
    	
    	ctx.writeAndFlush(sendBuf);    	
	}
		
}
