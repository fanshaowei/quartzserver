package com.papi.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 
 * @author fanshaowei
 *
 *netty客户端创建类，在启动时，通过注入一个spring bean，调用该类，生成一个netty客户端长链接
 *
 */
public final class NettyClient {
	private static Logger logger = Logger.getLogger(NettyClient.class.getName());
	
    private static EventLoopGroup eventLoopGroup;
    private static ChannelFuture channelFuture;
    
    protected final HashedWheelTimer timer = new HashedWheelTimer();//(3, TimeUnit.SECONDS, 1);
    private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();
    
    public void connect(String host,int port){    	 
         // Configure the client.
         eventLoopGroup = new NioEventLoopGroup();
         try {
             Bootstrap b = new Bootstrap();
             b.group(eventLoopGroup)
              .channel(NioSocketChannel.class)
              .option(ChannelOption.SO_KEEPALIVE, true);
             /* .handler(new ChannelInitializer<SocketChannel>() {
                  @Override
                  public void initChannel(SocketChannel ch) throws Exception {
                      ChannelPipeline pipeline = ch.pipeline();
                      pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                      pipeline.addLast(new DelimiterBasedFrameDecoder(1024, true, Delimiters.lineDelimiter()));
                      pipeline.addLast(new StringDecoder());
                      pipeline.addLast(new NettyClientInBoundHandler());
                      pipeline.addLast(new NettyClientOutBoundHandler());
                  }
              });*/          
             //channelFuture = b.connect(host, port).sync();             
             //channelFuture.channel().closeFuture().sync();
             
             final ConnectionWatchdog watchdog = new ConnectionWatchdog(b, timer, port, host, true) {
				
				@Override
				public ChannelHandler[] handlers() {					
					return new ChannelHandler[]{
						this,
						new IdleStateHandler(0, 60, 0, TimeUnit.SECONDS),
						idleStateTrigger,
						new DelimiterBasedFrameDecoder(1024, true, Delimiters.lineDelimiter()),
	                    new StringDecoder(),
	                    new NettyClientInBoundHandler(),
	                    new NettyClientOutBoundHandler()
					};
				}
			};
            synchronized (b) {
            	b.handler(new ChannelInitializer<SocketChannel>() {
    				@Override
    				public void initChannel(SocketChannel ch) throws Exception {
    					ch.pipeline().addLast(watchdog.handlers());
    				};
    			});
            	channelFuture = b.connect(host,port);
			}
						
            channelFuture.sync();
			//channelFuture.channel().closeFuture().sync();
			
         }catch(Exception e){
         	e.printStackTrace();
         	logger.info("connects to  fails"); 
         } finally {
             // Shut down the event loop to terminate all threads.
        	 //eventLoopGroup.shutdownGracefully();
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
