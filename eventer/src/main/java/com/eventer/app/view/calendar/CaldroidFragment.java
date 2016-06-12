package com.eventer.app.view.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.view.MyGridView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
@SuppressWarnings({"UnusedDeclaration"})
@SuppressLint({ "DefaultLocale", "UseSparseArrays", "InlinedApi", "SimpleDateFormat" })
public class CaldroidFragment extends DialogFragment {
	public String TAG = "CaldroidFragment";

	/**
	 * Weekday conventions
	 */
	public static int SUNDAY = 1;
	public static int MONDAY = 2;
	public static int TUESDAY = 3;
	public static int WEDNESDAY = 4;
	public static int THURSDAY = 5;
	public static int FRIDAY = 6;
	public static int SATURDAY = 7;

	/**
	 * Flags to display month
	 */
	private static final int MONTH_YEAR_FLAG = DateUtils.FORMAT_SHOW_DATE
			| DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;

	/**
	 * First day of month time
	 */
	private Time firstMonthTime = new Time();

	/**
	 * Reuse formatter to print "MMMM yyyy" format
	 */
	private final StringBuilder monthYearStringBuilder = new StringBuilder(50);
	private Formatter monthYearFormatter = new Formatter(
			monthYearStringBuilder, Locale.getDefault());

	/**
	 * To customize the selected background drawable and text color
	 */
	public static int selectedBackgroundDrawable = -1;
	public static int selectedTextColor = Color.BLACK;

	public final static int NUMBER_OF_PAGES = 4;

	/**
	 * To customize the disabled background drawable and text color
	 */
	public static int disabledBackgroundDrawable = -1;
	public static int disabledTextColor = Color.GRAY;

	/**
	 * Caldroid view components
	 */
	private TextView monthTitleTextView;
	private MyGridView weekdayGridView;
	private InfiniteViewPager dateViewPager;
	private DatePageChangeListener pageChangeListener;
	private ArrayList<DateGridFragment> fragments;

	/**
	 * Initial params key
	 */
	public final static String DIALOG_TITLE = "dialogTitle";
	public final static String MONTH = "month";
	public final static String YEAR = "year";
	public final static String SHOW_NAVIGATION_ARROWS = "showNavigationArrows";
	public final static String DISABLE_DATES = "disableDates";
	public final static String SELECTED_DATES = "selectedDates";
	public final static String MIN_DATE = "minDate";
	public final static String MAX_DATE = "maxDate";
	public final static String ENABLE_SWIPE = "enableSwipe";
	public final static String START_DAY_OF_WEEK = "startDayOfWeek";
	public final static String SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar";
	public final static String ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates";
	public final static String SCHEDULE_DATES = "userDates";

	/**
	 * For internal use
	 */
	public final static String _MIN_DATE_TIME = "_minDateTime";
	public final static String _MAX_DATE_TIME = "_maxDateTime";
	public final static String _BACKGROUND_FOR_DATETIME_MAP = "_backgroundForDateTimeMap";
	public final static String _TEXT_COLOR_FOR_DATETIME_MAP = "_textColorForDateTimeMap";

	/**
	 * Initial data
	 */
	protected String dialogTitle;
	public static int month = -1;
	protected int year = -1;
	protected ArrayList<DateTime> disableDates = new ArrayList<>();
	protected ArrayList<DateTime> selectedDates = new ArrayList<>();
	protected HashMap<Integer, List<DateTime>> scheduleDates = new HashMap<>();
	protected DateTime minDateTime;
	protected DateTime maxDateTime;
	protected ArrayList<DateTime> dateInMonthsList;

	protected TurntoTodayListener todaylistener;

	/**
	 * caldroidData belongs to Caldroid
	 */
	protected HashMap<String, Object> caldroidData = new HashMap<>();

	/**
	 * extraData belongs to client
	 */
	protected HashMap<String, Object> extraData = new HashMap<>();

	/**
	 * backgroundForDateMap holds background resource for each date
	 */
	protected HashMap<DateTime, Integer> backgroundForDateTimeMap = new HashMap<>();

	/**
	 * textColorForDateMap holds color for text for each date
	 */
	protected HashMap<DateTime, Integer> textColorForDateTimeMap = new HashMap<>();

	/**
	 * First column of calendar is Sunday
	 */
	protected int startDayOfWeek = SUNDAY;

	/**
	 * A calendar height is not fixed, it may have 5 or 6 rows. Set fitAllMonths
	 * to true so that the calendar will always have 6 rows
	 */
	private boolean sixWeeksInCalendar = true;

	/**
	 * datePagerAdapters hold 4 adapters, meant to be reused
	 */
	protected ArrayList<CaldroidGridAdapter> datePagerAdapters = new ArrayList<>();

	/**
	 * To control the navigation
	 */
	protected boolean enableSwipe = true;
	protected boolean showNavigationArrows = true;
	protected boolean enableClickOnDisabledDates = false;
	protected Button btn_turnto_today;

	/**
	 * dateItemClickListener is fired when user click on the date cell
	 */
	private OnItemClickListener dateItemClickListener;

