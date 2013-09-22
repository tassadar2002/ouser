package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;
import com.ouser.module.ChatIdFactory;
import com.ouser.module.ListMessage;
import com.ouser.util.StringUtil;
import com.ouser.util.UrlUtil;

public class GetListMessageProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/GetNewChatPersonDetailListServlet_Android_V2_0";

	private String mMyUid = "";
	private List<ListMessage> mMessage = new ArrayList<ListMessage>();
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public List<ListMessage> getResult() {
		return mMessage;
	}
	
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {

		try {
			JSONObject o = new JSONObject();
			o.put("email", mMyUid);
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			List<ListMessage> messages = new ArrayList<ListMessage>();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				
				ListMessage message = new ListMessage();
				message.getOuser().setUid(json.optString("aimemail"));
				message.getOuser().setNickName(UrlUtil.decode(json.optString("aimnick")));
				message.getOuser().getPortrait().setUrl(json.optString("aimheadurl"));
				message.setContent(UrlUtil.decode(json.optString("lastchat")));
				message.setTime(json.optInt("chattime"));
				message.setCount(json.optInt("total"));
				
				String aid = json.optString("desireid");
				String uid = json.optString("owner");
				if(!"0".equals(aid) && !StringUtil.isEmpty(uid)) {
					// 群聊
					AppointId appointId = new AppointId(aid, uid);
					message.setChatId(ChatIdFactory.create(appointId));
					message.getAppoint().setAppointId(appointId);
					message.getAppoint().setContent(UrlUtil.decode(json.optString("desire")));
				} else {
					message.setChatId(ChatIdFactory.create(json.optString("aimemail")));
				}
				
				messages.add(message);
			}
			mMessage = messages;
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}
}
