package com.papi.quartz.commons.config;


/**
 * 
 * @author fanshaowei
 *
 **该类通过spring注入，读取jdbc.properties的配置
 */
public class QuartzFrameConfig {
	private String instanceName;
	private String instanceId;
	private String threadPoolClass;
	private String makeThreadsDaemons;
	private String threadCount;
	private String threadPriority;
	private String misfireThreshold;
	private String jobStoreClass;
	
	public QuartzFrameConfig(){}
	
	public QuartzFrameConfig(String instanceName, String instanceId,
			String threadPoolClass, String makeThreadsDaemons,
			String threadCount, String threadPriority, String misfireThreshold,
			String jobStoreClass) {
		super();
		this.instanceName = instanceName;
		this.instanceId = instanceId;
		this.threadPoolClass = threadPoolClass;
		this.makeThreadsDaemons = makeThreadsDaemons;
		this.threadCount = threadCount;
		this.threadPriority = threadPriority;
		this.misfireThreshold = misfireThreshold;
		this.jobStoreClass = jobStoreClass;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getThreadPoolClass() {
		return threadPoolClass;
	}

	public void setThreadPoolClass(String threadPoolClass) {
		this.threadPoolClass = threadPoolClass;
	}

	public String getMakeThreadsDaemons() {
		return makeThreadsDaemons;
	}

	public void setMakeThreadsDaemons(String makeThreadsDaemons) {
		this.makeThreadsDaemons = makeThreadsDaemons;
	}

	public String getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(String threadCount) {
		this.threadCount = threadCount;
	}

	public String getThreadPriority() {
		return threadPriority;
	}

	public void setThreadPriority(String threadPriority) {
		this.threadPriority = threadPriority;
	}

	public String getMisfireThreshold() {
		return misfireThreshold;
	}

	public void setMisfireThreshold(String misfireThreshold) {
		this.misfireThreshold = misfireThreshold;
	}

	public String getJobStoreClass() {
		return jobStoreClass;
	}

	public void setJobStoreClass(String jobStoreClass) {
		this.jobStoreClass = jobStoreClass;
	}
    
	
	
}
