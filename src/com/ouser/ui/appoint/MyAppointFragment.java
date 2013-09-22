package com.ouser.ui.appoint;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.util.Const;

/**
 * 我的友约
 * @author hanlixin
 * @remark 如果使用viewpage，则setadapter必须使用异步加入，否则会报异常
 * java.lang.IllegalStateException: Recursive entry to executePendingTransactio
 * 当然，还会有其他一些异常
 */
@SuppressLint("HandlerLeak")
public class MyAppointFragment extends TopFragment {
	
	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new MyAppointFragment();
		}
	}
	
	private List<BaseListFragment> mFragments = new ArrayList<BaseListFragment>();
	private int mCurrentIndex = -1;
	
	private TextView[] mTabTitles = null;
	private View[] mTabUnderlines = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_my_appoint, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getHeadbar().setTitle("我的友约");
		getHeadbar().setActionButton(R.drawable.title_publish, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				ActivitySwitch.toPublishAppointForResult(getActivity());
			}
		});

		mTabTitles = new TextView[]{
				(TextView)getView().findViewById(R.id.txt_publish), 
				(TextView)getView().findViewById(R.id.txt_join),
		};
		mTabUnderlines = new View[]{
				getView().findViewById(R.id.view_publish_underline),
				getView().findViewById(R.id.view_join_underline),
		};
		
		String myUid = Cache.self().getMyUid();
		PublishedAppointListFragment publishList = new PublishedAppointListFragment(myUid);
		JoinedAppointListFragment joinList = new JoinedAppointListFragment(myUid);
		mFragments.add(publishList);
		mFragments.add(joinList);
		
		publishList.setFetchOnlyValid(false);

		View.OnClickListener listener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int index = 0;
				for(TextView tabTitle : mTabTitles) {
					if(tabTitle == v) {
						if(mCurrentIndex == index) {
							break;
						}
						switchTab(index);
						break;
					}
					++index;
				}
			}
		};
		
		for(View v : mTabTitles) {
			v.setOnClickListener(listener);
		}
	}

	@Override
	public boolean onFragmentActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == Const.RequestCode.PublishAppoint) {
			if(resultCode == Activity.RESULT_OK) {
				new Handler().post(new Runnable() {
					
					@Override
					public void run() {
						switchTab(0);	
					}
				});
			}
			return true;
		}
		if(requestCode == Const.RequestCode.AppointDetail) {
			// 不判断RESULT_OK，可能转了很远删除了友约或退出友约
			mFragments.get(mCurrentIndex).asyncInitData();
		}
		return super.onFragmentActivityResult(requestCode, resultCode, intent);
	}
	
	@Override
	public void onSaveState() {
		super.onSaveState();
		// 不需要保存数据
		// TODO 还是缓存的好
	}
	
	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);
		switchTab(0);
	}
	
	private void switchTab(int index) {
		if(mCurrentIndex != index) {
			mCurrentIndex = index;
			replaceFragment(R.id.layout_my_appoint_list, mFragments.get(index));
		}
		mFragments.get(index).asyncInitData();
		
		for(TextView tabTitle : mTabTitles) {
			tabTitle.setTextColor(getResources().getColor(R.color.gray_text));
		}
		for(View tabUnderline : mTabUnderlines) {
			tabUnderline.setBackgroundColor(getResources().getColor(R.color.gray_line));
		}
		int mainColor = getResources().getColor(R.color.main);
		mTabTitles[index].setTextColor(mainColor);
		mTabUnderlines[index].setBackgroundColor(mainColor);
	}
}
