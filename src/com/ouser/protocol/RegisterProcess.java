package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Ouser;
import com.ouser.module.User;
import com.ouser.util.UrlUtil;

public class RegisterProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/UserRegisterServlet_Android_V2_0";
	
	private User mUser = null;
	private String mPhoneUuid = "";
	private Ouser mOuser = new Ouser();
	private String mSuffix = "";
	private String mImageData = "";
	
	public void setUser(User value) {
		mUser = value;
	}
	public void setPhoneUuid(String value) {
		mPhoneUuid = value;
	}
	public void setOuser(Ouser value) {
		mOuser = value;
	}
	public void setPortraitSuffix(String value) {
		mSuffix = value;
	}
	public void setPortrait(String value) {
		mImageData = value;
	}
	
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("email", mUser.getUid());
			o.put("pwd", mUser.getPassword());
			o.put("lng", String.valueOf(mUser.getLocation().getLng()));
			o.put("lat", String.valueOf(mUser.getLocation().getLat()));
			o.put("uuid", mPhoneUuid);
			o.put("nickname", UrlUtil.encode(mOuser.getNickName()));
			o.put("gender", Util.convertFromGender(mOuser.getGender()));
			o.put("birthday", mOuser.getBirthday());
			o.put("suffix", mSuffix);
			o.put("imgdata", mImageData);
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject o = new JSONObject(result);
			int value = o.optInt("status");
			switch(value) {
			case 0:
				setStatus(ProcessStatus.Success);
				break;
			case -2:
				setStatus(ProcessStatus.ErrUidInvalid);
				break;
			case -11:
				setStatus(ProcessStatus.ErrUidExist);
				break;
			default:
				setStatus(ProcessStatus.ErrUnkown);
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}
	
	@Override
	protected String getFakeResult() {
		return "";
	}
}
