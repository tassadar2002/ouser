package com.ouser.protocol;

import com.ouser.module.Appoint;
import com.ouser.module.ChatMessage;
import com.ouser.module.Gender;

class Util {
	
	public static class PageNumber {
		private int mValue = 0;
		public void setValue(int value) {
			mValue = value;
		}
		public String getValue() {
			if(mValue == 0) {
				return "1";
			} else {
				return String.valueOf(mValue + 2);
			}
		}
	}

	public static Gender convertToGender(String value) {
		if(value.equalsIgnoreCase("M")) {
			return Gender.Male;
		}
		if(value.equalsIgnoreCase("F")) {
			return Gender.Female;
		}
		return Gender.None;
	}
	
	public static String convertFromGender(Gender value) {
		switch(value) {
		case Male:
			return "M";
		case Female:
			return "F";
		default:
			return "";
		}
	}

	public static Appoint.Status convertToStatus(String value) {
		if(value.equals("1")) {
			return Appoint.Status.Ing;
		} else if(value.equals("3")) {
			return Appoint.Status.Done;
		} else if(value.equals("-1")) {
			return Appoint.Status.Delete;
		} else {
			return Appoint.Status.None;
		}
	}

	public static Appoint.Relation convertToRelation(String value) {
		if(value.equals("1")) {
			return Appoint.Relation.Join;
		} else if(value.equals("2")) {
			return Appoint.Relation.Publish;
		} else {
			return Appoint.Relation.None;
		}
	}
	
	public static ChatMessage.Type convertToMessageType(int value) {
		switch(value) {
		case 0:
			return ChatMessage.Type.Text;
		case 1:
			return ChatMessage.Type.Image;
		case 2:
			return ChatMessage.Type.Location;
		case 3:
			return ChatMessage.Type.Audio;
		case 5:
			return ChatMessage.Type.Invite;
		default:
			return ChatMessage.Type.Text;
		}
	}
	
	public static String convertFromMessageType(ChatMessage.Type value) {
		switch(value) {
		case Text:
			return "0";
		case Image:
			return "1";
		case Location:
			return "2";
		case Audio:
			return "3";
		case Invite:
			return "5";
		default:
			return "0";
		}
	}
}
