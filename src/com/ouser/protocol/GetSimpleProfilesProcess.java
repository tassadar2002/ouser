package com.ouser.protocol;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.util.UrlUtil;

public class GetSimpleProfilesProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/BatchGetUserNickServlet_V2_0";

	private List<String> mUids = null;
	private Ousers mOusers = new Ousers();

	public void setUids(List<String> value) {
		mUids = value;
	}

	public Ousers getResult() {
		return mOusers;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		JSONArray array = new JSONArray();
		for (String uid : mUids) {
			array.put(uid);
		}
		return array.toString();
	}

	@Override
	protected void onResult(String result) {
		try {
			Ousers ousers = new Ousers();
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				Ouser ouser = new Ouser();
				ouser.setUid(json.optString("email"));
				ouser.setNickName(UrlUtil.decode(json.optString("nick")));
				ouser.getPortrait().setUrl(json.optString("head"));
				ousers.add(ouser);
			}
			mOusers = ousers;

		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}
}
