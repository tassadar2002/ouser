package com.ouser.ui.ouser;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.OusersEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.Alert;

@SuppressLint("ValidFragment")
class BlackFragment extends BaseListFragment {

	protected List<Ouser> mItems = new ArrayList<Ouser>();
	
	public BlackFragment() {
		enableEdit(true);
		observePhotoDownloadEvent(true);
	}

	@Override
	public void refreshData(boolean append) {
		LogicFactory.self().getOuser().getBlacks(append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
	}

	@Override
	protected int getItemCount() {
		return mItems.size();
	}

	@Override
	protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
		OusersEventArgs listArgs = (OusersEventArgs) statusArgs;
		if (append) {
			mItems.addAll(listArgs.getOusers());
		} else {
			mItems = listArgs.getOusers();
		}
		return listArgs.getOusers().isEmpty();
	}

	@Override
	protected void onClickItem(int index) {
	}

	@Override
	protected String getEditText(int index) {
		return "是否从黑名单中删除？";
	}

	@Override
	protected void onEdit(int index) {
		final int fIndex = index;
		Ouser ouser = mItems.get(index);
		LogicFactory.self().getOuser().removeBlack(ouser, 
				createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				StatusEventArgs statusArgs = (StatusEventArgs) args;
				if (statusArgs.getErrCode() == OperErrorCode.Success) {
					Alert.Toast("删除成功");
					mItems.remove(fIndex);
				} else {
					Alert.handleErrCode(statusArgs.getErrCode());
				}
				mActionListener.onEditingComplete();
			}
		}));
	}

	@Override
	public View getItemView(int position, View convertView) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.lvitem_black, null);
			holder = new ViewHolder();
			holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		} 
		Ouser ouser = mItems.get(position);
		holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(ouser.getPortrait(),
				Photo.Size.Large));
		holder.txtName.setText(ouser.getNickName());

		return convertView;
	}

	private static class ViewHolder {
		public ImageView imagePortrait = null;
		public TextView txtName = null;
	}

}
