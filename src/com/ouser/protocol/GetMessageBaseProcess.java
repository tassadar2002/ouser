package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.ChatMessage;
import com.ouser.util.UrlUtil;

abstract public class GetMessageBaseProcess extends BaseProcess {

	private String mMyUid = "";
	private List<ChatMessage> mResult = new ArrayList<ChatMessage>();

	public void setMyUid(String value) {
		mMyUid = value;
	}
	
	public List<ChatMessage> getResult() {
		return mResult;
	}
	
	abstract protected void fillOwnerParam(JSONObject o) throws JSONException;
	abstract protected void fillOwnerResult(JSONObject o, ChatMessage message) throws JSONException;

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("email", mMyUid);
			fillOwnerParam(o);
			return o.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			List<ChatMessage> messages = new ArrayList<ChatMessage>();
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);

				ChatMessage message = new ChatMessage();
				fillOwnerResult(json, message);
				message.setSend(false);
				message.setTime(json.optLong("chattime"));

				String strChat = UrlUtil.decode(json.optString("chat"));
				JSONObject jsonChat = new JSONObject(strChat);
				message.setType(Util.convertToMessageType(jsonChat.optInt("msgtype")));
				String content = jsonChat.optString("content");
				switch (message.getType()) {
				case Text:
				case Image:
				case Audio:
					message.setContent(content);
					break;
				case Location: {
					JSONObject o = new JSONObject(content);
					message.getLocation().getLocation().setValue(o.optDouble("lat"), o.optDouble("lng"));
					message.getLocation().setPlace(o.optString("address"));
				}
					break;
				case Invite: {
					JSONObject o = new JSONObject(content);
					message.getInvite().setContent(UrlUtil.decode(o.optString("textContent")));
					message.getInvite().getAppointId().setUid(o.optString("owner"));
					message.getInvite().getAppointId().setAid(o.optString("desireid"));
				}
					break;
				}

				messages.add(message);
			}
			mResult = messages;
		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "[]";
	}
}
