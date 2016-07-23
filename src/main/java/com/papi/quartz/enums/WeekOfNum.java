package com.papi.quartz.enums;

public enum WeekOfNum {
	SUN(1),
	MON(2),
	TUE(3),
	WED(4),
	THU(5),
	FRI(6),
	SAT(7);
	
	private int num;
	private WeekOfNum(int _num){
		this.num = _num;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	 public static int getWeekOfNum(String name){
		  WeekOfNum[] weekOfNum = WeekOfNum.values();
		   int i = 0;
		   for(WeekOfNum won: weekOfNum){
			   if(name.equals(won.name())){
				   i =  won.getNum();
			   }
		   }
		   return i;
	   }
	 
	public static String getWeekName(int i){
		WeekOfNum[] weekOfNum = WeekOfNum.values();
		String weekName="";
		for(WeekOfNum won: weekOfNum){
			if(i == won.getNum()){
				weekName = won.name();
			}
		}
		return weekName;
	} 
}
