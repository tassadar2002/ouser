package com.ouser.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 用于获取各种格式的时间，单例模式实现
 * @author sunjianshun
 *
 */
public class TimeUtil {
	
	public static String getFullFormat(String value) {
		return value + " 00:00:00";
	}
	
	public static String getDateFormat(String value) {
		String[] s = value.split(" ");
		if(s.length > 1) {
			return s[0];
		} else {
			return value;
		}
	}

	///////////////////////////////////////////////////////////////
    /**
     * 返回"yyyy-MM-dd HH:mm:ss"格式
     */
	public static String getCurrentTime() {
		
		long timemillis = System.currentTimeMillis();   
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(timemillis));
	}
	
	/**
	 * 返回真：当天时间。以第一个参数为参照
	 * 参数格式:"yyyy-MM-dd HH:mm:ss"
	 */
	private static boolean isToday(final String today, final String time) {
		if (getYear(today) == getYear(time) && getMonth(today) == getMonth(time)) {
			int today_day = getDay(today);
			int time_day = getDay(time);
			return today_day == time_day;
		}
		
		return false;
	}
	
	/**
	 * 返回真：当天或者未来时间。以第一个参数为参照
	 * 参数格式:"yyyy-MM-dd HH:mm:ss"
	 */
	public static boolean isTodayOrFuture(final String today, final String time) {
		
		if (isToday(today, time)) return true;
		else if (today.compareTo(time) < 0) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 以第一个参数为参照
	 * 参数格式:"yyyy-MM-dd HH:mm:ss"
	 */
	public static boolean isYesterday(final String today, final String time) {
		// TODO 1.格式检查
		
		String yestoday = null;
		int iTodayYear = Integer.valueOf(getYear(today)).intValue();				
		int iTodayMonth = Integer.valueOf(getMonth(today)).intValue();
		int iTodayDay = Integer.valueOf(getDay(today)).intValue();
		
		// 月份起始从0开始
		iTodayMonth = (iTodayMonth -1 + 12)%12;
		
		// 用today设置cal
		Calendar cal = Calendar.getInstance();
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");	    
	    cal.set(iTodayYear, iTodayMonth, iTodayDay);
	    
	    // 计算得到yesterday
	    cal.add(Calendar.DATE, -1);
	    yestoday = dateFormat.format(cal.getTime());
	    
	    // 比较tiem与yesterday是否相等
	    String time_yesterday = time.split(" ")[0];
	    return yestoday.equals(time_yesterday);	
	}
	
	/**
	 * 以第一个参数为参照
	 * 参数格式:"yyyy-MM-dd HH:mm:ss"
	 */
	public static boolean isEarliear(final String today, final String time) {
		// TODO 优化
		// 不是昨天，不是今天，并且值小于today
		if (!isToday(today, time) && !isYesterday(today, time)) {
			return today.compareTo(time) > 0 ? true : false; 
		}
		return false;
	}
	
	/**
	 * 提取 dd
	 * 参数格式:"yyyy-MM-dd HH:mm:ss" 或 "yyyy-MM-dd"
	 */
	public static int getDay(String time) {
		String[] data = time.split("-");
		if (data.length > 2) {
			String[] subData = data[2].split(" ");
			if (data.length > 1) {
				return Integer.parseInt(subData[0]);
			} else {
				return Integer.parseInt(data[2]);
			}
		}
		return 0;
	}
	
	/**
	 * 提取 yyyy
	 * 参数格式:"yyyy-MM-dd HH:mm:ss" 或 "yyyy-MM-dd"
	 */
	public static int getYear(String time) {
		String[] data = time.split("-");
		if (data.length > 0) {
			return Integer.parseInt(data[0]);
		}
		return 0;
	}
	
	/**
	 * 提取 MM
	 * 参数格式:"yyyy-MM-dd HH:mm:ss" 或 "yyyy-MM-dd"
	 */
	public static int getMonth(String time) {
		String[] data = time.split("-");
		if (data.length > 1) {
			return Integer.parseInt(data[1]);
		}
		return 0;
	}
	
	/**
	 * 判断是否为两周以内
	 * 以第一个参数为参照
	 * 参数格式:"yyyy-MM-dd HH:mm:ss"
	 */
	public static boolean isInTwoWeeks(String today, String time) {
		
		String twoWeeks = null;
		int iTodayYear = Integer.valueOf(getYear(today)).intValue();				
		int iTodayMonth = Integer.valueOf(getMonth(today)).intValue();
		int iTodayDay = Integer.valueOf(getDay(today)).intValue();
		
		// 月份起始从0开始
		iTodayMonth = (iTodayMonth -1 + 12)%12;
		
		// 用today设置cal
		Calendar cal = Calendar.getInstance();
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");	    
	    cal.set(iTodayYear, iTodayMonth, iTodayDay);
	    
	    // 计算得到twoWeeks
	    cal.add(Calendar.DATE, -14);
	    twoWeeks = dateFormat.format(cal.getTime());
	    
	    // 比较tiem与twoWeeks是否相等
	    String time_twoWeeks = time.split(" ")[0];
	    if (twoWeeks.compareTo(time_twoWeeks) > 0) {
	    	return false;
	    }
	    return true;
	}
}
