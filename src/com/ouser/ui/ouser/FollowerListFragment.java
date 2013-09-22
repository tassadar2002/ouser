package com.ouser.ui.ouser;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Ouser;
import com.ouser.ui.helper.Alert;

/**
 * 关注列表
 * @author Administrator
 *
 */
@SuppressLint("ValidFragment")
public class FollowerListFragment extends RelationListFragment {
	
	public FollowerListFragment(String uid) {
		super(uid);
		enableEdit(mUid.equals(Cache.self().getMyUid()));
	}

	@Override
	public void refreshData(boolean append) {
		LogicFactory.self().getOuser().getFollowers(mUid, append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
	}

	@Override
	protected String getEditText(int index) {
		return "是否删除？";
	}

	@Override
	protected void onEdit(int index) {
		final int fIndex = index;
		Ouser ouser = mItems.get(index).getOuser();
		LogicFactory.self().getOuser().removeFollow(ouser, 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				StatusEventArgs statusArgs = (StatusEventArgs)args;
				if(statusArgs.getErrCode() == OperErrorCode.Success) {
					Alert.Toast("删除成功");
					mItems.remove(fIndex);
					getActivity().setResult(Activity.RESULT_OK);
				} else {
					Alert.handleErrCode(statusArgs.getErrCode());
				}
				mActionListener.onEditingComplete();
			}
		}));
	}
	
	
}
