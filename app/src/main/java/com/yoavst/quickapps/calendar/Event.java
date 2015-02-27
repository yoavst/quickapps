package com.yoavst.quickapps.calendar;

import java.io.Serializable;

/**
 * Created by Yoav.
 */
public class Event implements Serializable {
	private final long id;
	private final String title;
	private final String location;
	private long startDate;
	private long endDate;
	private String date;
	private int color;
	boolean isAllDay = false;

	public Event(long id, String title, long startDate, long endDate, String location) {
		this(id, title, startDate, endDate, location, false);
	}

	public Event(long id, String title, long startDate, long endDate, String location, boolean isAllDay) {
		this(id,title, null, location, isAllDay);
		this.startDate = startDate;
		this.endDate = endDate;
		this.date = CalendarUtil.getDateFromEvent(this);
	}

	public Event(long id, String title, String date, String location) {
		this(id, title, date, location, false);
	}

	public Event(long id, String title, String date, String location, boolean isAllDay) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.location = location;
		this.isAllDay = isAllDay;
	}

	public Event setColor(int color) {
		this.color = color;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getLocation() {
		return location;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public String getDate() {
		return date;
	}

	public int getColor() {
		return color;
	}

	public long getId() {
		return id;
	}

	/**
	 * Return true if the event takes the whole day.
	 *
	 * @return true if the event takes the whole day.
	 */
	public boolean isAllDay() {
		return isAllDay;
	}

	@Override
	public String toString() {
		return "Event{" + "id=" + id + ", title='" + title + '\'' + ", location='" + location + '\'' + ", startDate=" + startDate + ", endDate=" + endDate + ", day='" + date + '\'' + ", color=" + color + '}';
	}
}
