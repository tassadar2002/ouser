package com.ouser.ui.ouser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.logic.LogicFactory;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.HideKeyboard;

public class SearchOuserActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_search_ouser);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("搜索藕丝");

		findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search();
			}
		});
		
		HideKeyboard.setupUI(this);
	}

	private void search() {
		
		EditText editKeyword = (EditText)findViewById(R.id.edit_keyword);
		
		SearchListFragment fragment = (SearchListFragment)getSupportFragmentManager().findFragmentById(R.id.layout_search_ouser_list);
		if(fragment == null) {
			fragment = new SearchListFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.layout_search_ouser_list, fragment).commit();
		} else {
			fragment.clear();
		}
		
		
		fragment.search(editKeyword.getText().toString());
	}
	
	@SuppressLint("ValidFragment")
	private static class SearchListFragment extends RelationListFragment {
		
		private String mKeyword = "";
		
		public void search(String value) {
			mKeyword = value;
			asyncInitData();
		}
		
		public void clear() {
			mItems.clear();
			mAdapter.notifyDataSetChanged();
		}

		public SearchListFragment() {
			super(Cache.self().getMyUid());
		}

		@Override
		protected void refreshData(boolean append) {
			LogicFactory.self().getOuser().search(mKeyword, append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
		}

		@Override
		public View getItemView(int position, View convertView) {
			View v = super.getItemView(position, convertView);
			HideKeyboard.setupUI(getActivity(), v);
			return v;
		}
	}
}
