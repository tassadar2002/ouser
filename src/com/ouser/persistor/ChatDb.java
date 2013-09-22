package com.ouser.persistor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ouser.module.ChatId;
import com.ouser.module.ChatMessage;

class ChatDb extends BaseDb {

	private static final String T_Name = "chat";
	private static final String F_Id = "_id";
	private static final String F_MyUid = "myuid";
	private static final String F_Uid = "uid"; // 说话人
	private static final String F_Content = "content";
	private static final String F_Type = "type";
	private static final String F_Time = "time";
	private static final String F_Send = "send";
	private static final String F_ChatType = "chattype";
	private static final String F_Owner = "owner";
	private static final String F_Extra = "extra";

	static final String CreateTabelSql = String.format("CREATE TABLE %s("
			+ "%s INTEGER PRIMARY KEY AUTOINCREMENT, " + "%s TEXT, " + "%s TEXT, " + "%s TEXT, "
			+ "%s INTEGER, " + "%s INTEGER, " + "%s INTEGER, " + "%s INTEGER, " + "%s TEXT, "
			+ "%s TEXT)", T_Name, F_Id, F_MyUid, F_Uid, F_Content, F_Type, F_Time, F_Send,
			F_ChatType, F_Owner, F_Extra);

	public void append(String myUid, ChatMessage message) {
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		messages.add(message);
		append(myUid, messages);
	}

	public void append(String myUid, List<ChatMessage> messages) {
		try {
			SQLiteDatabase db = openWritableDatabase();
			for (ChatMessage message : messages) {
				ContentValues value = new ContentValues();
				value.put(F_MyUid, myUid);
				value.put(F_Uid, message.getOuser().getUid());
				value.put(F_Content, Util.convertToContent(message));
				value.put(F_Type, Util.convertFromMessageType(message.getType()));
				value.put(F_Time, message.getTime());
				value.put(F_Send, Util.convertFromBoolean(message.isSend()));

				value.put(F_ChatType, ChatIdPersisitor.getType(message.getChatId()));
				value.put(F_Owner, ChatIdPersisitor.toDb(message.getChatId()));

				long id = db.insert(T_Name, null, value);
				if (id != -1) {
					message.setId(id);
				}
			}
		} finally {
			finalize();
		}
	}

	public void remove(List<ChatMessage> messages) {
		try {
			SQLiteDatabase db = openWritableDatabase();
			for (ChatMessage message : messages) {
				db.delete(T_Name, String.format("%s=?", F_Id),
						new String[] { String.valueOf(message.getId()) });
			}
		} finally {
			finalize();
		}
	}
	
	public void clear(String myUid, ChatId chatId) {
		try {
			SQLiteDatabase db = openWritableDatabase();
			db.delete(T_Name, getWhere(), getWhereArgs(myUid, chatId));
		} finally {
			finalize();
		}
	}

	public List<ChatMessage> getList(String myUid, ChatId chatId) {
		try {
			Cursor c = query(T_Name, new String[] { F_Id, F_Uid, F_Content, F_Type, F_Time, F_Send,
					F_ChatType, F_Owner }, 
					getWhere(), getWhereArgs(myUid, chatId), String.format("%s DESC", F_Time));
			return convert(c);
		} finally {
			finalize();
		}
	}

	protected List<ChatMessage> convert(Cursor c) {
		ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
		if (!c.moveToFirst()) {
			return messages;
		}
		do {
			ChatMessage message = new ChatMessage();
			message.setId(c.getInt(c.getColumnIndex(F_Id)));
			message.getOuser().setUid(c.getString(c.getColumnIndex(F_Uid)));
			message.setType(Util.convertToMessageType(c.getInt(c.getColumnIndex(F_Type))));
			message.setTime(c.getLong(c.getColumnIndex(F_Time)));
			message.setSend(c.getInt(c.getColumnIndex(F_Send)) == 0);
			Util.convertFromContent(message, c.getString(c.getColumnIndex(F_Content)));

			int chatType = c.getInt(c.getColumnIndex(F_ChatType));
			String ownerStr = c.getString(c.getColumnIndex(F_Owner));
			message.setChatId(ChatIdPersisitor.fromDb(chatType, ownerStr));

			messages.add(message);
		} while (c.moveToNext());
		return messages;
	}
	
	private String getWhere() {
		return String.format("%s=? and %s=? and %s=?", F_MyUid, F_ChatType, F_Owner);
	}
	
	private String[] getWhereArgs(String myUid, ChatId chatId) {
		return new String[] { myUid, String.valueOf(ChatIdPersisitor.getType(chatId)), ChatIdPersisitor.toDb(chatId) };
	}
}
