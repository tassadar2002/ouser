package com.ouser.ui.component;

import android.app.Activity;
import android.app.Dialog;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ouser.R;

public class EnumDialogBuilder extends DialogBuilder {

	public interface Callback {
		void onSelect(int key);
	}

	private String mTitle = "";
	private SparseArray<String> mItems = null;
	private int mSelecteted = 0;
	private Adapter mAdapter = null;
	private Callback mCallback = null;

	public EnumDialogBuilder(Activity activity) {
		super(activity);
	}

	public Dialog create() {
		final Dialog dialog = super.create();

		View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_enum_dialog, null);
		dialog.setContentView(view);
		((TextView) view.findViewById(R.id.txt_title)).setText(mTitle);

		mAdapter = new Adapter();
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (mCallback != null) {
					mCallback.onSelect(mItems.keyAt(arg2));
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

	public EnumDialogBuilder setEnum(String title, SparseArray<String> items, int selected,
			Callback callback) {
		mTitle = title;
		mItems = items;
		mSelecteted = selected;
		mCallback = callback;
		return this;
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
			int key = position;
			String text = mItems.get(key);

			convertView = LayoutInflater.from(mActivity).inflate(R.layout.lvitem_enum_dialog, null);
			TextView txtText = (TextView) convertView.findViewById(R.id.txt_text);
			txtText.setText(text);
			if (mSelecteted != key) {
				txtText.setCompoundDrawables(null, null, null, null);
			}
			return convertView;
		}
	}
}
