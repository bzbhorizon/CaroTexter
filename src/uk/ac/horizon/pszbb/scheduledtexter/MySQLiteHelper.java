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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_PARTICIPANTS = "participants";
	public static final String COLUMN_NUMBER = "_number";
	public static final String COLUMN_START_DATE = "start_date";

	private static final String DATABASE_NAME = "participants.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PARTICIPANTS + "(" + COLUMN_NUMBER
			+ " string primary key, " + COLUMN_START_DATE
			+ " real not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		
		List<Participant> initialParticipants = new ArrayList<Participant>();
		
		// fake participant
		initialParticipants.add(new Participant("01234567890", Utility.D2MS(7, 9, 0, 0))); // a fake participant who'll receive texts starting on 7th September this year
		
		for (Participant participant : initialParticipants) {
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_NUMBER, participant.getNumber());
			values.put(MySQLiteHelper.COLUMN_START_DATE, participant.getStart_date());
			db.insert(MySQLiteHelper.TABLE_PARTICIPANTS, null, values);
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
		onCreate(db);
	}

}
