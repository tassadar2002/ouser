package com.ouser.ui.appoint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
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
import com.ouser.logic.event.AppointsEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.Appoint.Status;
import com.ouser.module.Appoints;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.util.Const;

/**
 * 友约列表，不带发布人信息
 * 
 * @author hanlixin
 * 
 */
@SuppressLint("ValidFragment")
public class PublishedAppointListFragment extends BaseListFragment {

	private String mUid = "";
	private Appoints mItems = new Appoints();
	
	/** 是否只获取有效友约 */
	private boolean mFetchOnlyValid = false;

	public PublishedAppointListFragment(String uid) {
		mUid = uid;
	}

	public void setFetchOnlyValid(boolean value) {
		mFetchOnlyValid = value;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		enableAppend(false);
		enableEdit(mUid.equals(Cache.self().getMyUid()));
	}

	@Override
	public void refreshData(boolean append) {
		LogicFactory.self().getAppoint().getPublish(mUid, mFetchOnlyValid, getMainEventListener());
	}

	@Override
	protected String getEditText(int index) {
		return "是否删除该友约？";
	}

	@Override
	protected void onEdit(int index) {
		final int fIndex = index;
		Appoint appoint = mItems.get(index);
		LogicFactory.self().getAppoint().remove(appoint, 
				createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				StatusEventArgs statusArgs = (StatusEventArgs) args;
				if (statusArgs.getErrCode() == OperErrorCode.Success) {
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

	@Override
	protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
		AppointsEventArgs listArgs = (AppointsEventArgs)statusArgs;
		if (append) {
			mItems.addAll(listArgs.getAppoints());
		} else {
			mItems = listArgs.getAppoints();
		}
		return listArgs.getAppoints().isEmpty();
	}

	@Override
	protected int getItemCount() {
		return mItems.size();
	}

	@Override
	protected void onClickItem(int index) {
		ActivitySwitch.toAppointDetailForResult(getActivity(), mItems, mItems.get(index).getAppointId());
	}
	
	@Override
	public View getItemView(int position, View convertView) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getActivity()).inflate(
					R.layout.lvitem_publish_appoint, null);
			holder.txtContent = (TextView) convertView.findViewById(R.id.txt_content);
			holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_status);
			holder.txtViewCount = (TextView) convertView.findViewById(R.id.txt_view_count);
			holder.txtJoinCount = (TextView) convertView.findViewById(R.id.txt_join_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Appoint appoint = mItems.get(position);
		holder.txtContent.setText(Formatter.getAppointContent(appoint));
		holder.txtViewCount.setText(Formatter.getViewCount(appoint));
		holder.txtJoinCount.setText(Formatter.getJoinCount(appoint));
		
		holder.txtStatus.setText(Formatter.getAppointStatus(appoint));
		if(appoint.getStatus() == Status.Ing) {
			holder.txtStatus.setBackgroundColor(Const.Application.getResources().getColor(R.color.ing_appoint_bg));
		} else {
			holder.txtStatus.setBackgroundColor(Const.Application.getResources().getColor(R.color.white));
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView txtContent = null;
		public TextView txtStatus = null;
		public TextView txtViewCount = null;
		public TextView txtJoinCount = null;
	}

}
