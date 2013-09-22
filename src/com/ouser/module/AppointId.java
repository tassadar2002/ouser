package com.ouser.module;

import android.os.Bundle;

public class AppointId {

	private String aid = "";
	private String uid = "";
	
	public AppointId() {
	}
	
	public AppointId(String aid, String uid) {
		this.aid = aid;
		this.uid = uid;
	}

	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public boolean isEmpty() {
		return "".equals(uid) || "".equals(aid) || "0".equals(aid);
	}
	public boolean isSame(AppointId value) {
		return aid.equals(value.aid) && uid.equals(value.uid);
	}
	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putString("aid", aid);
		bundle.putString("uid", uid);
		return bundle;
	}
	public void fromBundle(Bundle bundle) {
		aid = bundle.getString("aid");
		uid = bundle.getString("uid");
	}
}
