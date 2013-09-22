package com.ouser.ui.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoDownloadCompleteEventArgs;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.PhotoDelayedRefresh;
import com.ouser.util.Const;

@SuppressLint("HandlerLeak")
abstract public class BaseListFragment extends BaseFragment {
	
	protected interface OnActionListener {
		void onLoadingComplete();
		void onEditingComplete();
	}

	/** 创建list所属的adapter */
	protected BaseListAdapter createAdapter() {
		return new BaseListAdapter();
	}

	////////////数据相关接口////////////
	/** 重新加载数据 */
	abstract protected void refreshData(boolean append);
	
	/** 获得数据个数 */
	abstract protected int getItemCount();
	
	protected EventListener getMainEventListener() {
		return createUIEventListener(mMainListener);
	}

	////////////行为相关接口////////////
	/**
	 * 处理主要事件
	 * @return 返回的结果是否为空
	 * @remark 如果返回true，则表示“全部加载完成”
	 */
	abstract protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append);

	/** 点击列表项 */
	abstract protected void onClickItem(int index);
	
	////////////界面相关接口////////////
	abstract protected View getItemView(int position, View convertView);
	
	////////////编辑相关接口////////////
	protected String getEditText(int index) {
		return "";
	}
	
	protected void onEdit(int index) {
	}

	/** 当前用户操作时添加还是重置 */
	private boolean mAppend = false;
	
	/** 当前最新数据的页索引 */
	private int mCurrentPageIndex = 0;
	
	/** 是否可以编辑 */
	private boolean mEnableEdit = false;
	
	/** 是否关注头像下载事件 */
	private boolean mObservePhotoDownloadEvent = false;
	
	/** list view */
	protected PullToRefreshListView mListView = null;
	protected BaseListAdapter mAdapter = null;
	
	protected OnActionListener mActionListener = new OnActionListener() {
		
		@Override
		public void onLoadingComplete() {
			// TODO
		}
		
		@Override
		public void onEditingComplete() {
			stopLoading();
			mAdapter.notifyDataSetChanged();
		}
	};
	
	private PhotoDelayedRefresh mRefresh = null;
	private EventListener mPhotoListener = new EventListener() {
		
		@Override
		public void onEvent(EventId id, EventArgs args) {
			PhotoDownloadCompleteEventArgs photoArgs = (PhotoDownloadCompleteEventArgs)args;
			if(photoArgs.isSuccess()) {
				mRefresh.notifyDataSetChanged();
			}
		}
	};
	
	/** 主要事件监听器 */
	private EventListener mMainListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			stopLoading();
			StatusEventArgs statusEventArgs = (StatusEventArgs) args;
			if (statusEventArgs.getErrCode() == OperErrorCode.Success) {
				
				// 设置当前页索引
				if(mAppend) {
					++mCurrentPageIndex;
				} else {
					mCurrentPageIndex = 0;
				}
				
				// 处理数据
				boolean empty = handleMainEvent(statusEventArgs, mAppend);
				
				// 通知控件
				mListView.onRefreshComplete();
				
				if(empty) {
					if(!mAdapter.isAllLoadComplete()) {
						mAdapter.setAllLoadComplete(true);
						mListView.setAdapter(mAdapter);
						enableAppend(false);
					}
				} else {
					if(mAdapter.isAllLoadComplete()) {
						mAdapter.setAllLoadComplete(false);
						mListView.setAdapter(mAdapter);
						enableAppend(true);
					} else {
						mAdapter.notifyDataSetChanged();
					}
				}
			} else {
				Alert.handleErrCode(statusEventArgs.getErrCode());
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview_container, null);
		mListView = (PullToRefreshListView) view.findViewById(R.id.list);
		mListView.setMode(PullToRefreshListView.Mode.BOTH);
		mListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						mAppend = false;
						refreshData(mAppend);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						mAppend = true;
						refreshData(mAppend);
					}
				});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int type = mAdapter.getItemViewType(arg2 - 1);
				if(type == BaseListAdapter.ViewTypeComplete || 
						type == BaseListAdapter.ViewTypeEmpty) {
					return;
				}
				onClickItem(arg2 - 1);
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(!mEnableEdit) {
					return false;
				}
				final int index = arg2 - 1;
				new AlertDialog.Builder(getActivity())
					.setMessage(getEditText(index))
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onEdit(index);
							startLoading();
						}
					})
					.setNegativeButton("取消", null)
					.create().show();
				return true;
			}
		});
		
		mAdapter = createAdapter();
		mListView.setAdapter(mAdapter);
		mRefresh = new PhotoDelayedRefresh(mAdapter);
		return view;
	}

	@Override
	public void syncInitData(Bundle bundle) {
		mAppend = false;
		refreshData(mAppend);
		addEventListener();
		startLoading();
	}
	
	public void notfiyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}
	
	public void observePhotoDownloadEvent(boolean value) {
		mObservePhotoDownloadEvent = value;
	}
	
	public void enableEdit(boolean value) {
		mEnableEdit = value;
	}
	
	public void enableRefresh(boolean value) {
		if(value) {
			addListMode(PullToRefreshListView.Mode.PULL_FROM_START);
		} else {
			removeListMode(PullToRefreshListView.Mode.PULL_FROM_START);
		}
	}
	
	public void enableAppend(boolean value) {
		if(value) {
			addListMode(PullToRefreshListView.Mode.PULL_FROM_END);
		} else {
			removeListMode(PullToRefreshListView.Mode.PULL_FROM_END);
		}
	}

	public int getCurrentPageIndex() {
		return mCurrentPageIndex;
	}
	
	private void addListMode(PullToRefreshListView.Mode value) {
		switch(mListView.getMode()) {
		case BOTH:
			break;
		case PULL_FROM_START:
			if(value == PullToRefreshListView.Mode.PULL_FROM_END) {
				mListView.setMode(PullToRefreshListView.Mode.BOTH);
			}
			break;
		case PULL_FROM_END:
			if(value == PullToRefreshListView.Mode.PULL_FROM_START) {
				mListView.setMode(PullToRefreshListView.Mode.BOTH);
			}
			break;
		default:
			mListView.setMode(value);
			break;
		}
	}
	
	private void removeListMode(PullToRefreshListView.Mode value) {
		switch(mListView.getMode()) {
		case BOTH:
			if(value == PullToRefreshListView.Mode.PULL_FROM_START) {
				mListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
			} else {
				mListView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
			}
			break;
		case PULL_FROM_START:
			if(value == PullToRefreshListView.Mode.PULL_FROM_START) {
				mListView.setMode(PullToRefreshListView.Mode.DISABLED);
			}
			break;
		case PULL_FROM_END:
			if(value == PullToRefreshListView.Mode.PULL_FROM_END) {
				mListView.setMode(PullToRefreshListView.Mode.DISABLED);
			}
			break;
		default:
			break;
		}
	}

	/** 添加事件关注 */
	private void addEventListener() {
		if(mObservePhotoDownloadEvent) {
			addUIEventListener(EventId.ePhotoDownloadComplete, mPhotoListener);
		}
	}

	public class BaseListAdapter extends BaseAdapter {
		
		public static final int ViewTypeData = 0;
		public static final int ViewTypeComplete = 1;
		
		// for bottom divider
		public static final int ViewTypeEmpty = 2;
		
		private boolean mAllLoadComplete = false;
		
		public boolean isAllLoadComplete() {
			return mAllLoadComplete;
		}
		
		public void setAllLoadComplete(boolean value) {
			mAllLoadComplete = value;
		}

		@Override
		public int getItemViewType(int position) {
			if(mAllLoadComplete && position == getCount() - 1) {
				return ViewTypeComplete;
			} else if(position == getCount() - 1) {
				return ViewTypeEmpty;
			} else {
				return ViewTypeData;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getCount() {
			return getItemCount() + 1;
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
			if(mAllLoadComplete && position == getCount() - 1) {
				if(convertView == null) {
					convertView = LayoutInflater.from(Const.Application).inflate(R.layout.lvitem_load_complete, null);
				}
				return convertView;
			} else if(position == getCount() - 1) {
				if(convertView == null) {
					convertView = new View(Const.Application);
				}
				return convertView;
			} else {
				return getItemView(position, convertView);
			}
		}
	}
}
