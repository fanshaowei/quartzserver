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

import javax.net.ssl.SSLException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 
 * @author fanshaowei
 *
 *netty客户端创建类，在启动时，通过注入一个spring bean，调用该类，生成一个netty客户端长链接
 *
 */
public final class NettyClient {
    private static EventLoopGroup eventLoopGroup;
    private static ChannelFuture channelFuture;
    
    public void connect(String host,int port) throws SSLException, InterruptedException{    	 
         // Configure the client.
         eventLoopGroup = new NioEventLoopGroup();
         try {
             Bootstrap b = new Bootstrap();
             b.group(eventLoopGroup)
              .channel(NioSocketChannel.class)
              .option(ChannelOption.SO_KEEPALIVE, true)
              .handler(new ChannelInitializer<SocketChannel>() {
                  @Override
                  public void initChannel(SocketChannel ch) throws Exception {
                      ChannelPipeline pipeline = ch.pipeline();

                      pipeline.addLast(new DelimiterBasedFrameDecoder(1024, true, Delimiters.lineDelimiter()));
                      pipeline.addLast(new StringDecoder());
                      pipeline.addLast(new NettyClientInBoundHandler());
                      pipeline.addLast(new NettyClientOutBoundHandler());
                  }
              });

             // Start the client.
             channelFuture = b.connect(host, port).sync();

             // Wait until the connection is closed.
             channelFuture.channel().closeFuture().sync();
         } finally {
             // Shut down the event loop to terminate all threads.
        	 eventLoopGroup.shutdownGracefully();
         }
    }
    
    public void disconnect(){
    	eventLoopGroup.shutdownGracefully();
    }
    
    public void writeMesg(String str) {
    	str = str + "\n";
    	
    	if(channelFuture.channel().isActive()){
    		channelFuture.channel().writeAndFlush(str);
    	}else{
    		System.out.println("channelFuture is not active!try connect again");
    	}
    		
    }
       
}
