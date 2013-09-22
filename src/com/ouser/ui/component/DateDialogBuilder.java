package com.ouser.ui.component;

import java.util.Calendar;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouser.R;

public class DateDialogBuilder extends ContentDialogBuilder {
	
	private static final int MaxYearCount = 100;
	
	private int endYear = Calendar.getInstance().get(Calendar.YEAR);
	private int startYear = endYear - MaxYearCount;

	public interface Callback {
		void onSelect(Calendar value);
	}

	/** 当前选择的日期 */
	private Calendar mSelect = null;

	public DateDialogBuilder(Activity activiy) {
		super(activiy);
		setResId(R.layout.layout_date_dialog);
	}

	public DateDialogBuilder setSelect(Calendar value) {
		mSelect = value;
		return this;
	}

	public DateDialogBuilder setCallback(final Callback value) {
		setCallback(new ContentDialogBuilder.Callback() {

			@Override
			public void onPrepare(View view) {
				final WheelView wheelYear = (WheelView) view.findViewById(R.id.wheel_year);
				final WheelView wheelMonth = (WheelView) view.findViewById(R.id.wheel_month);
				final WheelView wheelDay = (WheelView) view.findViewById(R.id.wheel_day);

				OnWheelChangedListener yearListener = new OnWheelChangedListener() {
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
						updateMonth(wheelYear, wheelMonth);
						updateDays(wheelYear, wheelMonth, wheelDay);
					}
				};
				OnWheelChangedListener monthListener = new OnWheelChangedListener() {
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
						updateDays(wheelYear, wheelMonth, wheelDay);
					}
				};
				
				boolean first = false;
				if(mSelect == null) {
					mSelect = Calendar.getInstance();
					first = true;
				}

				// year
				int curYear = mSelect.get(Calendar.YEAR);
				wheelYear.setViewAdapter(new DateNumericAdapter(mActivity, startYear, endYear, -1));
				if(first) {
					wheelYear.setCurrentItem(82); // 向前18年
				} else {
					wheelYear.setCurrentItem(curYear - startYear);
				}
				wheelYear.addChangingListener(yearListener);

				// month
				updateMonth(wheelYear, wheelMonth);
				wheelMonth.setCurrentItem(mSelect.get(Calendar.MONTH));
				wheelMonth.addChangingListener(monthListener);

				// day
				updateDays(wheelYear, wheelMonth, wheelDay);
				wheelDay.setCurrentItem(mSelect.get(Calendar.DAY_OF_MONTH) - 1);
			}

			@Override
			public boolean onOK(View view) {

				final WheelView wheelYear = (WheelView) view.findViewById(R.id.wheel_year);
				final WheelView wheelMonth = (WheelView) view.findViewById(R.id.wheel_month);
				final WheelView wheelDay = (WheelView) view.findViewById(R.id.wheel_day);

				int year = startYear + wheelYear.getCurrentItem();
				int month = 1 + wheelMonth.getCurrentItem() - 1;
				int day = 1 + wheelDay.getCurrentItem();

				Calendar select = Calendar.getInstance();
				select.set(year, month, day);
				value.onSelect(select);
				return true;
			}
		});
		return this;
	}
	
	private void updateMonth(WheelView year, WheelView month) {
		int maxMonth = 12;
		if(year.getCurrentItem() == MaxYearCount) {
			maxMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		}
		month.setViewAdapter(new DateNumericAdapter(mActivity, 1, maxMonth, -1));
		int curMonth = Math.min(maxMonth, month.getCurrentItem() + 1);
		month.setCurrentItem(curMonth - 1);
	}

	/**
	 * Updates day wheel. Sets max days according to selected month and year
	 */
	private void updateDays(WheelView year, WheelView month, WheelView day) {
		Calendar calendar = Calendar.getInstance();
		if(year.getCurrentItem() == MaxYearCount && 
				month.getCurrentItem() == calendar.get(Calendar.MONTH)) {
			int today = calendar.get(Calendar.DAY_OF_MONTH);
			day.setViewAdapter(new DateNumericAdapter(mActivity, 1, today, -1));
			day.setCurrentItem(today - 1);
		} else {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
			calendar.set(Calendar.MONTH, month.getCurrentItem());
	
			int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			day.setViewAdapter(new DateNumericAdapter(mActivity, 1, maxDays, -1));
			int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
			day.setCurrentItem(curDay - 1);
		}
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
}