	/**
	 * dateItemLongClickListener is fired when user does a longclick on the date
	 * cell
	 */
	private OnItemLongClickListener dateItemLongClickListener;

	/**
	 * caldroidListener inform library client of the event happens inside
	 * Caldroid
	 */
	private CaldroidListener caldroidListener;

	public CaldroidListener getCaldroidListener() {
		return caldroidListener;
	}

	/**
	 * Meant to be subclassed. User who wants to provide custom view, need to
	 * provide custom adapter here
	 */
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
		return new CaldroidGridAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);
	}

	/**
	 * For client to customize the weekDayGridView
	 *
	 */
	public GridView getWeekdayGridView() {
		return weekdayGridView;
	}

	/**
	 * For client to access array of rotating fragments
	 */
	public ArrayList<DateGridFragment> getFragments() {
		return fragments;
	}

	/**
	 * To let client customize month title textview
	 */
	public TextView getMonthTitleTextView() {
		return monthTitleTextView;
	}

	public void setMonthTitleTextView(TextView monthTitleTextView) {
		this.monthTitleTextView = monthTitleTextView;
	}

	/**
	 * Get 4 adapters of the date grid views. Useful to set custom data and
	 * refresh date grid view
	 *
	 */
	public ArrayList<CaldroidGridAdapter> getDatePagerAdapters() {
		return datePagerAdapters;
	}

	/**
	 * caldroidData return data belong to Caldroid
	 *
	 * */
	public HashMap<String, Object> getCaldroidData() {
		caldroidData.clear();
		caldroidData.put(DISABLE_DATES, disableDates);
		caldroidData.put(SELECTED_DATES, selectedDates);
		caldroidData.put(_MIN_DATE_TIME, minDateTime);
		caldroidData.put(_MAX_DATE_TIME, maxDateTime);
		caldroidData.put(START_DAY_OF_WEEK, startDayOfWeek);
		caldroidData.put(SIX_WEEKS_IN_CALENDAR,
				sixWeeksInCalendar);
		caldroidData.put(SCHEDULE_DATES,scheduleDates);
		// For internal use
		caldroidData
				.put(_BACKGROUND_FOR_DATETIME_MAP, backgroundForDateTimeMap);
		caldroidData.put(_TEXT_COLOR_FOR_DATETIME_MAP, textColorForDateTimeMap);

		return caldroidData;
	}

	/**
	 * Extra data is data belong to Client
	 */
	public HashMap<String, Object> getExtraData() {
		return extraData;
	}
	public void SetScheduleDates(){
		scheduleDates = new HashMap<>();
		new SchedualDao(getActivity());
		DBManager dbHelper;
		dbHelper = new DBManager(getActivity());
		dbHelper.openDatabase();

		Cursor c=dbHelper.findList(true, "dbSchedule", new String[]{"startTime","endTime","frequency"}, " status >? and flag > 0", new String[]{"-1"}, null, null, null, null);
		while (c.moveToNext()) {
			String _time = c.getString(c.getColumnIndex("startTime"));
			String end_time = c.getString(c.getColumnIndex("endTime"));
			if(_time!=null){
				try{
					int _f = c.getInt(c.getColumnIndex("frequency"));
					List<DateTime> ts=new ArrayList<>();
					DateTime dt = CalendarHelper.getDateTimeFromString(
							_time, "yyyy-MM-dd");
					DateTime end = CalendarHelper.getDateTimeFromString(
							end_time, "yyyy-MM-dd");
					if(dt!=null&&end!=null){
						if(scheduleDates.containsKey(_f)){
							ts=scheduleDates.get(_f);
							while(end.gteq(dt)){
								if(!ts.contains(dt)){
									ts.add(dt);
								}
								dt=dt.plusDays(1);
							}

							scheduleDates.put(_f,ts);
						}else{
							while(end.gteq(dt)){
								if(!ts.contains(dt)){
									ts.add(dt);
								}
								dt=dt.plusDays(1);
							}
							scheduleDates.put(_f,ts);
						}
					}
				}
				catch(Exception e){
					Log.e("1", e.toString());
				}
			}
		}
		dbHelper.closeDatabase();
		Log.e("1", "db finish!");
	}

	public void addScheduleDates(int _f,String time){
		DateTime dt = CalendarHelper.getDateTimeFromString(
				time, "yyyy-MM-dd");
		List<DateTime> ts;
		if(scheduleDates.containsKey(_f)){
			ts=scheduleDates.get(_f);
			if(!ts.contains(dt)){
				ts.add(dt);
			}
			scheduleDates.put(_f,ts);
		}
	}
	/**
	 * Client can set custom data in this HashMap
	 *
	 */
	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	/**
	 * Set backgroundForDateMap
	 */
	public void setBackgroundResourceForDates(
			HashMap<Date, Integer> backgroundForDateMap) {
		// Clear first
		backgroundForDateTimeMap.clear();

		if (backgroundForDateMap == null || backgroundForDateMap.size() == 0) {
			return;
		}

		for (Date date : backgroundForDateMap.keySet()) {
			Integer resource = backgroundForDateMap.get(date);
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
			backgroundForDateTimeMap.put(dateTime, resource);
		}
	}

	public void setBackgroundResourceForDateTimes(
			HashMap<DateTime, Integer> backgroundForDateTimeMap) {
		this.backgroundForDateTimeMap.putAll(backgroundForDateTimeMap);
	}

	public void setBackgroundResourceForDate(int backgroundRes, Date date) {
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
		backgroundForDateTimeMap.put(dateTime, backgroundRes);
	}

	public void setBackgroundResourceForDateTime(int backgroundRes,
												 DateTime dateTime) {
		backgroundForDateTimeMap.put(dateTime, backgroundRes);
	}

	/**
	 * Set textColorForDateMap
	 *
	 */
	public void setTextColorForDates(HashMap<Date, Integer> textColorForDateMap) {
		// Clear first
		textColorForDateTimeMap.clear();

		if (textColorForDateMap == null || textColorForDateMap.size() == 0) {
			return;
		}

		for (Date date : textColorForDateMap.keySet()) {
			Integer resource = textColorForDateMap.get(date);
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
			textColorForDateTimeMap.put(dateTime, resource);
		}
	}

	public void setTextColorForDateTimes(
			HashMap<DateTime, Integer> textColorForDateTimeMap) {
		this.textColorForDateTimeMap.putAll(textColorForDateTimeMap);
	}

	public void setTextColorForDate(int textColorRes, Date date) {
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
		textColorForDateTimeMap.put(dateTime, textColorRes);
	}

	public void setTextColorForDateTime(int textColorRes, DateTime dateTime) {
		textColorForDateTimeMap.put(dateTime, textColorRes);
	}

	/**
	 * Get current saved sates of the Caldroid. Useful for handling rotation
	 */
	public Bundle getSavedStates() {
		Bundle bundle = new Bundle();
		bundle.putInt(MONTH, month);
		bundle.putInt(YEAR, year);

		if (dialogTitle != null) {
			bundle.putString(DIALOG_TITLE, dialogTitle);
		}

		if (selectedDates != null && selectedDates.size() > 0) {
			bundle.putStringArrayList(SELECTED_DATES,
					CalendarHelper.convertToStringList(selectedDates));
		}

		if (disableDates != null && disableDates.size() > 0) {
			bundle.putStringArrayList(DISABLE_DATES,
					CalendarHelper.convertToStringList(disableDates));
		}

		if (minDateTime != null) {
			bundle.putString(MIN_DATE, minDateTime.format("YYYY-MM-DD"));
		}

		if (maxDateTime != null) {
			bundle.putString(MAX_DATE, maxDateTime.format("YYYY-MM-DD"));
		}

		bundle.putBoolean(SHOW_NAVIGATION_ARROWS, showNavigationArrows);
		bundle.putBoolean(ENABLE_SWIPE, enableSwipe);
		bundle.putInt(START_DAY_OF_WEEK, startDayOfWeek);
		bundle.putBoolean(SIX_WEEKS_IN_CALENDAR, sixWeeksInCalendar);

		return bundle;
	}

	/**
	 * Save current state to bundle outState
	 *
	 */
	public void saveStatesToKey(Bundle outState, String key) {
		outState.putBundle(key, getSavedStates());
	}

	/**
	 * Restore current states from savedInstanceState
	 *
	 */
	public void restoreStatesFromKey(Bundle savedInstanceState, String key) {
		if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
			Bundle caldroidSavedState = savedInstanceState.getBundle(key);
			setArguments(caldroidSavedState);
		}
	}

	/**
	 * Restore state for dialog
	 *
	 */
	public void restoreDialogStatesFromKey(FragmentManager manager,
										   Bundle savedInstanceState, String key, String dialogTag) {
		restoreStatesFromKey(savedInstanceState, key);

		CaldroidFragment existingDialog = (CaldroidFragment) manager
				.findFragmentByTag(dialogTag);
		if (existingDialog != null) {
			existingDialog.dismiss();
			show(manager, dialogTag);
		}
	}

	/**
	 * Get current virtual position of the month being viewed
	 */
	public int getCurrentVirtualPosition() {
		int currentPage = dateViewPager.getCurrentItem();
		return pageChangeListener.getCurrent(currentPage);
	}

	/**
	 * Move calendar to the specified date
	 *
	 */
	public void moveToDate(Date date) {
		moveToDateTime(CalendarHelper.convertDateToDateTime(date));
	}

	public void moveToToday() {
		turnToToday(todaylistener);
		todaylistener.onTurnTo();
	}

	/**
	 * turn to today.
	 *
	 */
	public void turnToToday(TurntoTodayListener Listener) {
		this.todaylistener=Listener;
	}

	/**
	 * Move calendar to specified dateTime, with animation
	 *
	 */
	public void moveToDateTime(DateTime dateTime) {

		DateTime firstOfMonth = new DateTime(year, month, 1, 0, 0, 0, 0);
		DateTime lastOfMonth = firstOfMonth.getEndOfMonth();

		// To create a swipe effect
		// Do nothing if the dateTime is in current month

		// Calendar swipe left when dateTime is in the past
		if (dateTime.lt(firstOfMonth)) {
			// Get next month of dateTime. When swipe left, month will
			// decrease
			DateTime firstDayNextMonth = dateTime.plus(0, 1, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);

			// Refresh adapters
			pageChangeListener.setCurrentDateTime(firstDayNextMonth);
			int currentItem = dateViewPager.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// Swipe left
			dateViewPager.setCurrentItem(currentItem - 1);
		}

		// Calendar swipe right when dateTime is in the future
		else if (dateTime.gt(lastOfMonth)) {
			// Get last month of dateTime. When swipe right, the month will
			// increase
			DateTime firstDayLastMonth = dateTime.minus(0, 1, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);

			// Refresh adapters
			pageChangeListener.setCurrentDateTime(firstDayLastMonth);
			int currentItem = dateViewPager.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// Swipe right
			dateViewPager.setCurrentItem(currentItem + 1);
		}

	}

	/**
	 * Set month and year for the calendar. This is to avoid naive
	 * implementation of manipulating month and year. All dates within same
	 * month/year give same result
	 *
	 */
	public void setCalendarDate(Date date) {
		setCalendarDateTime(CalendarHelper.convertDateToDateTime(date));
	}

	public void setCalendarDateTime(DateTime dateTime) {
		month = dateTime.getMonth();
		year = dateTime.getYear();

		// Notify listener
		if (caldroidListener != null) {
			caldroidListener.onChangeMonth(month, year);
		}

		refreshView();
	}

	/**
	 * Set calendar to previous month
	 */
	public void prevMonth() {
		dateViewPager.setCurrentItem(pageChangeListener.getCurrentPage() - 1);
	}

	/**
	 * Set calendar to next month
	 */
	public void nextMonth() {
		dateViewPager.setCurrentItem(pageChangeListener.getCurrentPage() + 1);
	}

	/**
	 * Clear all disable dates. Notice this does not refresh the calendar, need
	 * to explicitly call refreshView()
	 */
	public void clearDisableDates() {
		disableDates.clear();
	}

	/**
	 * Set disableDates from ArrayList of Date
	 *
	 */
	public void setDisableDates(ArrayList<Date> disableDateList) {
		disableDates.clear();
		if (disableDateList == null || disableDateList.size() == 0) {
			return;
		}

		for (Date date : disableDateList) {
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
			disableDates.add(dateTime);
		}

	}

	/**
	 * Set disableDates from ArrayList of String. By default, the date formatter
	 * is yyyy-MM-dd. For e.g 2013-12-24
	 *
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings) {
		setDisableDatesFromString(disableDateStrings, null);
	}

	/**
	 * Set disableDates from ArrayList of String with custom date format. For
	 * example, if the date string is 06-Jan-2013, use date format dd-MMM-yyyy.
	 * This method will refresh the calendar, it's not necessary to call
	 * refreshView()
	 *
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings,
										  String dateFormat) {
		disableDates.clear();
		if (disableDateStrings == null) {
			return;
		}

		for (String dateString : disableDateStrings) {
			DateTime dateTime = CalendarHelper.getDateTimeFromString(
					dateString, dateFormat);
			disableDates.add(dateTime);
		}
	}

	/**
	 * To clear selectedDates. This method does not refresh view, need to
	 * explicitly call refreshView()
	 */
	public void clearSelectedDates() {
		selectedDates.clear();
	}

	/**
	 * Select the dates from fromDate to toDate. By default the background color
	 * is holo_blue_light, and the text color is black. You can customize the
	 * background by changing CaldroidFragment.selectedBackgroundDrawable, and
	 * change the text color CaldroidFragment.selectedTextColor before call this
	 * method. This method does not refresh view, need to call refreshView()
	 *
	 */
	public void setSelectedDates(Date fromDate, Date toDate) {
		// Ensure fromDate is before toDate
		if (fromDate == null || toDate == null || fromDate.after(toDate)) {
			return;
		}

		selectedDates.clear();

		DateTime fromDateTime = CalendarHelper.convertDateToDateTime(fromDate);
		DateTime toDateTime = CalendarHelper.convertDateToDateTime(toDate);

		DateTime dateTime = fromDateTime;
		while (dateTime.lt(toDateTime)) {
			selectedDates.add(dateTime);
			dateTime = dateTime.plusDays(1);
		}
		selectedDates.add(toDateTime);
	}

	/**
	 * Convenient method to select dates from String
	 *
	 * @throws ParseException
	 */
	public void setSelectedDateStrings(String fromDateString,
									   String toDateString, String dateFormat) throws ParseException {

		Date fromDate = CalendarHelper.getDateFromString(fromDateString,
				dateFormat);
		Date toDate = CalendarHelper
				.getDateFromString(toDateString, dateFormat);
		setSelectedDates(fromDate, toDate);
	}

	/**
	 * Check if the navigation arrow is shown
	 *
	 */
	public boolean isShowNavigationArrows() {
		return showNavigationArrows;
	}

	/**
	 * Show or hide the navigation arrows
	 *
	 */
	public void setShowNavigationArrows() {
		this.showNavigationArrows = false;
	}

	/**
	 * Enable / Disable swipe to navigate different months
	 *
	 */
	public boolean isEnableSwipe() {
		return enableSwipe;
	}

	public void setEnableSwipe(boolean enableSwipe) {
		this.enableSwipe = enableSwipe;
		dateViewPager.setEnabled(enableSwipe);
	}

	/**
	 * Set min date. This method does not refresh view
	 *
	 */
	public void setMinDate(Date minDate) {
		if (minDate == null) {
			minDateTime = null;
		} else {
			minDateTime = CalendarHelper.convertDateToDateTime(minDate);
		}
	}

	public boolean isSixWeeksInCalendar() {
		return sixWeeksInCalendar;
	}

	public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
		this.sixWeeksInCalendar = sixWeeksInCalendar;
		dateViewPager.setSixWeeksInCalendar(sixWeeksInCalendar);
	}

	/**
	 * Convenient method to set min date from String. If dateFormat is null,
	 * default format is yyyy-MM-dd
	 *
	 */
	public void setMinDateFromString(String minDateString, String dateFormat) {
		if (minDateString == null) {
			setMinDate(null);
		} else {
			minDateTime = CalendarHelper.getDateTimeFromString(minDateString,
					dateFormat);
		}
	}

	/**
	 * Set max date. This method does not refresh view
	 *
	 */
	public void setMaxDate(Date maxDate) {
		if (maxDate == null) {
			maxDateTime = null;
		} else {
			maxDateTime = CalendarHelper.convertDateToDateTime(maxDate);
		}
	}

	/**
	 * Convenient method to set max date from String. If dateFormat is null,
	 * default format is yyyy-MM-dd
	 *
	 */
	public void setMaxDateFromString(String maxDateString, String dateFormat) {
		if (maxDateString == null) {
			setMaxDate(null);
		} else {
			maxDateTime = CalendarHelper.getDateTimeFromString(maxDateString,
					dateFormat);
		}
	}

	/**
	 * Set caldroid listener when user click on a date
	 **/
	public void setCaldroidListener(CaldroidListener caldroidListener) {
		this.caldroidListener = caldroidListener;
	}

	/**
	 * Callback to listener when date is valid (not disable, not outside of
	 * min/max date)
	 *
	 */
	private OnItemClickListener getDateItemClickListener() {
		if (dateItemClickListener == null) {
			dateItemClickListener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {

					DateTime dateTime = dateInMonthsList.get(position);

					if (caldroidListener != null) {
						if (!enableClickOnDisabledDates) {
							if ((minDateTime != null && dateTime
									.lt(minDateTime))
									|| (maxDateTime != null && dateTime
									.gt(maxDateTime))
									|| (disableDates != null && disableDates
									.indexOf(dateTime) != -1)) {
								return;
							}
						}

						Date date = CalendarHelper
								.convertDateTimeToDate(dateTime);
						caldroidListener.onSelectDate(date, view);

					}
				}
			};
		}

		return dateItemClickListener;
	}

	/**
	 * Callback to listener when date is valid (not disable, not outside of
	 * min/max date)
	 *
	 */
	private OnItemLongClickListener getDateItemLongClickListener() {
		if (dateItemLongClickListener == null) {
			dateItemLongClickListener = new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
											   View view, int position, long id) {

					DateTime dateTime = dateInMonthsList.get(position);

					if (caldroidListener != null) {
						if (!enableClickOnDisabledDates) {
							if ((minDateTime != null && dateTime
									.lt(minDateTime))
									|| (maxDateTime != null && dateTime
									.gt(maxDateTime))
									|| (disableDates != null && disableDates
									.indexOf(dateTime) != -1)) {
								return false;
							}
						}
						Date date = CalendarHelper
								.convertDateTimeToDate(dateTime);
						caldroidListener.onLongClickDate(date, view);
					}

					return true;
				}
			};
		}

		return dateItemLongClickListener;
	}

	/**
	 * Refresh month title text view when user swipe
	 */
	protected void refreshMonthTitleTextView() {
		// Refresh title view
		firstMonthTime.year = year;
		firstMonthTime.month = month - 1;
		firstMonthTime.monthDay = 1;
		long millis = firstMonthTime.toMillis(true);

		// This is the method used by the platform Calendar app to get a
		// correctly localized month name for display on a wall calendar
		monthYearStringBuilder.setLength(0);
		String monthTitle = DateUtils.formatDateRange(getActivity(),
				monthYearFormatter, millis, millis, MONTH_YEAR_FLAG).toString();

		monthTitleTextView.setText(monthTitle);
	}

	/**
	 * Refresh view when parameter changes. You should always change all
	 * parameters first, then call this method.鍒锋柊鏃ュ巻
	 */
	public void refreshView() {
		// If month and year is not yet initialized, refreshView doesn't do
		// anything
		if (month == -1 || year == -1) {
			return;
		}

		refreshMonthTitleTextView();

		// Refresh the date grid views
		for (CaldroidGridAdapter adapter : datePagerAdapters) {
			// Reset caldroid data
			adapter.setCaldroidData(getCaldroidData());

			// Reset extra data
			adapter.setExtraData(extraData);

			// Refresh view
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Retrieve initial arguments to the fragment Data can include: month, year,
	 * dialogTitle, showNavigationArrows,(String) disableDates, selectedDates,
	 * minDate, maxDate
	 */
	protected void retrieveInitialArgs() {
		// Get arguments
		Bundle args = getArguments();
		if (args != null) {
			// Get month, year  
			month = args.getInt(MONTH, -1);
			year = args.getInt(YEAR, -1);
			dialogTitle = args.getString(DIALOG_TITLE);
			Dialog dialog = getDialog();
			if (dialog != null) {
				if (dialogTitle != null) {
					dialog.setTitle(dialogTitle);
				} else {
					// Don't display title bar if user did not supply
					// dialogTitle
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				}
			}

			// Get start day of Week. Default calendar first column is SUNDAY
			startDayOfWeek = args.getInt(START_DAY_OF_WEEK, 1);
			if (startDayOfWeek > 7) {
				startDayOfWeek = startDayOfWeek % 7;
			}

			// Should show arrow
			showNavigationArrows = args
					.getBoolean(SHOW_NAVIGATION_ARROWS, true);

			// Should enable swipe to change month
			enableSwipe = args.getBoolean(ENABLE_SWIPE, true);

			// Get sixWeeksInCalendar
			sixWeeksInCalendar = args.getBoolean(SIX_WEEKS_IN_CALENDAR, true);

			// Get clickable setting
			enableClickOnDisabledDates = args.getBoolean(
					ENABLE_CLICK_ON_DISABLED_DATES, false);

			// Get disable dates
			ArrayList<String> disableDateStrings = args
					.getStringArrayList(DISABLE_DATES);
			if (disableDateStrings != null && disableDateStrings.size() > 0) {
				disableDates.clear();
				for (String dateString : disableDateStrings) {
					DateTime dt = CalendarHelper.getDateTimeFromString(
							dateString, "yyyy-MM-dd");
					if(dt!=null)
						disableDates.add(dt);
				}
			}
			SetScheduleDates();
			// Get selected dates
			ArrayList<String> selectedDateStrings = args
					.getStringArrayList(SELECTED_DATES);

			if (selectedDateStrings != null && selectedDateStrings.size() > 0) {
				selectedDates.clear();
				for (String dateString : selectedDateStrings) {
					DateTime dt = CalendarHelper.getDateTimeFromString(
							dateString, "yyyy-MM-dd");
					if(dt!=null)
						selectedDates.add(dt);

				}
			}else{
				Log.e("1","null");

			}

			// Get min date and max date
			String minDateTimeString = args.getString(MIN_DATE);
			if (minDateTimeString != null) {
				minDateTime = CalendarHelper.getDateTimeFromString(
						minDateTimeString, null);
			}

			String maxDateTimeString = args.getString(MAX_DATE);
			if (maxDateTimeString != null) {
				maxDateTime = CalendarHelper.getDateTimeFromString(
						maxDateTimeString, null);
			}

		}
		if (month == -1 || year == -1) {
			DateTime dateTime = DateTime.today(TimeZone.getDefault());
			month = dateTime.getMonth();
			year = dateTime.getYear();
		}
	}

	/**
	 * To support faster init
	 *
	 */
	public static CaldroidFragment newInstance(String dialogTitle, int month,
											   int year) {
		CaldroidFragment f = new CaldroidFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putString(DIALOG_TITLE, dialogTitle);
		args.putInt(MONTH, month);
		args.putInt(YEAR, year);

		f.setArguments(args);

		return f;
	}

	/**
	 * Below code fixed the issue viewpager disappears in dialog mode on
	 * orientation change
	 *
	 * Code taken from Andy Dennie and Zsombor Erdody-Nagy
	 * http://stackoverflow.com/questions/8235080/fragments-dialogfragment
	 * -and-screen-rotation
	 */
	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	/**
	 * Setup view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		retrieveInitialArgs();

		// To support keeping instance for dialog
		if (getDialog() != null) {
			setRetainInstance(true);
		}

		// Inflate layout
		View view = inflater.inflate(R.layout.calendar_view, container, false);

		// For the monthTitleTextView
		monthTitleTextView = (TextView) view
				.findViewById(R.id.calendar_month_year_textview);
		monthTitleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Calendar localCalendar = Calendar.getInstance();
				Log.e("1",monthTitleTextView.getText().toString());
				localCalendar.setTime(strToDate("yyyy年MM月", monthTitleTextView.getText().toString()));
				new MonPickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
//				                    localCalendar.set(1, year);  
//				                    localCalendar.set(2, monthOfYear);  
						monthTitleTextView.setText(year+"年"+(monthOfYear+1)+"月");

						String date_str=year+"-"+(monthOfYear+1);
						SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM");
						Date date;
						try {
							date = formatter.parse(date_str);
							moveToDate(date);
							//refreshView();;
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}, localCalendar.get(1), localCalendar.get(2),localCalendar.get(5)).show();
			}
		});
		btn_turnto_today = (Button) view.findViewById(R.id.btn_turnto_today);
		btn_turnto_today.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				moveToToday();
			}
		});

		// Show navigation arrows depend on initial arguments
		setShowNavigationArrows();

		// For the weekday gridview ("SUN, MON, TUE, WED, THU, FRI, SAT")
		weekdayGridView = (MyGridView) view.findViewById(R.id.weekday_gridview);
		WeekdayArrayAdapter weekdaysAdapter = new WeekdayArrayAdapter(
				getActivity(), android.R.layout.simple_list_item_1,
				getDaysOfWeek());
		weekdayGridView.setAdapter(weekdaysAdapter);

		// Setup all the pages of date grid views. These pages are recycled
		setupDateGridPages(view);

		// Refresh view
		refreshView();

		// Inform client that all views are created and not null
		// Client should perform customization for buttons and textviews here
		if (caldroidListener != null) {
			caldroidListener.onCaldroidViewCreated();
		}


		return view;
	}

	// 字符串类型日期转化成date类型  
	public static Date strToDate(String style, String date) {
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}


	public class MonPickerDialog extends DatePickerDialog {
		public MonPickerDialog(Context context, int theme,OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
			super(context, theme,callBack, year, monthOfYear, dayOfMonth);
			this.setTitle(year + "年" + (monthOfYear + 1) + "月");
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);
			this.setTitle(year + "年" + (month + 1) + "月");
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}

		@Override
		protected void onStop() {
			super.onStop();
		}

	}
	/**
	 * Setup 4 pages contain date grid views. These pages are recycled to use
	 * memory efficient
	 *
	 */
	private void setupDateGridPages(View view) {
		// Get current date time
		DateTime currentDateTime = new DateTime(year, month, 1, 0, 0, 0, 0);

		// Set to pageChangeListener
		pageChangeListener = new DatePageChangeListener();
		pageChangeListener.setCurrentDateTime(currentDateTime);

		// Setup adapters for the grid views
		// Current month
		CaldroidGridAdapter adapter0 = getNewDatesGridAdapter(
				currentDateTime.getMonth(), currentDateTime.getYear());

		// Setup dateInMonthsList
		dateInMonthsList = adapter0.getDatetimeList();

		// Next month
		DateTime nextDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0,
				DateTime.DayOverflow.LastDay);
		CaldroidGridAdapter adapter1 = getNewDatesGridAdapter(
				nextDateTime.getMonth(), nextDateTime.getYear());

		// Next 2 month
		DateTime next2DateTime = nextDateTime.plus(0, 1, 0, 0, 0, 0, 0,
				DateTime.DayOverflow.LastDay);
		CaldroidGridAdapter adapter2 = getNewDatesGridAdapter(
				next2DateTime.getMonth(), next2DateTime.getYear());

		// Previous month
		DateTime prevDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0,
				DateTime.DayOverflow.LastDay);
		CaldroidGridAdapter adapter3 = getNewDatesGridAdapter(
				prevDateTime.getMonth(), prevDateTime.getYear());

		// Add to the array of adapters
		datePagerAdapters.add(adapter0);
		datePagerAdapters.add(adapter1);
		datePagerAdapters.add(adapter2);
		datePagerAdapters.add(adapter3);

		// Set adapters to the pageChangeListener so it can refresh the adapter
		// when page change
		pageChangeListener.setCaldroidGridAdapters(datePagerAdapters);

		// Setup InfiniteViewPager and InfinitePagerAdapter. The
		// InfinitePagerAdapter is responsible
		// for reuse the fragments
		dateViewPager = (InfiniteViewPager) view
				.findViewById(R.id.months_infinite_pager);

		// Set enable swipe
		dateViewPager.setEnabled(enableSwipe);

		// Set if viewpager wrap around particular month or all months (6 rows)
		dateViewPager.setSixWeeksInCalendar(sixWeeksInCalendar);

		// Set the numberOfDaysInMonth to dateViewPager so it can calculate the
		// height correctly
		dateViewPager.setDatesInMonth(dateInMonthsList);

		// MonthPagerAdapter actually provides 4 real fragments. The
		// InfinitePagerAdapter only recycles fragment provided by this
		// MonthPagerAdapter
		final MonthPagerAdapter pagerAdapter = new MonthPagerAdapter(
				getChildFragmentManager());

		// Provide initial data to the fragments, before they are attached to
		// view.
		fragments = pagerAdapter.getFragments();
		for (int i = 0; i < NUMBER_OF_PAGES; i++) {
			DateGridFragment dateGridFragment = fragments.get(i);
			CaldroidGridAdapter adapter = datePagerAdapters.get(i);
			dateGridFragment.setGridAdapter(adapter);
			dateGridFragment.setOnItemClickListener(getDateItemClickListener());
			dateGridFragment
					.setOnItemLongClickListener(getDateItemLongClickListener());
		}

		// Setup InfinitePagerAdapter to wrap around MonthPagerAdapter
		InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(
				pagerAdapter);

		// Use the infinitePagerAdapter to provide data for dateViewPager
		dateViewPager.setAdapter(infinitePagerAdapter);

		// Setup pageChangeListener
		dateViewPager.setOnPageChangeListener(pageChangeListener);
	}

	/**
	 * To display the week day title
	 *
	 * @return "SUN, MON, TUE, WED, THU, FRI, SAT"
	 */
	private ArrayList<String> getDaysOfWeek() {
		ArrayList<String> list = new ArrayList<String>();

		SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());

		// 17 Feb 2013 is Sunday
		DateTime sunday = new DateTime(2013, 2, 17, 0, 0, 0, 0);
		DateTime nextDay = sunday.plusDays(startDayOfWeek - SUNDAY);

		for (int i = 0; i < 7; i++) {
			Date date = CalendarHelper.convertDateTimeToDate(nextDay);
			list.add(fmt.format(date).toUpperCase());
			nextDay = nextDay.plusDays(1);
		}

		return list;
	}

	/**
	 * DatePageChangeListener refresh the date grid views when user swipe the
	 * calendar
	 *
	 * @author thomasdao
	 *
	 */
	public class DatePageChangeListener implements OnPageChangeListener {
		private int currentPage = InfiniteViewPager.OFFSET;
		private DateTime currentDateTime;
		private ArrayList<CaldroidGridAdapter> caldroidGridAdapters;

		/**
		 * Return currentPage of the dateViewPager
		 *
		 * @return
		 */
		public int getCurrentPage() {
			return currentPage;
		}

		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

		/**
		 * Return currentDateTime of the selected page
		 *
		 * @return
		 */
		public DateTime getCurrentDateTime() {
			return currentDateTime;
		}

		public void setCurrentDateTime(DateTime dateTime) {
			this.currentDateTime = dateTime;
			setCalendarDateTime(currentDateTime);
		}

		/**
		 * Return 4 adapters
		 *
		 * @return
		 */
		public ArrayList<CaldroidGridAdapter> getCaldroidGridAdapters() {
			return caldroidGridAdapters;
		}

		public void setCaldroidGridAdapters(
				ArrayList<CaldroidGridAdapter> caldroidGridAdapters) {
			this.caldroidGridAdapters = caldroidGridAdapters;
		}

		/**
		 * Return virtual next position
		 *
		 */
		private int getNext(int position) {
			return (position + 1) % CaldroidFragment.NUMBER_OF_PAGES;
		}

		/**
		 * Return virtual previous position
		 *
		 */
		private int getPrevious(int position) {
			return (position + 3) % CaldroidFragment.NUMBER_OF_PAGES;
		}

		/**
		 * Return virtual current position
		 *
		 */
		public int getCurrent(int position) {
			return position % CaldroidFragment.NUMBER_OF_PAGES;
		}

		@Override
		public void onPageScrollStateChanged(int position) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void refreshAdapters(int position) {
			// Get adapters to refresh
			CaldroidGridAdapter currentAdapter = caldroidGridAdapters
					.get(getCurrent(position));
			CaldroidGridAdapter prevAdapter = caldroidGridAdapters
					.get(getPrevious(position));
			CaldroidGridAdapter nextAdapter = caldroidGridAdapters
					.get(getNext(position));

			if (position == currentPage) {
				// Refresh current adapter

				currentAdapter.setAdapterDateTime(currentDateTime);
				currentAdapter.notifyDataSetChanged();

				// Refresh previous adapter
				prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0,
						0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				prevAdapter.notifyDataSetChanged();

				// Refresh next adapter
				nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0,
						0, 0, 0, DateTime.DayOverflow.LastDay));
				nextAdapter.notifyDataSetChanged();
			}
			// Detect if swipe right or swipe left
			// Swipe right
			else if (position > currentPage) {
				// Update current date time to next month
				currentDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0,
						DateTime.DayOverflow.LastDay);

				// Refresh the adapter of next gridview
				nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0,
						0, 0, 0, DateTime.DayOverflow.LastDay));
				nextAdapter.notifyDataSetChanged();

			}
			// Swipe left
			else {
				// Update current date time to previous month
				currentDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0,
						DateTime.DayOverflow.LastDay);

				// Refresh the adapter of previous gridview
				prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0,
						0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				prevAdapter.notifyDataSetChanged();
			}

			// Update current page
			currentPage = position;
		}

		/**
		 * Refresh the fragments
		 */
		@Override
		public void onPageSelected(int position) {
			refreshAdapters(position);

			// Update current date time of the selected page
			setCalendarDateTime(currentDateTime);

			// Update all the dates inside current month
			CaldroidGridAdapter currentAdapter = caldroidGridAdapters
					.get(position % CaldroidFragment.NUMBER_OF_PAGES);

			// Refresh dateInMonthsList
			dateInMonthsList.clear();
			dateInMonthsList.addAll(currentAdapter.getDatetimeList());
		}

	}

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	public interface TurntoTodayListener {
		void onTurnTo();
	}

}