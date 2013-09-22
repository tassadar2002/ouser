package com.ouser.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ouser.logger.Logger;
import com.ouser.logic.LogicFactory;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity  implements IWXAPIEventHandler {
	
	private static final Logger logger = new Logger("WXEntryActivity");

	@Override
	public void onReq(BaseReq resp) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		logger.d("onResp " + resp.errCode);
		boolean success = false;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			success = true;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
		default:
			success = false;
			break;
		}
		LogicFactory.self().getShare().completeShare(success);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logger.d("onCreate");
		LogicFactory.self().getShare().handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		logger.d("onNewIntent");
		LogicFactory.self().getShare().handleIntent(getIntent(), this);
		super.onNewIntent(intent);
	}
}