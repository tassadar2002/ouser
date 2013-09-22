package com.ouser.util;

import java.util.Calendar;

public class Constellation {

	private static final String[] constellationArr = { "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
			"狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座" };
	private static final int[] constellationEdgeDay = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };

	public static Calendar convertBirthday(String birthday) {
		Calendar birth = Calendar.getInstance();

		try {
			String[] s = birthday.split("-");
			if(s.length != 3) {
				return null;
			}
			int year = Integer.parseInt(s[0]);
			int month = Integer.parseInt(s[1]);
			int day = Integer.parseInt(s[2]);
			birth.set(year, month - 1, day);

		} catch (NumberFormatException nfe) {
			birth.set(0, 0, 0);
		}
		return birth;
	}

	public static int getAge(String birthday) {

		try {
			Calendar now = Calendar.getInstance();
			Calendar birth = convertBirthday(birthday);
			if (now.before(birth))
				return -1;

			int thisYear = now.get(Calendar.YEAR);
			int thisMonth = now.get(Calendar.MONTH) + 1;
			int thisDay = now.get(Calendar.DATE);

			int year = birth.get(Calendar.YEAR);
			int month = birth.get(Calendar.MONTH) + 1;
			int day = birth.get(Calendar.DATE);

			int age = thisYear - year;

			if (thisMonth <= month) {
				if (thisMonth == month) {
					if (thisDay < day)
						age--;
				} else
					age--;
			}
			return age;
		} catch (Exception e) {
			return 0;
		}
	}

	public static String getStar(String birthday) {
		Calendar birth = convertBirthday(birthday);
		int month = birth.get(Calendar.MONTH);
		int day = birth.get(Calendar.DAY_OF_MONTH);
		if (day < constellationEdgeDay[month]) {
			month = month - 1;
		}
		if (month >= 0)
			return constellationArr[month];
		else
			return constellationArr[11];
	}
}
