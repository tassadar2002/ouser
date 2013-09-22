package com.ouser.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;

import com.ouser.R;

public class AddressCode {

	private static List<Item> mItems = new ArrayList<Item>();
	
	public static String getDesc(String code) {
		init();
		
		if(StringUtil.isEmpty(code)) {
			return "";
		}
		
		Item item = null;
		for(Item i : mItems) {
			if(i.code.equals(code)) {
				item = i;
				break;
			}
		}
		if(item == null) {
			return "";
		}
		
		ArrayList<String> strs = new ArrayList<String>();
		while(item != null) {
			if(!"市辖区".equals(item.name) && !"县".equals(item.name)) {
				strs.add(item.name);
			}
			item = item.parent;
		}

		StringBuilder sb = new StringBuilder();
		for(int i = strs.size() - 1; i >= 0; --i) {
			sb.append(strs.get(i));
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private static void init() {
		if(!mItems.isEmpty()) {
			return;
		}
		
		Item curProvince = null;
    	Item curCity = null;
		
		XmlResourceParser parser = Const.Application.getResources().getXml(R.xml.addresscode);
		try {
			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
	            if (parser.getEventType() == XmlResourceParser.START_TAG) {
	            	String tagName = parser.getName();
	            	
	            	if(tagName.equals("province") || 
	            			tagName.equals("city") || 
	            			tagName.equals("district")) {
		            	Item item = new Item();
	                	item.name = parser.getAttributeValue(0);
	                	item.code = parser.getAttributeValue(1);
	                	mItems.add(item);
	                	
		                if (tagName.equalsIgnoreCase("province")) {
		                	curProvince = item;
		                } else if (tagName.equalsIgnoreCase("city")) {
		                	curCity = item;
		                	item.parent = curProvince;
		                } else if(tagName.equalsIgnoreCase("district")) {
		                	item.parent = curCity;
		                }
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
	
	private static class Item {
		public String name;
		public String code;
		public Item parent = null;
	}
}
