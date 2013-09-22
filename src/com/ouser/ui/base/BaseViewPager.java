package com.ouser.ui.base;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class BaseViewPager {
	
	public interface OnActionListener {
		
		void onPageChanged();
		int getPageCount();
		Fragment createFragment(int index);
	}
	
	/** 当前的下标 */
	private int mCurrentIndex = 0;
	
	private ViewPager mViewPager = null;
	private Adapter mAdapter = null;
	
	private OnActionListener mActionListener = null;

	/**
	 * 初始化
	 * @param activtiy
	 * @param viewPager viewpager实例
	 * @param listener 监听器
	 */
	public void init(FragmentActivity activtiy, ViewPager viewPager, OnActionListener listener) {
		
		mActionListener = listener;
		mAdapter = new Adapter(activtiy.getSupportFragmentManager());
		
		mViewPager = viewPager;
		final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mCurrentIndex = arg0;
				mActionListener.onPageChanged();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		};

		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		mViewPager.setCurrentItem(mCurrentIndex);
		if (mCurrentIndex == 0) {
			mActionListener.onPageChanged();
		}
	}
	
	/**
	 * 获得当前页面下标
	 * @return
	 */
	public int getCurrentIndex() {
		return mCurrentIndex;
	}
	
	/**
	 * 设置当前页面下标
	 * @param value
	 */
	public void setCurrentIndex(int value) {
		mCurrentIndex = value;
	}

	public Fragment getFragment(int index) {
		return mAdapter.getItem(index);
	}
	
	public void toNext() {
		int i = mViewPager.getCurrentItem();
		if (i == mActionListener.getPageCount() - 1) {
			return;
		}
		mViewPager.setCurrentItem(++i);
	}
	
	public void toLast() {
		int i = mViewPager.getCurrentItem();
		if (i == 0) {
			return;
		}
		mViewPager.setCurrentItem(--i);
	}
	
	public void removeCurrent() {
		mAdapter.removeFragment(mCurrentIndex);
		
		// fix bug 删除一个后，左右还有残留(notifyDatasetChanged不管用)
		mViewPager.setAdapter(mAdapter);
		
		if(mCurrentIndex == mActionListener.getPageCount()) {
			mCurrentIndex -= 1;
		}
		
		// 重新设置了adapter，所以一定要重新set item
		mViewPager.setCurrentItem(mCurrentIndex);
		
		// 首页的会触发onpagechange事件
		if(mCurrentIndex == 0) {
			mActionListener.onPageChanged();
		}
	}

	/**
	 * 适配器
	 * @author hanlixin
	 *
	 */
	private class Adapter extends FragmentStatePagerAdapter {

		private List<Element> mFragments = new ArrayList<Element>();

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Element element = find(arg0);
			if (element == null) {
				element = new Element();
				element.index = arg0;
				element.fragment = mActionListener.createFragment(arg0);
				mFragments.add(element);
			}
			return element.fragment;
		}

		@Override
		public int getCount() {
			return mActionListener.getPageCount();
		}
		
		public void removeFragment(int index) {
			mFragments.remove(find(index));
			
			for(Element element : mFragments) {
				if(element.index > index) {
					element.index -= 1;
				}
			}
		}
		
		private Element find(int index) {
			for(Element element : mFragments) {
				if(element.index == index) {
					return element;
				}
			}
			return null;
		}
		
		private class Element {
			public Fragment fragment = null;
			public int index = 0;
		}
	};
}
