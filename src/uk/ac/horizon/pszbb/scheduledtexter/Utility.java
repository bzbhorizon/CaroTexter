/*
 * Copyright (C) 2012 Ben Bedwell
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */

package uk.ac.horizon.pszbb.scheduledtexter;

import java.util.Calendar;

public abstract class Utility {
	
	public static String MS2D (long ms) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ms);
		return c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
	}

	public static long D2MS(int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			c.add(Calendar.DAY_OF_YEAR, 1);
		}
		return c.getTimeInMillis();
	}

	// N.B. months start at 0
	public static long D2MS(int month, int day, int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.ZONE_OFFSET, 0);
		return c.getTimeInMillis();
	}

	public static int getHourOfDay() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}
	
	public static String getCompleteBy () {
		Calendar c = Calendar.getInstance();
		String completeBy = (c.get(Calendar.HOUR) + 2) + "";
		if (c.get(Calendar.AM_PM) == 0) {
			completeBy += "am";
		} else {
			completeBy += "pm";
		}
		return completeBy;
	}
	
	static String getLink() {
		// id of Qualtrics survey
		return "SV_bKpZaJgTfGOcvw9";
	}
	
}
