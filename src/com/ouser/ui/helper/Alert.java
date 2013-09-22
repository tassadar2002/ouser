package com.ouser.ui.helper;

import android.view.Gravity;
import android.widget.Toast;

import com.ouser.logic.OperErrorCode;
import com.ouser.util.Const;

public class Alert {

	public static void Toast(String message) {
		Alert.Toast(message, true, true);
	}
	
	public static void Toast(String message, boolean longShow, boolean center) {
		Toast toast = Toast.makeText(Const.Application, message, 
				longShow ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		if(center) {
			toast.setGravity(Gravity.CENTER, 0, 0);
		}
		toast.show();
	}
	
	public static void showNetAvaiable() {
		Toast("您的网络不给力", false, true);
	}
	
	public static void handleErrCode(OperErrorCode errCode) {
		switch(errCode) {
		case NetNotAviable:
			Alert.showNetAvaiable();
			break;
		default:
			Alert.Toast("未知错误");
			break;
		}
	}
}
