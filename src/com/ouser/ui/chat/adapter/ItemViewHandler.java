package com.ouser.ui.chat.adapter;

import com.ouser.ui.base.BaseActivity;

import android.view.View;


/**
 * 聊天项视图处理器 
 * 成员变量只能是全局性质的，所以不能保存和单条消息相关的
 */
interface ItemViewHandler {

	/**
	 * 生命周期控制
	 */
	void onDestroy();
	
	/**
	 * 设置宿主
	 * @param value
	 */
	void setActivity(BaseActivity value);
	
	/**
	 * 设置回调
	 * @param value
	 */
	void setOnActionListener(OnActionListener value);
	
	/**
	 * 设置是否为正在编辑状态
	 * @param value
	 */
	void setEditing(boolean value);
	
	/**
	 * 点击列表项
	 * @param index
	 */
	void onClickItem(ChatItem item);

	/**
	 * 获得视图类型
	 * @param item
	 * @return
	 */
	int getItemViewType(ChatItem item);
	
	/**
	 * 创建视图，并填充ViewHolder
	 * @param item
	 * @param holder
	 * @param root
	 */
	void createView(ChatItem item, ViewHolder holder, View root);
	
	/**
	 * 设置视图的内容
	 * @param item
	 */
	void setContent(ChatItem item, ViewHolder holder);
}
