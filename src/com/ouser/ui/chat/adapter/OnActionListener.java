package com.ouser.ui.chat.adapter;



/**
 * adapter的回调函数
 * 由adapter实现
 * 由各个chatitemview调用
 * @author hanlixin
 *
 */
interface OnActionListener {

	void notifyDataSetChanged();
	ChatItems getItems();
}
