/*
 * Copyright (C) 2012 Ben Bedwell
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */

package uk.ac.horizon.pszbb.scheduledtexter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ParticipantsDataSource {

	// Database fields
	private SQLiteDatabase db;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_NUMBER,
			MySQLiteHelper.COLUMN_START_DATE };

	public ParticipantsDataSource (Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open () throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close () {
		dbHelper.close();
	}

	public Participant createParticipant (String number, long start_date) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NUMBER, number);
		values.put(MySQLiteHelper.COLUMN_START_DATE, start_date);
		long result = db.insert(MySQLiteHelper.TABLE_PARTICIPANTS, null, values);
		if (result >= 0) {
			Cursor cursor = db.query(MySQLiteHelper.TABLE_PARTICIPANTS,
				allColumns, MySQLiteHelper.COLUMN_NUMBER + " = " + number, null,
				null, null, null);
			cursor.moveToFirst();
			Participant newParticipant = cursorToParticipant(cursor);
			cursor.close();
			return newParticipant;
		} else {
			return null;
		}
	}

	public void deleteParticipant(Participant participant) {
		String number = participant.getNumber();
		System.out.println("Participant deleted with number: " + number);
		db.delete(MySQLiteHelper.TABLE_PARTICIPANTS, MySQLiteHelper.COLUMN_NUMBER
				+ " = " + number, null);
	}

	public List<Participant> getAllParticipants () {
		List<Participant> participants = new ArrayList<Participant>();

		Cursor cursor = db.query(MySQLiteHelper.TABLE_PARTICIPANTS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Participant participant = cursorToParticipant(cursor);
			participants.add(participant);
			cursor.moveToNext();
		}

		cursor.close();
		return participants;
	}

	private Participant cursorToParticipant(Cursor cursor) {
		Participant participant = new Participant();
		participant.setNumber(cursor.getString(0));
		participant.setStart_date(cursor.getLong(1));
		return participant;
	}
	
}
