package com.eventer.app.widget.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eventer.app.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;


/**
 * The CaldroidGridAdapter provides customized view for the dates gridview
 * 
 * @author thomasdao
 * 
 */
@SuppressWarnings({"UnusedDeclaration"})
@SuppressLint({"UseSparseArrays","SetTextI18n"})
public class CaldroidGridAdapter extends BaseAdapter {
	protected ArrayList<DateTime> datetimeList;
	protected int month;
	protected int year;
	protected Context context;
	protected ArrayList<DateTime> disableDates;
	protected ArrayList<DateTime> selectedDates;
    protected HashMap<Integer, List<DateTime>> scheduleDates = new HashMap<>();
    
	// Use internally, to make the search for date faster instead of using
	// indexOf methods on ArrayList
	protected HashMap<DateTime, Integer> disableDatesMap = new HashMap<>();
	protected HashMap<DateTime, Integer> selectedDatesMap = new HashMap<>();
	
	protected DateTime minDateTime;
	protected DateTime maxDateTime;
	protected DateTime today;
	protected int startDayOfWeek;
	protected boolean sixWeeksInCalendar;
	protected Resources resources;

	/**
	 * caldroidData belongs to Caldroid
	 */
	protected HashMap<String, Object> caldroidData;
	/**
	 * extraData belongs to client
	 */
	protected HashMap<String, Object> extraData;

