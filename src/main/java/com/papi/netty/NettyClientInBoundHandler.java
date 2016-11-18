/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.papi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class NettyClientInBoundHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf firstMessage;

    /**
     * Creates a client-side handler.
     * @throws Exception 
     */

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	super.channelActive(ctx);
    	
    	String connect = "client ready connect to server!\n";
    	byte[] connectByte = connect.getBytes(); 
    	
    	firstMessage = Unpooled.buffer(connectByte.length);
    	firstMessage.writeBytes(connectByte);
    	
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {    	
        System.out.println("----------read server messege----------");
    	ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    		throws Exception {    
    	//super.userEventTriggered(ctx, evt);
    /*	if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				// 读超时
				System.out.println("client read idle");
			} else if (event.state() == IdleState.WRITER_IDLE) {
				// 写超时
				System.out.println("client write idle");
				ctx.channel().writeAndFlush(evt);
			} else if (event.state() == IdleState.ALL_IDLE) {
				// 读写超时
				//System.out.println("client rw idle");
			}
		}*/
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
