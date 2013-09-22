package com.ouser.ui.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.helper.Alert;
import com.ouser.util.ListUtil;

public class DateTimeDialogBuilder extends ContentDialogBuilder {
	
	public interface Callback {
		void onSelect(Calendar value);
	}
	
	/** 当前选择的日期 */
	private Calendar mSelect = null;
	
	/** 控件下标对应的日期对象 */
	private List<Calendar> mDateItems = new ArrayList<Calendar>();
	
	public DateTimeDialogBuilder(Activity activiy) {
		super(activiy);
		setResId(R.layout.layout_datetime_dialog);
	}
	
	public DateTimeDialogBuilder setSelect(Calendar value) {
		mSelect = value;
		return this;
	}
	
	public DateTimeDialogBuilder setCallback(final Callback value) {
		setCallback(new ContentDialogBuilder.Callback() {
			
			@Override
			public void onPrepare(View view) {
				if(mSelect == null) {
					mSelect = Calendar.getInstance();
				}
				
				// date
				Calendar mNowDate = Calendar.getInstance();
				Calendar mLastDate = Calendar.getInstance();
				mLastDate.add(Calendar.MONTH, 3);
				
				int selectIndex = -1;
				List<String> dateItems = new ArrayList<String>();
				for(int i = 0; mNowDate.before(mLastDate); ++i) {
					String key = formatDate(mNowDate);
					dateItems.add(key);
					
					Calendar thisDate = Calendar.getInstance();
					thisDate.add(Calendar.DATE, i);
					mDateItems.add(thisDate);
					if(dateEqual(mNowDate, mSelect)) {
						selectIndex = i;
					}
					mNowDate.add(Calendar.DATE, 1);
				}
				if(selectIndex == -1) {
					selectIndex = 0;
				}
				WheelView wheelDate = (WheelView)view.findViewById(R.id.wheel_date);
				wheelDate.setViewAdapter(new DateArrayAdapter(mActivity, ListUtil.toArray(dateItems), -1));
				wheelDate.setCurrentItem(selectIndex);
				
				// hour
		        int hour = mSelect.get(Calendar.HOUR_OF_DAY);
		        WheelView wheelHour = (WheelView)view.findViewById(R.id.wheel_hour);
		        wheelHour.setViewAdapter(new DateNumericAdapter(mActivity, 0, 23, -1));
		        wheelHour.setCurrentItem(hour);
		        
		        // minute
		        int minute = mSelect.get(Calendar.MINUTE);
		        List<String> minuteItems = new ArrayList<String>();
		        for(int i = 0; i < 60; ++i) {
		        	if(i % 5 == 0) {
		        		minuteItems.add(String.valueOf(i));
		        	}
		        }
		        WheelView wheelMinute = (WheelView)view.findViewById(R.id.wheel_minute);
		        wheelMinute.setViewAdapter(new DateArrayAdapter(mActivity, ListUtil.toArray(minuteItems), -1));
		        wheelMinute.setCurrentItem(minute / 5);		        
			}

			@Override
			public boolean onOK(View view) {
				
				WheelView wheelDate = (WheelView)view.findViewById(R.id.wheel_date);
				WheelView wheelHour = (WheelView)view.findViewById(R.id.wheel_hour);
				WheelView wheelMinute = (WheelView)view.findViewById(R.id.wheel_minute);
				
				int index = wheelDate.getCurrentItem();
				Calendar select = mDateItems.get(index);
				select.set(Calendar.HOUR_OF_DAY, wheelHour.getCurrentItem());
				select.set(Calendar.MINUTE, wheelMinute.getCurrentItem() * 5);
				
				if(select.before(Calendar.getInstance())) {
					Alert.Toast("友约时间需大于当前时间", false, true);
					return false;
				}
				
				value.onSelect(select);
				return true;
			}
		});
		return this;
	}

	private String formatDate(Calendar value) {
		if(dateEqual(value, Calendar.getInstance())) {
			return "今天";
		}
		return String.format("%s月%s日", 
				value.get(Calendar.MONTH) + 1, value.get(Calendar.DAY_OF_MONTH));
	}
	
	private boolean dateEqual(Calendar left, Calendar right) {
		return left.get(Calendar.YEAR) == right.get(Calendar.YEAR) && 
				left.get(Calendar.DAY_OF_YEAR) == right.get(Calendar.DAY_OF_YEAR);
	}
	
	  
    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(20);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
	
    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(20);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
}
