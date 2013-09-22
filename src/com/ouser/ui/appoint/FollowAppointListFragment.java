package com.ouser.ui.appoint;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.FollowAppointsEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.FollowAppoint;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.topframework.TopActivity;
import com.ouser.ui.topframework.TopFragmentType;
import com.ouser.util.Const;

/**
 * 关注的友约列表
 * 
 * @author hanlixin
 * 
 */
@SuppressLint("ValidFragment")
public class FollowAppointListFragment extends BaseListFragment {

	private String mUid = "";
	private List<FollowAppoint> mItems = new ArrayList<FollowAppoint>();

	public FollowAppointListFragment(String uid) {
		mUid = uid;
		enableEdit(mUid.equals(Cache.self().getMyUid()));
	}

	@Override
	public void refreshData(boolean append) {
		LogicFactory.self().getAppoint().getFollow(mUid, append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
	}

	@Override
	protected int getItemCount() {
		return mItems.size();
	}

	@Override
	protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
		FollowAppointsEventArgs listArgs = (FollowAppointsEventArgs) statusArgs;
		if (append) {
			mItems.addAll(listArgs.getAppoints());
		} else {
			mItems = listArgs.getAppoints();
		}
		return listArgs.getAppoints().isEmpty();
	}

	@Override
	protected String getEditText(int index) {
		return "是否取消对该标签的关注？";
	}

	@Override
	protected void onEdit(int index) {
		final int fIndex = index;
		FollowAppoint appoint = mItems.get(index);
		LogicFactory.self().getAppoint().removeFollow(appoint.getTag(), 
				createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				StatusEventArgs statusArgs = (StatusEventArgs) args;
				if (statusArgs.getErrCode() == OperErrorCode.Success) {
					Alert.Toast("取消关注成功");
					mItems.remove(fIndex);
					getActivity().setResult(Activity.RESULT_OK);
				} else {
					Alert.handleErrCode(statusArgs.getErrCode());
				}
				mActionListener.onEditingComplete();
			}
		}));
	}

	@Override
	protected void onClickItem(int index) {
		FollowAppoint appoint = mItems.get(index);
		Intent intent = new Intent(getActivity(), TopActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(Const.Intent.SwitchPage, TopFragmentType.SearchAppoint.getValue());
		intent.putExtra(SearchAppointFragment.IntentKeyword, appoint.getTag());
		startActivity(intent);
	}

	@Override
	public View getItemView(int position, View convertView) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getActivity()).inflate(
					R.layout.lvitem_follow_appoint, null);
			holder.txtTag = (TextView) convertView.findViewById(R.id.txt_tag);
			holder.txtCount = (TextView) convertView.findViewById(R.id.txt_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FollowAppoint appoint = mItems.get(position);
		holder.txtTag.setText(appoint.getTag());

		String format = "%d条友约，热力指数%d";
		holder.txtCount.setText(String.format(format, appoint.getCount(), appoint.getHot()));
		return convertView;
	}

	private static class ViewHolder {
		public TextView txtTag = null;
		public TextView txtCount = null;
	}
}
