package com.ouser.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlUtil {

	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String decode(String value) {
		try {
			value = value.replace("+", "%20");
			return URLDecoder.decode(value, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