	public void setAdapterDateTime(DateTime dateTime) {
		this.month = dateTime.getMonth();
		this.year = dateTime.getYear();
		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
				startDayOfWeek, sixWeeksInCalendar);
	}

	// GETTERS AND SETTERS
	public ArrayList<DateTime> getDatetimeList() {
		return datetimeList;
	}

	public DateTime getMinDateTime() {
		return minDateTime;
	}

	public void setMinDateTime(DateTime minDateTime) {
		this.minDateTime = minDateTime;
	}

	public DateTime getMaxDateTime() {
		return maxDateTime;
	}

	public void setMaxDateTime(DateTime maxDateTime) {
		this.maxDateTime = maxDateTime;
	}

	public ArrayList<DateTime> getDisableDates() {
		return disableDates;
	}

	public void setDisableDates(ArrayList<DateTime> disableDates) {
		this.disableDates = disableDates;
	}

	public ArrayList<DateTime> getSelectedDates() {
		return selectedDates;
	}

	public void setSelectedDates(ArrayList<DateTime> selectedDates) {
		this.selectedDates = selectedDates;
	}

	public HashMap<String, Object> getCaldroidData() {
		return caldroidData;
	}

	public void setCaldroidData(HashMap<String, Object> caldroidData) {
		this.caldroidData = caldroidData;

		// Reset parameters
		populateFromCaldroidData();
	}

	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	/**
	 * Constructor
	 *
	 */
	public CaldroidGridAdapter(Context context, int month, int year,
			HashMap<String, Object> caldroidData,
			HashMap<String, Object> extraData) {
		super();
		this.month = month;
		this.year = year;
		this.context = context;
		this.caldroidData = caldroidData;
		this.extraData = extraData;
		this.resources = context.getResources();

		// Get data from caldroidData
		populateFromCaldroidData();
	}

	/**
	 * Retrieve internal parameters from caldroid data
	 */
	@SuppressWarnings("unchecked")
	private void populateFromCaldroidData() {
		disableDates = (ArrayList<DateTime>) caldroidData
				.get(CaldroidFragment.DISABLE_DATES);
		if (disableDates != null) {
			disableDatesMap.clear();
			for (DateTime dateTime : disableDates) {
				disableDatesMap.put(dateTime, 1);
			}
		}

		selectedDates = (ArrayList<DateTime>) caldroidData
				.get(CaldroidFragment.SELECTED_DATES);
		if (selectedDates != null) {
			selectedDatesMap.clear();
			for (DateTime dateTime : selectedDates) {
				selectedDatesMap.put(dateTime, 1);
			}
		}
		scheduleDates=(HashMap<Integer, List<DateTime>>)caldroidData
				.get(CaldroidFragment.SCHEDULE_DATES);
		minDateTime = (DateTime) caldroidData
				.get(CaldroidFragment._MIN_DATE_TIME);
		maxDateTime = (DateTime) caldroidData
				.get(CaldroidFragment._MAX_DATE_TIME);
		startDayOfWeek = (Integer) caldroidData
				.get(CaldroidFragment.START_DAY_OF_WEEK);
		sixWeeksInCalendar = (Boolean) caldroidData
				.get(CaldroidFragment.SIX_WEEKS_IN_CALENDAR);

		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
				startDayOfWeek, sixWeeksInCalendar);
	}

	protected DateTime getToday() {
		if (today == null) {
			today = CalendarHelper.convertDateToDateTime(new Date());
		}
		return today;
	}

	@SuppressWarnings("unchecked")
	protected void setCustomResources(DateTime dateTime, View backgroundView,
			TextView textView) {
		// Set custom background resource
		HashMap<DateTime, Integer> backgroundForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData
				.get(CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP);
		if (backgroundForDateTimeMap != null) {
			// Get background resource for the dateTime
			Integer backgroundResource = backgroundForDateTimeMap.get(dateTime);

			// Set it
			if (backgroundResource != null) {
				backgroundView.setBackgroundResource(backgroundResource);
			}
		}

		// Set custom text color
		HashMap<DateTime, Integer> textColorForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData
				.get(CaldroidFragment._TEXT_COLOR_FOR_DATETIME_MAP);
		if (textColorForDateTimeMap != null) {
			// Get textColor for the dateTime
			Integer textColorResource = textColorForDateTimeMap.get(dateTime);

			// Set it
			if (textColorResource != null) {
				textView.setTextColor(resources.getColor(textColorResource));
			}
		}
	}

	/**
	 * Customize colors of text and background based on states of the cell
	 * (disabled, active, selected, etc)
	 *
	 * To be used only in getView method
	 *
	 */
	protected void customizeTextView(int position, View cellView) {
		
	   TextView cView =  (TextView) cellView.findViewById(R.id.calendar_tv);
	   TextView sView =  (TextView) cellView.findViewById(R.id.calendar_click);
	   cView.setTextColor(Color.BLACK);

		// Get dateTime of this cell
		DateTime dateTime = this.datetimeList.get(position);

		// Set color of the dates in previous / next month
		if (dateTime.getMonth() != month) {
			cView.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));
		}

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.lt(minDateTime))
				|| (maxDateTime != null && dateTime.gt(maxDateTime))
				|| (disableDates != null && disableDatesMap
						.containsKey(dateTime))) {

			cView.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				cellView.setBackgroundResource(R.drawable.disable_cell);
			} else {
				cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}

			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
			}
		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
			if (CaldroidFragment.selectedBackgroundDrawable != -1) {
				cellView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
			} else {
				cellView.setBackgroundColor(resources
						.getColor(R.color.caldroid_sky_blue));
			}

			cView.setTextColor(CaldroidFragment.selectedTextColor);
		} else {
			shouldResetSelectedView = true;
		}
		Iterator iter = scheduleDates.entrySet().iterator();
		sView.setBackgroundResource(R.color.white);
		while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Integer key = (Integer)entry.getKey();
				List<DateTime> times = (List<DateTime>)entry.getValue();
				switch(key){
				   case 0:
					   if (times.contains(dateTime)) {					
							sView.setBackgroundResource(R.drawable.gray_border);
					    }
					   break;
				   case 1:
					   for (DateTime dateTime2 : times) {
							if(dateTime.gteq(dateTime2)){					
									sView.setBackgroundResource(R.drawable.gray_border);
							}	
						}
					   break;
				   case 2:
					   for (DateTime dateTime2 : times) {
							if(dateTime.gteq(dateTime2)){						
								int day1=dateTime.getWeekDay();
								if (day1<7&&day1>1) {					
									sView.setBackgroundResource(R.drawable.gray_border);
							    }
							}
						}
					   break;
				   case 3:
					   for (DateTime dateTime2 : times) {
							if(dateTime.gteq(dateTime2)){						
								int day1=dateTime.getWeekDay();
								//Log.e("1", day1+"------------");
								int day2=dateTime2.getWeekDay();
								if (day1==day2) {					
									sView.setBackgroundResource(R.drawable.gray_border);
							    }
							}
						}
					   break;
				   case 4:
					   for (DateTime dateTime2 : times) {
							if(dateTime.gteq(dateTime2)){						
								int day1=dateTime.getDay();
								int day2=dateTime2.getDay();
								if (day1==day2) {					
									sView.setBackgroundResource(R.drawable.gray_border);
							    }
							}
						}
					   break;
				   case 5:
					   for (DateTime dateTime2 : times) {
							if(dateTime.gteq(dateTime2)){
								int month1=dateTime.getMonth();
								int day1=dateTime.getDay();
								int month2=dateTime2.getMonth();
								int day2=dateTime2.getDay();
								if (month1==month2&&day1==day2) {					
									sView.setBackgroundResource(R.drawable.gray_border);
							    }
							}
						}
					   break;
				   default:
						   break;
				}
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for today
			if (dateTime.equals(getToday())) {
				cView.setBackgroundResource(R.drawable.red_border);
			} else {
				cView.setBackgroundResource(R.drawable.cell_bg);
				
			}
			
		}

		cView.setText("" + dateTime.getDay());

		// Set custom color if required color
		setCustomResources(dateTime, cView, cView);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.datetimeList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cellView = convertView;

		// For reuse
		if (convertView == null) {
			cellView =  inflater.inflate(R.layout.date_cell, parent , false);
		}

		customizeTextView(position, cellView);

		return cellView;
	}

}
