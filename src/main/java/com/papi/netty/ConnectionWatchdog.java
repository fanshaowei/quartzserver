package com.papi.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask,ChannelHandlerHolder{
	private static final Logger logger = Logger.getLogger(ConnectionWatchdog.class);
			
	private final Bootstrap bootstrap;
	private final Timer timer;
	private final int port;
	
	private final String host;
	private volatile boolean reconnec = true;
	private int attempts;
	
	public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port,
			String host, boolean reconnec) {
		this.bootstrap = bootstrap;
		this.timer = timer;
		this.port = port;
		this.host = host;
		this.reconnec = reconnec;	
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("-------------当前链路已经激活，重连尝试次数重置为0-------------");
		attempts = 0;
		reconnec = false;
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("--------------连接关闭--------------");
		reconnec = true;
		if(reconnec){
			//if(attempts < 12){
				attempts ++;
				int timeout = 15;//8 << attempts;
				timer.newTimeout(this, timeout, TimeUnit.SECONDS);
				logger.info("---------重连第"+ attempts +"次--------");
			//}
		}
		ctx.fireChannelInactive();
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		/*reconnec = true;
		ctx.fireChannelInactive();*/
	}
	
	//@Override
	public void run(Timeout timeout) throws Exception {		
		ChannelFuture channelFuture = null;
		
		synchronized (bootstrap) {
			bootstrap.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(handlers());					
				}				
			});
			channelFuture = bootstrap.connect(host,port);
		}			
		
		channelFuture.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				boolean succeed = f.isSuccess(); 
				//如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
				if(succeed){
					logger.info("----------重连成功------------");	
					reconnec = false;
				}else{					
					logger.info("----------重连失败------------");					
					f.channel().pipeline().fireChannelInactive();
				}
			}
		});	
				
	}
}
