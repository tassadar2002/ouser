package com.ouser.ui.ouser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoDownloadCompleteEventArgs;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.module.Enums;
import com.ouser.module.Gender;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Formatter;
import com.ouser.util.Const;
import com.ouser.util.StringUtil;

@SuppressLint("ValidFragment")
public class OuserInfoFragment extends BaseFragment {
	
	public interface OnActionListener {
		void onInitActionButton(TextView button);
	}

	private Ouser mOuser = null;

	private OnActionListener mActionListener = null;
	private EventListener mListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			PhotoDownloadCompleteEventArgs photoArgs = (PhotoDownloadCompleteEventArgs) args;
			if (photoArgs.getPhoto().isSame(mOuser.getPortrait())) {
				if (photoArgs.isSuccess()) {
					fillPortairt(photoArgs.getPhoto());
				}
			}
		}
	};

	public OuserInfoFragment(Ouser ouser) {
		mOuser = ouser;
	}
	
	public void setActionListener(OnActionListener value) {
		mActionListener = value;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frgmt_ouser_info, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getView().setOnClickListener(
				ActivitySwitch.getToProfileClickListener(getActivity(), mOuser.getUid()));
		
		if(mActionListener != null) {
			mActionListener.onInitActionButton(
					(TextView)getView().findViewById(R.id.btn_ouser_action));
		}
	}

	@Override
	public void syncInitData(Bundle bundle) {
		addUIEventListener(EventId.ePhotoDownloadComplete, mListener);

		LogicFactory.self().getProfile().get(mOuser.getUid(), 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopMyselfLoading();
				
				OuserEventArgs ouserArgs = (OuserEventArgs) args;
				if (ouserArgs.getErrCode() == OperErrorCode.Success) {
					mOuser = ouserArgs.getOuser();
				}
				fillInfo();
			}
		}));
		startMyselfLoading();
	}

	private void startMyselfLoading() {
		getView().findViewById(R.id.progress_ouser_loading).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.layout_ouser_info).setVisibility(View.GONE);		
	}
	
	private void stopMyselfLoading() {
		getView().findViewById(R.id.progress_ouser_loading).setVisibility(View.GONE);
		getView().findViewById(R.id.layout_ouser_info).setVisibility(View.VISIBLE);		
	}

	private void fillInfo() {
		if(!StringUtil.isEmpty(mOuser.getNickName())) {
			TextView txtName = (TextView) getView().findViewById(R.id.txt_ouser_name);
			txtName.setText(mOuser.getNickName());
		}

		if(mOuser.getAge() != Const.DefaultValue.Age) {
			TextView txtAge = (TextView) getView().findViewById(R.id.txt_age);
			txtAge.setText(String.valueOf(mOuser.getAge()));
		}

		if(mOuser.getGender() != Gender.None) {
			ImageView imageGender = (ImageView) getView().findViewById(R.id.image_gender);
			imageGender.setImageResource(Formatter.getGenderIcon(mOuser.getGender()));
		}

		if(mOuser.getMerry() != Enums.self().getDefaultValue(Enums.Type.Merry)) {
			TextView txtMerry = (TextView) getView().findViewById(R.id.txt_ouser_merry);
			txtMerry.setText(Enums.self().getText(Enums.Type.Merry, mOuser.getMerry()));
		}

		if(!"".equals(mOuser.getStar())) {
			TextView txtStar = (TextView) getView().findViewById(R.id.txt_ouser_star);
			txtStar.setText(mOuser.getStar());
		}

		if(!mOuser.getPortrait().isEmpty()) {
			fillPortairt(mOuser.getPortrait());
		}
	}

	private void fillPortairt(Photo photo) {
		ImageView imagePortrait = (ImageView) getView().findViewById(R.id.image_ouser_portrait);
		imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(photo, Photo.Size.Small));
	}
}
