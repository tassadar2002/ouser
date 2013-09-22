package com.ouser.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.ouser.R;
import com.ouser.util.Const;

public class EmotionLogic extends BaseLogic {
	
	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new EmotionLogic();
		}
	}
	
	EmotionLogic() {
	}

	private List<Pair<String, Bitmap>> mItems = new ArrayList<Pair<String, Bitmap>>();
	
	private Pattern mPattern = null;
	
	public Bitmap getImage(String text) {
		init();
		
		for(Pair<String, Bitmap> item : mItems) {
			if(item.first.equals(text)) {
				return item.second;
			}
		}
		return null;
	}
	
	public String getText(Bitmap image) {
		init();
		
		for(Pair<String, Bitmap> item : mItems) {
			if(item.second.equals(image)) {
				return item.first;
			}
		}
		return "";
	}
	
	public List<Bitmap> getAllImage() {
		init();
		
		List<Bitmap> result = new ArrayList<Bitmap>();
		for(Pair<String, Bitmap> item : mItems) {
			result.add(item.second);
		}
		return result;
	}

	public Pattern getPattern() {
		init();
		
		if(mPattern == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for(Pair<String, Bitmap> item : mItems) {
				sb.append(Pattern.quote(item.first)).append("|");
			}
			sb.replace(sb.length() - 1, sb.length(), ")");
			mPattern = Pattern.compile(sb.toString());
		}
		return mPattern;
	}
	
	private void init() {
		if(!mItems.isEmpty()) {
			return;
		}
		
		AssetManager assetMgr = Const.Application.getAssets();
		XmlResourceParser parser = Const.Application.getResources().getXml(R.xml.emotion);
		try {
			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
	            if (parser.getEventType() == XmlResourceParser.START_TAG) {
	            	String tagName = parser.getName();
	                if (tagName.equalsIgnoreCase("item")) {
	                	String name = parser.getAttributeValue(0);
	                	String file = parser.getAttributeValue(1);
	                	
	    				Bitmap bitmap = BitmapFactory.decodeStream(assetMgr.open("emotion/" + file));
	    				mItems.add(new Pair<String, Bitmap>(name, bitmap));
	                }
	            }
	            parser.next();
	        }
		} catch(XmlPullParserException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
