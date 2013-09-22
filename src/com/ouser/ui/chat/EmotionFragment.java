package com.ouser.ui.chat;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ouser.R;
import com.ouser.logic.LogicFactory;
import com.ouser.ui.base.BaseFragment;

@SuppressLint("ValidFragment")
class EmotionFragment extends BaseFragment {
	
	public interface OnActionListener {
		void onSend();
		void onBack();
		void onAppend(Bitmap image);
	}
	
	private List<Bitmap> mItems = new ArrayList<Bitmap>();	
	private OnActionListener mActionListener = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frgmt_emotion, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initGridView();
		initActionButton();
	}

	public void setActionListener(OnActionListener value) {
		mActionListener = value;
	}
	
	private void initGridView() {
		mItems = LogicFactory.self().getEmotion().getAllImage();
		GridView gridView = (GridView)getView().findViewById(R.id.gridview_emotion);
		gridView.setAdapter(new GridViewAdapter());
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(mActionListener != null) {
					mActionListener.onAppend(mItems.get(arg2));
				}
			}
		});
	}
	
	private void initActionButton() {
		getView().findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mActionListener != null) {
					mActionListener.onSend();
				}
			}
		});
		getView().findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mActionListener != null) {
					mActionListener.onBack();
				}
			}
		});
	}
	
	private class GridViewAdapter extends ArrayAdapter<Void> {

		public GridViewAdapter() {
			super(getActivity(), 0);
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			if(convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.gvitem_emotion, null);
			}
			((ImageView)convertView.findViewById(R.id.image_emotion)).setImageBitmap(mItems.get(position));
			return convertView;
		}
	}
}
