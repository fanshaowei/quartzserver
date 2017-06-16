package com.papi.netty;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder {
	public ChannelHandler[] handlers();
}
