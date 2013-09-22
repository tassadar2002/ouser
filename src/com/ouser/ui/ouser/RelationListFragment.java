package com.ouser.ui.ouser;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.image.PhotoManager;
import com.ouser.logic.event.OusersEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Formatter;

abstract public class RelationListFragment extends BaseListFragment {

	protected String mUid = "";
	private boolean mSelectOrToInfo = false;
	protected List<Item> mItems = new ArrayList<Item>();

	public RelationListFragment(String uid) {
		mUid = uid;
		observePhotoDownloadEvent(true);
	}
	
	public void setSelectOrToInfo(boolean value) {
		mSelectOrToInfo = value;
	}

	@Override
	protected int getItemCount() {
		return mItems.size();
	}

	@Override
	protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
		OusersEventArgs listArgs = (OusersEventArgs) statusArgs;
		List<Item> items = convertToItems(listArgs.getOusers());
		if (append) {
			mItems.addAll(items);
		} else {
			mItems = items;
		}
		return listArgs.getOusers().isEmpty();
	}

	@Override
	protected void onClickItem(int index) {
		if(mSelectOrToInfo) {
			Item item = mItems.get(index);
			item.setSelect(!item.isSelect());
			mAdapter.notifyDataSetChanged();
		} else {
			Ouser ouser = mItems.get(index).getOuser();
			ActivitySwitch.toProfile(getActivity(), ouser);
		}
	}

	public void setData(List<Ouser> value) {
		mItems = convertToItems(value);
	}
	
	public List<Ouser> getSelected() {
		List<Ouser> result = new ArrayList<Ouser>();
		for(Item item : mItems) {
			if(item.isSelect()) {
				result.add(item.getOuser());
			}
		}
		return result;
	}
	
	private List<Item> convertToItems(List<Ouser> ousers) {
		List<Item> items = new ArrayList<Item>();
		for(Ouser ouser : ousers) {
			Item item = new Item();
			item.setOuser(ouser);
			items.add(item);
		}
		return items;
	}

	@Override
	public View getItemView(int position, View convertView) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.lvitem_ouser, null);
			holder = new ViewHolder();
			holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
			holder.txtAge = (TextView) convertView.findViewById(R.id.txt_age);
			holder.imageGender = (ImageView) convertView.findViewById(R.id.image_gender);
			holder.txtDistance = (TextView) convertView.findViewById(R.id.txt_distance);
			holder.txtLastOnline = (TextView) convertView.findViewById(R.id.txt_lastonline);
			holder.chkCheck = (ImageView)convertView.findViewById(R.id.chk_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		} 
		final Item item = mItems.get(position);
		final Ouser ouser = item.getOuser();
		
		if(mSelectOrToInfo) {
			holder.chkCheck.setVisibility(View.VISIBLE);
		}
		holder.chkCheck.setImageResource(item.isSelect() ? R.drawable.checkbox_check : R.drawable.checkbox_uncheck);
		holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(ouser.getPortrait(),
				Photo.Size.Large));
		holder.txtName.setText(ouser.getNickName());
		holder.txtAge.setText(String.valueOf(ouser.getAge()));
		holder.imageGender.setImageResource(Formatter.getGenderIcon(ouser.getGender()));
		holder.txtDistance.setText(Formatter.formatDistance(ouser.getDistance()));
		holder.txtLastOnline.setText(Formatter.formatMinuteValueToInterval(ouser.getLastOnline()));

		return convertView;
	}

	private static class ViewHolder {
		public ImageView imagePortrait = null;
		public TextView txtName = null;
		public TextView txtAge = null;
		public ImageView imageGender = null;
		public TextView txtDistance = null;
		public TextView txtLastOnline = null;
		public ImageView chkCheck = null;
	}

	protected static class Item {
		private Ouser mOuser = null;
		private boolean mSelect = false;
		
		public Ouser getOuser() {
			return mOuser;
		}
		public void setOuser(Ouser ouser) {
			this.mOuser = ouser;
		}
		public boolean isSelect() {
			return mSelect;
		}
		public void setSelect(boolean select) {
			this.mSelect = select;
		}
	}
}
