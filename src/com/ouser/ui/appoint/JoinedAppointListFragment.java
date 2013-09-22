package com.ouser.ui.appoint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointsWithPublisherEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.Appoint.Status;
import com.ouser.module.AppointsWithPublisher;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.util.Const;

/**
 * 参加的友约
 * @author hanlixin
 *
 */
@SuppressLint("ValidFragment")
public class JoinedAppointListFragment extends BaseListFragment {
	
	private String mUid = "";
	private AppointsWithPublisher mItems = new AppointsWithPublisher();
	
	public JoinedAppointListFragment(String uid) {
		mUid = uid;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		enableAppend(false);
		enableEdit(mUid.equals(Cache.self().getMyUid()));
		observePhotoDownloadEvent(true);
	}

	@Override
	protected void refreshData(boolean append) {
		LogicFactory.self().getAppoint().getJoin(getMainEventListener());
	}

	@Override
	protected String getEditText(int index) {
		return "是否退出该友约？";
	}

	@Override
	protected void onEdit(int index) {
		final int fIndex = index;
		Appoint appoint = mItems.getAppoint(index);
		LogicFactory.self().getAppoint().exit(appoint, 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				StatusEventArgs statusArgs = (StatusEventArgs)args;
				if(statusArgs.getErrCode() == OperErrorCode.Success) {
					Alert.Toast("退出成功");
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
		AppointsWithPublisherEventArgs listArgs = (AppointsWithPublisherEventArgs)statusArgs;
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
		AppointValidChecker.check(mItems.getAppoint(index), 
				getFragment(), new AppointValidChecker.OnValidListener() {
			
			@Override
			public void onValid(Appoint appoint) {
				ActivitySwitch.toAppointDetailForResult(getActivity(), appoint);
			}
		});
	}
	
	@Override
	public View getItemView(int position, View convertView) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getActivity()).inflate(
					R.layout.lvitem_join_appoint, null);
			holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
			holder.imageGender = (ImageView)convertView.findViewById(R.id.image_gender);
			holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_status);
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
			holder.txtContent = (TextView) convertView.findViewById(R.id.txt_content);
			holder.txtViewCount = (TextView) convertView.findViewById(R.id.txt_view_count);
			holder.txtJoinCount = (TextView) convertView.findViewById(R.id.txt_join_count);
			holder.txtAge = (TextView)convertView.findViewById(R.id.txt_age);
			holder.txtDistance = (TextView)convertView.findViewById(R.id.txt_distance);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Pair<Appoint, Ouser> item = mItems.get(position);
		Appoint appoint = item.first;
		Ouser publisher = item.second;
		holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
				publisher.getPortrait(), Photo.Size.Large));
		holder.imagePortrait.setOnClickListener(
				ActivitySwitch.getToProfileClickListener(getActivity(), publisher.getUid()));
		holder.imageGender.setImageResource(Formatter.getGenderIcon(publisher.getGender()));
		holder.txtName.setText(publisher.getNickName());
		holder.txtContent.setText(Formatter.getAppointContent(appoint));
		holder.txtViewCount.setText(Formatter.getViewCount(appoint));
		holder.txtJoinCount.setText(Formatter.getJoinCount(appoint));
		holder.txtAge.setText(String.valueOf(publisher.getAge()));
		
		holder.txtStatus.setText(Formatter.getAppointStatus(appoint));
		if(appoint.getStatus() == Status.Ing) {
			holder.txtStatus.setBackgroundColor(Const.Application.getResources().getColor(R.color.ing_appoint_bg));
		} else {
			holder.txtStatus.setBackgroundColor(Const.Application.getResources().getColor(R.color.white));
		}
		
		if(appoint.getDistance() == Const.DefaultValue.Distance) {
			holder.txtDistance.setVisibility(View.GONE);
		} else {
			holder.txtDistance.setVisibility(View.VISIBLE);
			holder.txtDistance.setText(Formatter.formatDistance(appoint.getDistance()));
		}
		return convertView;
	}

	private class ViewHolder {
		public ImageView imagePortrait = null;
		public ImageView imageGender = null;
		public TextView txtStatus = null;
		public TextView txtName = null;
		public TextView txtContent = null;
		public TextView txtViewCount = null;
		public TextView txtJoinCount = null;
		public TextView txtAge = null;
		public TextView txtDistance = null;
	}
}
