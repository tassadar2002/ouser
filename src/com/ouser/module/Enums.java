package com.ouser.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.SparseArray;

import com.ouser.R;
import com.ouser.util.Const;

public class Enums {

	public enum Type {
		Height,
		Body,
		Merry,
		Education,
		Cost,
		Period,
		None,
	}

	private static Enums ins = new Enums();
	public static Enums self() {
		return ins;
	}

	private List<KeyValueList> mItems;
	
	private Enums() {
		initData();
	}
	
	public SparseArray<String> getList(Type type) {
		for(KeyValueList list : mItems) {
			if(type == list.mType) {
				return list.mItems;
			}
		}
		return null;
	}
	
	public int getDefaultValue(Type type) {
		for(KeyValueList list : mItems) {
			if(type == list.mType) {
				return list.mDefaultValue;
			}
		}
		return 0;
	}
	
	public String getText(Type type, int key) {
		SparseArray<String> list = getList(type);
		if(list == null) {
			return "";
		}
		return list.get(key);
	}
	
	public String getTitle(Type type) {
		initData();
		for(KeyValueList list : mItems) {
			if(type == list.mType) {
				return list.mTitle;
			}
		}
		return "";
	}
	
	private void initData() {
		mItems = new ArrayList<KeyValueList>();
		
		mItems.add(new KeyValueList(Type.Height, "height"));
		mItems.add(new KeyValueList(Type.Body, "body"));
		mItems.add(new KeyValueList(Type.Merry, "merry"));
		mItems.add(new KeyValueList(Type.Education, "education"));
		mItems.add(new KeyValueList(Type.Cost, "cost"));
		mItems.add(new KeyValueList(Type.Period, "validday"));
		
		KeyValueList curList = null;
		int curKey = 0;
		XmlResourceParser parser = Const.Application.getResources().getXml(R.xml.enums);
		try {
			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
	            if (parser.getEventType() == XmlResourceParser.START_TAG) {
	            	String name = parser.getName();

	                if (name.equalsIgnoreCase("item")) {
	                	curKey = parser.getAttributeIntValue(0, 0);
		            	if(parser.getAttributeBooleanValue(1, false)) {
		            		curList.mDefaultValue = curKey;
	        			}
	                } else {
		            	for(KeyValueList list : mItems) {
		            		if(name.equalsIgnoreCase(list.mName)) {
		            			curList = list;
		            			list.mTitle = parser.getAttributeValue(0);
		            			break;
		            		}
		            	}
	                }
	            } else if (parser.getEventType() == XmlResourceParser.TEXT) {
	            	curList.mItems.put(curKey, parser.getText());
	            }
	            parser.next();
	        }
		} catch(XmlPullParserException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static class KeyValueList {
		private SparseArray<String> mItems = new SparseArray<String>();
		private String mTitle = "";
		private String mName = "";
		private Type mType = Type.None;
		private int mDefaultValue = 0;
		
		private KeyValueList(Type type, String name) {
			mType = type;
			mName = name;
		}
	}
}
