package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.ChatMessage;
import com.ouser.util.UrlUtil;

abstract public class SendMessageBaseProcess extends BaseProcess {

	protected String mMyUid = "";
	protected ChatMessage mMessage = null;

	public void setMyUid(String value) {
		this.mMyUid = value;
	}
	public void setMessage(ChatMessage message) {
		this.mMessage = message;
	}
	
	abstract protected void fillOwnerParam(JSONObject o) throws JSONException;

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject json = new JSONObject();
			json.put("email", mMyUid);
			json.put("chat", UrlUtil.encode(createChatMessageJson().toString()));
			fillOwnerParam(json);
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject o = new JSONObject(result);
			int value = o.optInt("status");
			switch (value) {
			case 0:
				setStatus(ProcessStatus.Success);
				break;
			case -10:
				setStatus(ProcessStatus.ErrUnkown);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "{status:0}";
	}

	private JSONObject createChatMessageJson() throws JSONException {
		JSONObject jsonChat = new JSONObject();
		switch (mMessage.getType()) {
		case Text:
		case Image:
		case Audio:
			jsonChat.put("content", mMessage.getContent());
			break;
		case Location: {
			JSONObject o = new JSONObject();
			o.put("lat", String.valueOf(mMessage.getLocation().getLocation().getLat()));
			o.put("lng", String.valueOf(mMessage.getLocation().getLocation().getLng()));
			o.put("address", mMessage.getLocation().getPlace());
			jsonChat.put("content", o.toString());
		}
			break;
		case Invite: {
			JSONObject o = new JSONObject();
			o.put("textContent", UrlUtil.encode(mMessage.getInvite().getContent()));
			o.put("owner", mMessage.getInvite().getAppointId().getUid());
			o.put("desireid", mMessage.getInvite().getAppointId().getAid());
			jsonChat.put("content", o.toString());
		}
			break;
		}
		jsonChat.put("msgtype", Util.convertFromMessageType(mMessage.getType()));
		return jsonChat;
	}
}
