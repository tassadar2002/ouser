package com.ouser.util;

import android.content.Context;

public class SharedPreferencesUtil {

	public static boolean getBoolean(String name, String key, boolean defaultValue) {
		return Const.Application.getSharedPreferences(name, Context.MODE_PRIVATE).getBoolean(key,
				defaultValue);
	}

	public static void set(String name, String key, boolean value) {
		Const.Application.getSharedPreferences(name, Context.MODE_PRIVATE).edit()
				.putBoolean(key, value).commit();
	}
}
