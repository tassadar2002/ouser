package com.ouser.util;

import java.util.UUID;

public class StringUtil {
	public static String bytes2String(byte[] value) {
		return (value == null) ? "" : new String(value);
	}

	public static boolean isEmpty(String paramString) {
		if ((paramString == null) || ("".equals(paramString))) {
			return true;
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		final String number = "0123456789";
		for (int i = 0; i < str.length(); i++) {
			if (number.indexOf(str.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
	
	public static String reverse(String value) {
		StringBuilder sb = new StringBuilder();
		for(int i = value.length() - 1; i >= 0; --i) {
			sb.append(value.charAt(i));
		}
		return sb.toString();
	}

	public static String createUUID() {
		return UUID.randomUUID().toString();
	}
}
