package com.ouser.ui.topframework;

public enum TopFragmentType {

	NearAppoint(1),
	SearchAppoint(2),
	MyFriend(3),
	MyMessage(4),
	Timeline(5),
	MyAppoint(6),
	Setting(7);
	
	private int mValue = 0;
	private TopFragmentType(int value) {
		mValue = value;
	}
	public int getValue() {
		return mValue;
	}
	
	public static TopFragmentType find(int value) {
		for(TopFragmentType type : TopFragmentType.values()) {
			if(type.getValue() == value) {
				return type;
			}
		}
		return null;
	}
}
