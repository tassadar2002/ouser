package com.ouser.util;

import android.widget.Toast;

public class ToastUtil {

	public static void show(String message) {
		Toast.makeText(Const.Application, message, Toast.LENGTH_LONG).show();
	}
}
