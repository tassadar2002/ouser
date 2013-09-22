package com.ouser.ui.topframework;


public interface OnMenuSelectedListener {
	
	/**
	 * 点击菜单界面的头部
	 */
	void onHeaderClick();
	
	/**
	 * 选择菜单
	 * @param fragment 被选择菜单对应的fragment
	 */
	void onSelected(TopFragment fragment);
}
