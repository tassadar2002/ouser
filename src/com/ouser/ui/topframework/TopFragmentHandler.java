package com.ouser.ui.topframework;

import android.os.Bundle;

/**
 * top fragment处理器，top activity实现
 * @author hanlixin
 *
 */
public interface TopFragmentHandler {

	/**
	 * 切换top fragment
	 * @param type
	 */
	void change(TopFragmentType type, Bundle args);
}
