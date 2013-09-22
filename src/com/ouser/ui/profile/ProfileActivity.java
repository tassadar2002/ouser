package com.ouser.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.module.Ouser;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.util.Const;

/**
 * 个人资料
 * @author hanlixin
 * @remark 从任何界面回来都要刷新,逻辑层会在适当情况下无效化缓存
 * 有可能进入聊天后再进入其他界面，所以无法精确判断profile是否还有效
 * 有可能进入聊天后再进入其他界面，所以不要仅仅判断resultCode == Activity.RESULT_OK，一切都可能导致刷新
 */
public class ProfileActivity extends BaseActivity {	
	
	private HeadBar mHeadBar = new HeadBar();
	
	private Ouser mOuser = new Ouser();
	
	private HandlerFactory mHandlerFactory = new HandlerFactory();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_profile);

		mHeadBar.onCreate(findViewById(R.id.layout_head_bar), this);
		
		String uid = getIntent().getExtras().getString(Const.Intent.Uid);
		mOuser.setUid(uid);
		
		for(BaseHandler handler : mHandlerFactory.getHandlers()) {
			handler.setActivity(this);
			handler.onCreate();
		}
		
		mHeadBar.setActionButton(
				isMySelf() ? R.drawable.title_edit : R.drawable.title_chat, 
				new HeadBar.OnActionListener() {
					
					@Override
					public void onClick() {
						mHandlerFactory.getInfo().clickActionButton();
					}
				});
	}

	@Override
	protected void onPause() {
		for(BaseHandler handler : mHandlerFactory.getHandlers()) {
			handler.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		for(BaseHandler handler : mHandlerFactory.getHandlers()) {
			handler.onDestroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		for(BaseHandler handler : mHandlerFactory.getHandlers()) {
			handler.onActivityResult(arg0, arg1, arg2);
		}
	}

	public void setHeadBarTitle(String value) {
		mHeadBar.setTitle(value);
	}

	public Ouser getOuser() {
		return mOuser;
	}
	
	public void setOuser(Ouser value) {
		mOuser = value;
	}
	
	public boolean isMySelf() {
		return mOuser.getUid().equals(Cache.self().getMyUid());
	}
	
	public HandlerFactory getHandlerFactory() {
		return mHandlerFactory;
	}
}





