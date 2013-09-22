package com.ouser.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ouser.R;

public class ClickableTextView extends TextView {

	public static interface OnClickListener {
		void onClick(String text);
	}

	private List<Element> mItems = new ArrayList<Element>();

	public ClickableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void clearText() {
		mItems.clear();
		setText("");
	}

	public ClickableTextView appendText(String text) {
		return appendText(text, null);
	}

	public ClickableTextView appendText(String text, OnClickListener listener) {
		mItems.add(new Element(text, listener));
		return this;
	}

	public void complete() {
		StringBuilder sb = new StringBuilder();
		for (Element element : mItems) {
			sb.append(element.text);
		}

		SpannableStringBuilder style = new SpannableStringBuilder(sb.toString());

		int index = 0;
		for (Element element : mItems) {
			if (element.listener != null) {
				int start = index;
				int end = start + element.text.length();
				style.setSpan(new MySpan(element), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				style.setSpan(
						new ForegroundColorSpan(getContext().getResources().getColor(R.color.main)), 
						start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			index += element.text.length();
		}
		setText(style);
		setMovementMethod(LinkMovementMethod.getInstance());
	}

	private static class MySpan extends ClickableSpan {

		private Element mElement = null;

		public MySpan(Element element) {
			mElement = element;
		}

		@Override
		public void onClick(View widget) {
			if (mElement.listener != null) {
				mElement.listener.onClick(mElement.text);
			}
		}
	}

	private static class Element {
		public Element(String text, OnClickListener listener) {
			this.text = text;
			this.listener = listener;
		}

		public String text;
		public OnClickListener listener = null;
	}
}
