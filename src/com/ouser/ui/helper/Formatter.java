package com.ouser.ui.helper;

import java.util.Calendar;

import com.ouser.R;
import com.ouser.module.Appoint;
import com.ouser.module.Gender;
import com.ouser.util.Const;

public class Formatter {

	public static int getGenderIcon(Gender value) {
		switch (value) {
		case Female:
			return R.drawable.gender_female_small;
		case Male:
			return R.drawable.gender_male_small;
		default:
			return R.drawable.ic_launcher;
		}
	}
	
	public static String getGenderText(Gender value) {
		switch (value) {
		case Female:
			return "女";
		case Male:
			return "男";
		default:
			return "";
		}
	}

	public static String getAppointContent(Appoint value) {
		if (value.getStatus() == Appoint.Status.Delete) {
			return "该友约已被删除";
		} else {
			return value.getContent();
		}
	}

	public static String getAppointStatus(Appoint value) {
		switch (value.getStatus()) {
		case Ing:
			return "正在进行";
		case Done:
			return "[已过期]";
		case Delete:
			return "[已删除]";
		default:
			return "";
		}
	}

	public static String getViewCount(Appoint value) {
		return value.getViewCount() + "人浏览";
	}

	public static String getJoinCount(Appoint value) {
		return value.getJoinCount() + "人参加";
	}

	public static String formatDistance(int value) {
		return String.format("%1$.2f公里", (double) value / (double) 1000);
	}

	// TODO user string.xml
	// 单位，分钟
	public static String formatMinuteValueToInterval(int value) {
		if (value < 60) {
			return value + "分钟";
		} else if (value < 60 * 24) {
			return value / 60 + "小时";
		} else if (value < 60 * 24 * 3) {
			return value / (60 * 24) + "天";
		} else {
			return "3天";
		}
	}

	// 单位，秒
	public static String formatCurrentSecondToInterval(int value) {
		return formatCurrentMinuteToInterval(value / 60);
	}
	
	// 单位，分钟
	public static String formatCurrentMinuteToInterval(int value) {
		int minute = (int) (System.currentTimeMillis() / 60000);
		return formatMinuteValueToInterval(minute - value);
	}

	public static String formatSpeed(int value) {
		return formatSize(value) + "/s";
	}

	public static String formatSize(long value) {

		double k = (double) value / 1024;
		if (k == 0) {
			return String.format("%dB", value);
		}

		double m = k / 1024;
		if (m < 1) {
			return String.format("%.1fK", k);
		}

		double g = m / 1024;
		if (g < 1) {
			return String.format("%.1fM", m);
		}

		return String.format("%.1fG", g);
	}

	// 单位，秒
	public static String formatTime(int second) {

		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;

		if (0 != hh) {
			return String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			return String.format("%02d:%02d", mm, ss);
		}
	}

	// 单位，分钟
	public static String formatDateTime(int minute) {
		if(minute == Const.DefaultValue.Time) {
			return "";
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis((long)minute * 60 * 1000);
			return String.format("%4d-%02d-%02d %02d:%02d",
					calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH) + 1, 
					calendar.get(Calendar.DAY_OF_MONTH), 
					calendar.get(Calendar.HOUR_OF_DAY), 
					calendar.get(Calendar.MINUTE));
		}
	}
}
