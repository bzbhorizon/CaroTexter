/*
 * Copyright (C) 2012 Ben Bedwell
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */

package uk.ac.horizon.pszbb.scheduledtexter;

public class Participant {
	private String number;
	private long start_date;

	public Participant() {

	}

	public Participant(String number, long start_date) {
		setNumber(number);
		setStart_date(start_date);
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		String number = this.number;
		if (!number.startsWith("0")) {
			number = "0" + number;
		}
		return number;
	}

	public void setStart_date(long start_date) {
		this.start_date = start_date;
	}

	public long getStart_date() {
		return start_date;
	}
	
	public String toString () {
		return getNumber() + "; " + Utility.MS2D(getStart_date());
	}
}
