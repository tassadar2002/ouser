package com.ouser.logic;

import java.util.List;
import java.util.Stack;

import android.graphics.Bitmap;
import android.util.Pair;

import com.ouser.cache.Cache;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.logic.event.OusersEventArgs;
import com.ouser.module.Ouser;
import com.ouser.protocol.EditAboutmeProcess;
import com.ouser.protocol.EditProfileProcess;
import com.ouser.protocol.GetProfileProcess;
import com.ouser.protocol.GetSimpleProfilesProcess;
import com.ouser.protocol.ResponseListener;
import com.ouser.protocol.SetPortraitImageProcess;
import com.ouser.protocol.SetPortraitUrlProcess;
import com.ouser.stat.Stat;
import com.ouser.stat.StatId;
import com.ouser.util.ImageUtil;

public class ProfileLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new ProfileLogic();
		}
	}
	
	ProfileLogic() {
	}
	
	private Stack<Pair<String, EventListener>> mReqeusts = new Stack<Pair<String, EventListener>>();

	/**
	 * 查看profile，会进行统计
	 * 
	 * @param uid
	 */
	public void seeProfile(String uid) {
		Stat.onEvent(StatId.Profile);
		get(uid);
	}

	/**
	 * 获得用户信息并放入缓存，只发出广播
	 * 
	 * @param uid
	 */
	public void get(String uid) {
		get(uid, null);
	}

	/**
	 * 获得用户信息并放入缓存，发出广播并进行指定的回调
	 * 
	 * @param uid
	 * @param listener
	 */
	public void get(String uid, final EventListener listener) {
		mReqeusts.push(new Pair<String, EventListener>(uid, listener));
		if (mReqeusts.size() == 1) {
			get();
		}
	}

	private void get() {
		if (mReqeusts.isEmpty()) {
			return;
		}
		Pair<String, EventListener> item = mReqeusts.pop();
		final String uid = item.first;
		final EventListener listener = item.second;

		if (Cache.self().isOuserValid(uid)) {
			Ouser ouser = Cache.self().getOuser(uid);
			if (listener == null) {
				fireEvent(EventId.eGetOuserProfile, new OuserEventArgs(ouser));
			} else {
				fireEvent(listener, new OuserEventArgs(ouser));
			}
			runNextRequest();
			return;
		}

		final Ouser ouser = new Ouser();
		ouser.setUid(uid);

		final GetProfileProcess process = new GetProfileProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTarget(ouser);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				OuserEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					Ouser value = process.getTarget();
					Cache.self().setOuser(value);
					args = new OuserEventArgs(value);
				} else {
					args = new OuserEventArgs(ouser, errCode);
				}
				if (listener != null) {
					fireEvent(listener, args);
				}
				fireEvent(EventId.eGetOuserProfile, args);
				runNextRequest();
			}
		});
	}
	
	private void runNextRequest() {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				get();
			}
		});
	}

	public void getSimples(List<String> uids, final EventListener listener) {
		final GetSimpleProfilesProcess process = new GetSimpleProfilesProcess();
		process.setUids(uids);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				fireEvent(listener, new OusersEventArgs(process.getResult(), errCode));
			}
		});
	}

	/**
	 * 编辑我的用户信息
	 * 
	 * @param mySelf
	 */
	public void edit(Ouser mySelf, final EventListener listener) {
		final EditProfileProcess process = new EditProfileProcess();
		process.setOuser(mySelf);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if (errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}

	/**
	 * 编辑我的态度
	 * 
	 * @param mySelf
	 */
	public void editAboutme(Ouser mySelf, final EventListener listener) {
		final EditAboutmeProcess process = new EditAboutmeProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setAboutMe(mySelf.getAboutme());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if (errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}

	/**
	 * 设置自己头像为照片中的某一张
	 * 
	 * @param url
	 * @param listener
	 */
	public void setPortraitUrl(String url, final EventListener listener) {
		final SetPortraitUrlProcess process = new SetPortraitUrlProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setPortraitUrl(url);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if (errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}

	/**
	 * 设置自己头像为新图片
	 * 
	 * @param image
	 * @param listener
	 */
	public void setPortraitImage(Bitmap image, final EventListener listener) {
		final SetPortraitImageProcess process = new SetPortraitImageProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setSuffix("JPEG");
		process.setData(ImageUtil.toBase64(image));
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if (errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}

}
