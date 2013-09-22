package com.ouser.ui.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ouser.util.StringUtil;

/**
 * 输入友约标签的文本框
 * 
 * @author hanlixin
 * @remark 不能setText为空
 */
public class AppointEditText extends EditText {

	public AppointEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		setText("#");
		setSelection(1);

		setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (AppointEditText.this.getText().toString().equals("#")) {
						return true;
					}
				}
				return false;
			}
		});
		setFilters(new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
					int dstart, int dend) {
				if (source instanceof Spanned) {
					// 正在拼音状态下的汉字
					return null;
				}
				String txt = source.toString();
				Pattern p = Pattern.compile("[#0-9a-zA-Z\u4e00-\u9fa5]*");
				Matcher m = p.matcher(txt);
				if (m.matches()) {
					return null;
				}
				String curText = getText().toString();
				if(curText.length() > 0) {
					if(curText.toCharArray()[curText.length() - 1] == '#') {
						return "";
					}
				}
				return "#";
			}
		} });
	}

	public void append(String text) {
		String cur = getText().toString();
		if (!StringUtil.isEmpty(cur) && cur.toCharArray()[cur.length() - 1] == '#') {
			super.setText("#" + text);
		} else {
			super.setText(formatText(getText() + "#" + text));
		}
	}

	public void setText(String value) {
		super.setText(formatText(value));
	}

	public boolean isEmptyText() {
		char[] text = getText().toString().toCharArray();
		for (int i = 0; i < text.length; ++i) {
			if (text[i] != '#') {
				return false;
			}
		}
		return true;
	}

	private String formatText(String text) {
		if (text == null || text.length() == 0) {
			return "#";
		}
		return text;
	}
}
