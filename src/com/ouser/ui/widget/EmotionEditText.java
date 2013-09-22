package com.ouser.ui.widget;

import java.util.regex.Matcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ouser.logic.LogicFactory;

public class EmotionEditText extends EditText {

	public EmotionEditText(Context context) {
		super(context);
	}

	public EmotionEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void appendText(String text) {
		append(text);
	}

	// 在编辑框内添加图片或者表情
	public void appendImage(String text) {

		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = LogicFactory.self().getEmotion().getPattern().matcher(text);
		
		while(matcher.find()) {
			Bitmap image = LogicFactory.self().getEmotion().getImage(matcher.group());
			if(image == null) {
				continue;
			}
			Drawable drawable = new BitmapDrawable(getResources(), image);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			builder.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		append(builder);
	}
}
