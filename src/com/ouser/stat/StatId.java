package com.ouser.stat;

public enum StatId {

	Active("APP_ACTIVE"),
	Register("REGIST_SUM"),
	Login("LOGIN_SUM"),
	Profile("LOOK_PROFILE_SUM"),
	Join("JOIN_SUM"),
	Publish("PUBLISH_NEED_SUM"),
	Follow("FOLLOW_SUM"),
	FollowTag("FOLLOW_TAG_SUM"),
	Invest("INVEST_SUM"),
	SearchTag("SEARCH_TAG_SUM"),
	Shake("SHAKE"),

	SingleChatText("text"),
	SingleChatImage("image"),
	SingleChatLocation("location"),
	SingleChatVoice("voice"),
	GroupChatText("text"),
	GroupChatImage("image"),
	GroupChatLocation("location"),
	GroupChatVoice("voice");
	
	private String mValue;
	StatId(String value) {
		mValue = value;
	}
	String getValue() {
		return mValue;
	}
}
