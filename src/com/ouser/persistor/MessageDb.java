package com.ouser.persistor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ouser.module.ChatId;
import com.ouser.module.ListMessage;

class MessageDb extends BaseDb {

	private static final String T_Name = "message";
	private static final String F_Id = "_id";
	private static final String F_MyUid = "myuid";
	private static final String F_Uid = "uid";
	private static final String F_Content = "content";
	private static final String F_Time = "time";
	private static final String F_Count = "count";
	private static final String F_Type = "type";
	private static final String F_Owner = "owner";
	private static final String F_Extra = "extra";

	static final String CreateTabelSql = String.format("CREATE TABLE %s("
			+ "%s INTEGER PRIMARY KEY AUTOINCREMENT, " + "%s TEXT, "
			+ "%s TEXT, " + "%s TEXT, " + "%s INTEGER, " + "%s INTEGER, " + "%s INTEGER, "
			+ "%s TEXT, " + "%s TEXT)", T_Name, F_Id, F_MyUid, F_Uid, 
			F_Content, F_Time, F_Count, F_Type, F_Owner, F_Extra);

	/**
	 * 设置消息概况
	 * 
	 * @param myUid
	 *            本人uid
	 * @param messages
	 * @remark 按照藕丝分类，仅保存最后一条消息
	 */
	public void set(String myUid, List<ListMessage> messages) {

		try {
			SQLiteDatabase db = openWritableDatabase();
			for (ListMessage message : messages) {
				ContentValues values = new ContentValues();
				values.put(F_Uid, message.getOuser().getUid());
				values.put(F_Content, message.getContent());
				values.put(F_Time, message.getTime());
				values.put(F_Count, message.getCount());

				ChatId chatId = message.getChatId();
				if (contain(myUid, chatId)) {
					// 已存在，更新
					db.update(T_Name, values, getWhere(), getWhereArgs(myUid, chatId));
				} else {
					// 不存在，插入
					values.put(F_MyUid, myUid);
					values.put(F_Type, ChatIdPersisitor.getType(chatId));
					values.put(F_Owner, ChatIdPersisitor.toDb(chatId));
					values.put(F_Extra, getExtra(message.getAppoint().getContent()));
					db.insert(T_Name, null, values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}

	public void setLastMessageAppointContent(String myUid, ChatId chatId, String content) {
		try {
			ContentValues values = new ContentValues();
			values.put(F_Extra, getExtra(content));
			openWritableDatabase().update(T_Name, values, getWhere(), getWhereArgs(myUid, chatId));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}

	public void remove(String myUid, ChatId chatId) {
		try {
			openWritableDatabase().delete(T_Name, getWhere(), getWhereArgs(myUid, chatId));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}

	public ListMessage get(String myUid, ChatId chatId) {
		try {
			String[] columns = new String[] { F_Uid, F_Content, F_Count,
					F_Time, F_Type, F_Owner, F_Extra };
			Cursor c = query(T_Name, columns, getWhere(), getWhereArgs(myUid, chatId));
			return convertOne(c);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
		return null;
	}

	public List<ListMessage> getList(String myUid) {

		try {
			String[] columns = new String[] { F_Uid, F_Content, F_Count,
					F_Time, F_Type, F_Owner, F_Extra };
			Cursor c = query(T_Name, columns, F_MyUid + "=?", new String[] { myUid }, F_Time
					+ " DESC");
			return convert(c);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
		return new ArrayList<ListMessage>();
	}

	/**
	 * 设置指定uid的最后一条消息
	 * 
	 * @param myUid
	 * @param message
	 */
	public void setLastMessage(String myUid, ListMessage message) {
		try {
			ContentValues values = new ContentValues();
			values.put(F_Uid, message.getOuser().getUid());
			values.put(F_Content, message.getContent());
			values.put(F_Time, message.getTime());
			values.put(F_Count, 0);

			ChatId chatId = message.getChatId();
			if (contain(myUid, chatId)) {
				// 已存在，更新
				openWritableDatabase().update(T_Name, values, getWhere(),
						getWhereArgs(myUid, chatId));
			} else {
				// 不存在，插入
				values.put(F_MyUid, myUid);
				values.put(F_Type, ChatIdPersisitor.getType(chatId));
				values.put(F_Owner, ChatIdPersisitor.toDb(chatId));
				values.put(F_Extra, getExtra(message.getAppoint().getContent()));
				openWritableDatabase().insert(T_Name, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}

	public void clearUnreadCount(String myUid, ChatId chatId) {
		try {
			ContentValues values = new ContentValues();
			values.put(F_Count, 0);
			openWritableDatabase().update(T_Name, values, getWhere(), getWhereArgs(myUid, chatId));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}
	
	public void clearUnreadCount(String myUid) {
		try {
			ContentValues values = new ContentValues();
			values.put(F_Count, 0);
			openWritableDatabase().update(T_Name, values, 
					String.format("%s=?", F_MyUid), 
					new String[]{ myUid });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}

	private List<ListMessage> convert(Cursor c) {
		List<ListMessage> result = new ArrayList<ListMessage>();
		try {
			if (c.moveToFirst()) {
				do {
					ListMessage message = new ListMessage();
					message.getOuser().setUid(c.getString(c.getColumnIndex(F_Uid)));
					message.setContent(c.getString(c.getColumnIndex(F_Content)));
					message.setCount(c.getInt(c.getColumnIndex(F_Count)));
					message.setTime(c.getInt(c.getColumnIndex(F_Time)));

					int type = c.getInt(c.getColumnIndex(F_Type));
					String ownerStr = c.getString(c.getColumnIndex(F_Owner));
					message.setChatId(ChatIdPersisitor.fromDb(type, ownerStr));
					if (!message.isSingle()) {
						message.getAppoint().setAppointId(message.getChatId().getGroupId());
					}
					setExtra(message, c.getString(c.getColumnIndex(F_Extra)));

					result.add(message);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private ListMessage convertOne(Cursor c) {
		ListMessage message = null;
		try {
			if (c.moveToFirst()) {
				message = new ListMessage();
				message.getOuser().setUid(c.getString(c.getColumnIndex(F_Uid)));
				message.setContent(c.getString(c.getColumnIndex(F_Content)));
				message.setCount(c.getInt(c.getColumnIndex(F_Count)));
				message.setTime(c.getInt(c.getColumnIndex(F_Time)));

				int type = c.getInt(c.getColumnIndex(F_Type));
				String ownerStr = c.getString(c.getColumnIndex(F_Owner));
				message.setChatId(ChatIdPersisitor.fromDb(type, ownerStr));
				if (!message.isSingle()) {
					message.getAppoint().setAppointId(message.getChatId().getGroupId());
				}
				setExtra(message, c.getString(c.getColumnIndex(F_Extra)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	private boolean contain(String myUid, ChatId chatId) {
		try {
			Cursor c = query(T_Name, new String[] { "count(1)" }, getWhere(),
					getWhereArgs(myUid, chatId));
			if (c.moveToFirst()) {
				return c.getInt(0) == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalizeCursor();
		}
		return false;
	}

	private String getWhere() {
		return String.format("%s=? and %s=? and %s=?", F_MyUid, F_Type, F_Owner);
	}

	private String[] getWhereArgs(String myUid, ChatId chatId) {
		return new String[] { myUid, String.valueOf(ChatIdPersisitor.getType(chatId)),
				ChatIdPersisitor.toDb(chatId) };
	}

	private String getExtra(String content) {
		try {
			JSONObject o = new JSONObject();
			o.put("aname", content);
			return o.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void setExtra(ListMessage message, String extra) {
		try {
			JSONObject o = new JSONObject(extra);
			message.getAppoint().setContent(o.optString("aname"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
