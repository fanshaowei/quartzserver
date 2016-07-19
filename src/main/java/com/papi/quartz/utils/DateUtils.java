package com.papi.quartz.utils;

import com.papi.quartz.enums.TimeInterval;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	  public static String TIME_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	  public static String TIME_PATTERN_YMDHM = "yyyy-MM-dd HH:mm";
	  public static String TIME_PATTERN_MD = "MM-dd";
	  public static String TIME_PATTERN_HM = "HH:mm";

	  public static String DATE_PATTERN = "yyyy-MM-dd";
	  public static String DATE_PATTERN_DIRECTORY = "yyyy/MM/dd";
	  public static String DATE_PATTERN_YYYYMMDD = "yyyyMMdd";
	  public static String DATE_PATTERN_YYYYMM = "yyyyMM";
	  public static String DATE_PATTERN_YYYYMMDDHMS = "yyyyMMddHHmmss";

	  public static String sysDate(String pattern)
	  {
	    if ((pattern == null) || ("".equals(pattern))) {
	      return dateToString(new Date(), TIME_PATTERN_YMDHMS);
	    }
	    return dateToString(new Date(), pattern);
	  }

	  public static long dateDiff(TimeInterval interval, Date date1, Date date2)
	  {
	    long result = -1L;
	    long time1 = date1.getTime();
	    long time2 = date2.getTime();
	    int time = 0;

	    switch (interval) {
	    case DAY:
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTime(date2);
	      time = calendar.get(1);
	      calendar.setTime(date1);
	      result = time - calendar.get(1);
	      break;
	      
	    case HOUR:
	      Calendar calendar0 = Calendar.getInstance();
	      calendar0.setTime(date2);
	      time = calendar0.get(1) * 12;
	      calendar0.setTime(date1);
	      time -= calendar0.get(1) * 12;
	      calendar0.setTime(date2);
	      time += calendar0.get(2);
	      calendar0.setTime(date1);
	      result = time - calendar0.get(2);
	      break;
	    case MINUTE:
	      Calendar calendar1 = Calendar.getInstance();
	      calendar1.setTime(date2);
	      time = calendar1.get(1) * 52;
	      calendar1.setTime(date1);
	      time -= calendar1.get(1) * 52;
	      calendar1.setTime(date2);
	      time += calendar1.get(3);
	      calendar1.setTime(date1);
	      result = time - calendar1.get(3);
	      break;
	    case MONTH:
	      result = (time2 - time1) / 1000L / 3600L / 24L;
	      break;
	    case SECOND:
	      result = (time2 - time1) / 1000L / 3600L;
	      break;
	    case WEEK:
	      result = (time2 - time1) / 1000L / 60L;
	      break;
	    case YEAR:
	      result = (time2 - time1) / 1000L;
	      break;
	    default:
	      result = time2 - time1;
	    }

	    return result;
	  }

	  public static Date dateAdd(TimeInterval interval, Date date, int add)
	  {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    switch (interval) {
	    case DAY:
	      calendar.add(1, add);
	      break;
	    case HOUR:
	      calendar.add(2, add);
	      break;
	    case MINUTE:
	      calendar.add(3, add);
	      break;
	    case MONTH:
	      calendar.add(6, add);
	      break;
	    case SECOND:
	      calendar.add(10, add);
	      break;
	    case WEEK:
	      calendar.add(12, add);
	      break;
	    case YEAR:
	      calendar.add(13, add);
	      break;
	    }

	    return calendar.getTime();
	  }

	  public static Date dateFormat(Date date)
	  {
	    if (date == null) date = new Date();
	    return dateFormat(date, TIME_PATTERN_YMDHMS);
	  }

	  public static Date dateFormat(Date date, String pattern)
	  {
	    if (date == null) date = new Date();
	    SimpleDateFormat format = new SimpleDateFormat(pattern);
	    Date result = null;
	    try {
	      result = format.parse(format.format(date));
	    }
	    catch (ParseException localParseException) {
	    }
	    return result;
	  }

	  public static int weekNumOfYear(Date date)
	  {
	    if (date == null) date = new Date();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    return calendar.get(3);
	  }

	  public static Date stringToDate(String date, String pattern)
	  {
	    if ((date == null) || (date.equals("")))
	      return null;
	    SimpleDateFormat format = new SimpleDateFormat(pattern);
	    Date result = null;
	    try {
	      result = format.parse(date);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }

	    return result;
	  }

	  public static String dateToString(Date date, String pattern)
	  {
	    if ((pattern != null) && (!pattern.equals(""))) {
	      return new SimpleDateFormat(pattern).format(date);
	    }
	    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	  }

	  public static void main(String[] args) {
	    System.out.println(sysDate(null));
	  }
}
