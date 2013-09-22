package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.util.UrlUtil;

public class GetBlackOusersProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetBlackListServlet_Android_V2_0";
	
	private String mMyUid = "";
	private Util.PageNumber mPage = new Util.PageNumber();
	private Ousers mResult = new Ousers();
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setPageNum(int value) {
		mPage.setValue(value);
	}
	
	public Ousers getResult() {
		return mResult;
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
			o.put("pgnum", mPage.getValue());
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			Ousers ousers = new Ousers();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject jsonOuser = array.optJSONObject(i);
				
				Ouser ouser = new Ouser();
				ouser.setUid(jsonOuser.optString("email"));
				ouser.setNickName(UrlUtil.decode(jsonOuser.optString("nickname")));
				ouser.getPortrait().setUrl(jsonOuser.optString("head"));
				ousers.add(ouser);
			}
			mResult = ousers;
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
