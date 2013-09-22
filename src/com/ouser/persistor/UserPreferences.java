package com.ouser.persistor;

import android.content.Context;
import android.content.SharedPreferences;

import com.ouser.module.User;
import com.ouser.util.Const;

class UserPreferences {
	
	public void set(User user) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString("lastuid", user.getUid());
		editor.putString("lastpass", user.getPassword());
		editor.commit();
	}

	public User getLastest() {
		SharedPreferences sp = getPreferences();
		String uid = sp.getString("lastuid", "");
		String pass = sp.getString("lastpass", "");
		
		User user = new User();
		user.setUid(uid);
		user.setPassword(pass);
		return user;
	}
	
	public void clearLastestPass() {
		getPreferences().edit().putString("lastpass", "").commit();
	}
	
	private SharedPreferences getPreferences() {
		return Const.Application.getSharedPreferences("user", Context.MODE_PRIVATE);
	}
}



