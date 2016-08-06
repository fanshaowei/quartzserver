package com.papi.quartz.enums;

public enum QuartzJobs {
    BasicJob("com.papi.quartz.quartzjobs.BasicJob"),
    SceneRelateJob("com.papi.quartz.quartzjobs.SceneRelateJob"),
    SenseControlJob("com.papi.quartz.quartzjobs.SenseControlJob");
    
    private String clazz;
    private QuartzJobs(String clazz){
    	this.clazz = clazz;
    }
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
    public static String getJobType(String clazz){
    	QuartzJobs[] quartzJobs = QuartzJobs.values();
    	String jobType = "";
    	for(QuartzJobs qj: quartzJobs){
    		if(clazz.equals(qj.getClazz())){
    			jobType = qj.name();
    		}
    	}
    	return jobType;
    }
    
}
