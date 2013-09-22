package com.ouser.ui.appoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.js.cloudtags.KeywordsFlow;
import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StringListEventArgs;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.helper.Alert;

/**
 * 友约热词
 * @author hanlixin
 *
 */
public class CloudTagFragment extends BaseFragment {
	
	public interface OnSelectTagListener {
		void onSelect(String tag);
	}
	
	/** 选择tag后的监听器 */
	private OnSelectTagListener mListener = null;
	
	public void setOnSelectListener(OnSelectTagListener listener) {
		mListener = listener;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frgmt_cloudtag, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final KeywordsFlow keywordsFlow = (KeywordsFlow) getView().findViewById(R.id.keywordsflow);
        keywordsFlow.setDuration(800l);  
        keywordsFlow.setOnItemClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null) {
					mListener.onSelect(((TextView)v).getText().toString());
				}
			}
		});
	}

	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);

		final KeywordsFlow keywordsFlow = (KeywordsFlow) getView().findViewById(R.id.keywordsflow);
        LogicFactory.self().getAppoint().getCloudTag(
        		createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				keywordsFlow.rubKeywords();
				StringListEventArgs stringsArgs = (StringListEventArgs)args;
				if(stringsArgs.getErrCode() == OperErrorCode.Success) {
					for(String string : stringsArgs.getStrings()) {
						keywordsFlow.feedKeyword(string);
					}
			        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				} else {
					Alert.handleErrCode(stringsArgs.getErrCode());
				}
			}
		}));
        startLoading();
	}
}
