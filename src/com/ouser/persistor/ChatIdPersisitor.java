package com.ouser.persistor;

import com.ouser.module.AppointId;
import com.ouser.module.ChatId;
import com.ouser.module.ChatIdFactory;


class ChatIdPersisitor {
	
	public static final int TypeSingle = 1;
	public static final int TypeGroup = 2;

	public static String toDb(ChatId chatId) {
		if(chatId.isSingle()) {
			return chatId.getSingleId();
		} else {
			return chatId.getGroupId().getAid() + "," + chatId.getGroupId().getUid();
		}
	}
	
	public static int getType(ChatId chatId) {
		return chatId.isSingle() ? TypeSingle : TypeGroup;
	}

	public static ChatId fromDb(int type, String content) {
		try {
			String[] values = content.split(",");
			ChatId chatId = null;
			switch(type) {
			case TypeSingle:
				chatId = ChatIdFactory.create(content);
				break;
			case TypeGroup:
				chatId = ChatIdFactory.create(new AppointId(values[0], values[1]));
				break;
			default:
				break;
			}
			return chatId;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
