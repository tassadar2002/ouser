package com.ouser.ui.component;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ouser.R;

public class MenuDialogBuilder extends DialogBuilder {

	public interface Callback {
		void onClick(int key);
	}
	
	private List<Pair<Integer, String>> mItems = null;
	private Adapter mAdapter = null;
	private Callback mCallback = null;
	
	public MenuDialogBuilder(Activity activity) {
		super(activity);
	}

	public MenuDialogBuilder setMenuItem(List<Pair<Integer, String>> items, Callback callback) {
		mItems = items;
		mCallback = callback;
		return this;
	}

	public Dialog create() {
		final Dialog dialog = super.create();

		View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_menu_dialog, null);
		dialog.setContentView(view);

		mAdapter = new Adapter();
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (mCallback != null) {
					mCallback.onClick(mItems.get(arg2).first);
				}
				dialog.dismiss();
			}
		});

		view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	private class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.lvitem_menu_dialog, null);
			
			String text = mItems.get(position).second;
			((TextView) convertView.findViewById(R.id.txt_text)).setText(text);
			return convertView;
		}
	}
}
