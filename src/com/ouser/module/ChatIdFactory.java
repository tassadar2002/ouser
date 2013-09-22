package com.ouser.module;

import android.os.Bundle;


public class ChatIdFactory {

	///////////////////////bundle//////////////////////
	public static Bundle toBundle(ChatId chatId) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("single", chatId.isSingle());
		if(chatId.isSingle()) {
			((SingleId)chatId).toBundle(bundle);
		} else {
			((GroupId)chatId).toBundle(bundle);
		}
		return bundle;
	}

	public static ChatId fromBundle(Bundle value) {
		boolean isSingle = value.getBoolean("single");
		if(isSingle) {
			return new SingleId(value);
		} else {
			return new GroupId(value);
		}
	}
	
	///////////////////////create//////////////////////
	public static ChatId create(String uid) {
		return new SingleId(uid);
	}
	
	public static ChatId create(AppointId appointId) {
		return new GroupId(appointId);
	}

	///////////////////////SingleId//////////////////////
	private static class SingleId implements ChatId {
		private String mUid = "";
		public SingleId(Bundle bundle) {
			mUid = bundle.getString("uid");
		}
		public SingleId(String uid) {
			mUid = uid;
		}
		
		public void toBundle(Bundle bundle) {
			bundle.putString("uid", mUid);
		}

		@Override
		public boolean isSingle() {
			return true;
		}
		
		@Override
		public String getSingleId() {
			return mUid;
		}
		
		@Override
		public AppointId getGroupId() {
			return null;
		}
	}
	
	///////////////////////GroupId//////////////////////
	private static class GroupId implements ChatId {
		private AppointId mAppointId = new AppointId();
		
		public GroupId(AppointId value) {
			mAppointId = value;
		}
		public GroupId(Bundle bundle) {
			mAppointId.setUid(bundle.getString("uid"));
			mAppointId.setAid(bundle.getString("aid"));
		}
		public void toBundle(Bundle bundle) {
			bundle.putString("uid", mAppointId.getUid());
			bundle.putString("aid", mAppointId.getAid());
		}

		@Override
		public boolean isSingle() {
			return false;
		}
		
		@Override
		public String getSingleId() {
			return null;
		}
		
		@Override
		public AppointId getGroupId() {
			return mAppointId;
		}
	}
}
