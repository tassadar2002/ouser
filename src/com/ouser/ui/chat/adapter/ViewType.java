package com.ouser.ui.chat.adapter;

class ViewType {

	public static final int Count = 1/* time */+ 2/* send,rev */* 5/*
																	 * text,
																	 * image ,
																	 * location, 
																	 * audio, 
																	 * invite
																	 */;

	public static final int Time = 0;
	public static final int TextSend = 1;
	public static final int TextRecv = 2;
	public static final int ImageSend = 3;
	public static final int ImageRecv = 4;
	public static final int LocationSend = 5;
	public static final int LocationRecv = 6;
	public static final int AudioSend = 7;
	public static final int AudioRecv = 8;
	public static final int InviteSend = 9;
	public static final int InviteRecv = 10;
	
	public static enum Type {
		Time,
		Text,
		Image,
		Location,
		Audio,
		Invite,
	}
	
	public static Type convert(ChatItem item) {
		if(item.getType() == ChatItem.Type.Time) {
			return Type.Time;
		} else {
			switch(item.getMessage().getType()) {
			case Text:
				return Type.Text;
			case Image:
				return Type.Image;
			case Location:
				return Type.Location;
			case Audio:
				return Type.Audio;
			case Invite:
				return Type.Invite;
			}
			return Type.Text;
		}

	}
}
