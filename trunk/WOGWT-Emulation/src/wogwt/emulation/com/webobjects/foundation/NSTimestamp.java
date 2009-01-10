package com.webobjects.foundation;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

public class NSTimestamp extends Timestamp {

	public static final NSTimestamp DistantFuture = new NSTimestamp().timestampByAddingGregorianUnits(2922769, 0, 0, 0, 0, 0);
	public static final NSTimestamp DistantPast = new NSTimestamp().timestampByAddingGregorianUnits(-2000, 0, 0, 0, 0, 0);
	
	private static final String UNSUPPORTED = " is not a supported operation of com.webobjects.foundation.NSTimestamp";

	public NSTimestamp() {
		this(new Date().getTime());
	}
	
	public NSTimestamp(Date date) {
		this(date.getTime());
	}
	
	public NSTimestamp(long time) {
		super(time);
	}
	
	public NSTimestamp(long milliseconds, int nanoseconds) {
		super(milliseconds);
		
		if (nanoseconds > 0) {
			long justMilliseconds = milliseconds % 1000;
			long nanos = (justMilliseconds * 1000000) + nanoseconds;
			super.setNanos((int)nanos);
		}
	}
	
	public NSTimestamp(long time, NSTimestamp date) {
		this(date.getTime() + time);
	}
	
	public NSTimestamp(Timestamp sqlTimestamp) {
		super(sqlTimestamp.getTime());
		super.setNanos(sqlTimestamp.getNanos());
	}
	
	public int compare(NSTimestamp ts) {
		int result = compareTo(ts);
		if (result < 0)
			return NSComparator.OrderedAscending;
		else if (result == 0)
			return NSComparator.OrderedSame;
		else
			return NSComparator.OrderedDescending;
	}
	
	@Override
	public int getNanos() {
		int milliseconds =  Math.round(super.getNanos() / 1000000) * 1000000;
		return super.getNanos() - milliseconds;
	}

	public NSTimestamp timestampByAddingGregorianUnits(int years, 
			int months, int days, int hours, int minutes, int seconds)  {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(getTime());
		
		if (seconds != 0)
			cal.add(GregorianCalendar.SECOND, seconds);
		if (minutes != 0)
			cal.add(GregorianCalendar.MINUTE, minutes);
		if (hours != 0)
			cal.add(GregorianCalendar.HOUR, hours);
		if (days != 0)
			cal.add(GregorianCalendar.DATE, days);
		if (months != 0)
			cal.add(GregorianCalendar.MONTH, months);
		if (years != 0)
			cal.add(GregorianCalendar.YEAR, years);
		
		return new NSTimestamp(cal.getTime().getTime(), getNanos());
	}
	
	@Override
	public void setDate(int date) {
		throw new UnsupportedOperationException("setDate" + UNSUPPORTED);
	}
	
	@Override
	public void setMonth(int month) {
		throw new UnsupportedOperationException("setMonth" + UNSUPPORTED);
	}
	
	@Override
	public void setYear(int year) {
		throw new UnsupportedOperationException("setYear" + UNSUPPORTED);
	}
	
	@Override
	public void setHours(int hours) {
		throw new UnsupportedOperationException("setHours" + UNSUPPORTED);
	}
	
	@Override
	public void setMinutes(int minutes) {
		throw new UnsupportedOperationException("setMinutes" + UNSUPPORTED);
	}
	
	@Override
	public void setSeconds(int seconds) {
		throw new UnsupportedOperationException("setSeconds" + UNSUPPORTED);
	}
	
	@Override
	public void setNanos(int n) {
		throw new UnsupportedOperationException("setNanos" + UNSUPPORTED);
	}
	
	@Override
	public void setTime(long time) {
		throw new UnsupportedOperationException("setTime" + UNSUPPORTED);
	}
	
}