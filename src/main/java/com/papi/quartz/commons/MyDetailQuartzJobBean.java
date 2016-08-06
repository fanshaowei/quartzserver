package com.papi.quartz.commons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

//暂时没用到
public class MyDetailQuartzJobBean extends QuartzJobBean{
	protected final Log logger = LogFactory.getLog(getClass());
	
	private String targetObject;
	private String targetMethod;
	 
	private ApplicationContext ctx;
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {	
		logger.info("<<<<<<<<<<<<<<<<< 正在执行 [" + targetObject + "] at once >>>>>>>>>>>>>>>>>");
		
		try {
			ctx = (ApplicationContext) context.getScheduler().getContext().get("applicationContextKey");
			
			Object otargetObject = ctx.getBean(targetObject);
			Method method = null;
			
			/*method = otargetObject.getClass().getMethod(targetMethod, new Class[] {JobExecutionContext.class});
			method.invoke(otargetObject, new Object[]{context});*/
			
			method = otargetObject.getClass().getMethod(targetMethod, new Class[] {});
			method.invoke(otargetObject, new Object[]{});
				
			
		} catch (SchedulerException e1) {			
			e1.printStackTrace();
		} catch (NoSuchMethodException e) {			
			e.printStackTrace();
		} catch (SecurityException e) {			
			e.printStackTrace();
		} catch (IllegalAccessException e) {			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public String getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public String getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	public ApplicationContext getCtx() {
		return ctx;
	}

	public void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}

}
