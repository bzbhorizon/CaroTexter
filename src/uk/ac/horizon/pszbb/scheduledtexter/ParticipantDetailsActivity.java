/*
 * Copyright (C) 2012 Ben Bedwell
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */

package uk.ac.horizon.pszbb.scheduledtexter;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class ParticipantDetailsActivity extends ListActivity {

	private static ParticipantsDataSource pds;
	private static WakeLock wl;
	private static ArrayAdapter<Participant> adapter;
	private Dialog numberDialog;
	private static String newNumber;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.participants);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"participantdetails");
		wl.acquire();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		pds = new ParticipantsDataSource(this);
		pds.open();

		adapter = new ArrayAdapter<Participant>(this,
				android.R.layout.simple_list_item_1, pds.getAllParticipants());
		setListAdapter(adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Participant participant = (Participant) getListAdapter()
						.getItem(position);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ParticipantDetailsActivity.this);
				builder.setMessage("What do you want to do?")
						.setCancelable(true)
						.setPositiveButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								})
						.setNeutralButton("Force",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										new Thread(new SendSingleTextRunnable(participant)).start();
										dialog.cancel();
									}
								})
						.setNegativeButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										pds.deleteParticipant(participant);
										adapter.remove(participant);
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	@SuppressLint({ "NewApi", "NewApi", "NewApi" })
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Participant> adapter = (ArrayAdapter<Participant>) getListAdapter();
		switch (view.getId()) {
		case R.id.add:
			numberDialog = new Dialog(this);

			numberDialog.setContentView(R.layout.number);
			numberDialog.setTitle("Phone number");
			
			Button continueButton = (Button) numberDialog.findViewById(R.id.continueButton);
			continueButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					EditText number = (EditText) numberDialog.findViewById(R.id.editNumber);
					newNumber = number.getEditableText().toString();
					
					if (newNumber.length() == 11) {
						DialogFragment newFragment = new DatePickerFragment();
					    newFragment.show(getFragmentManager(), "datePicker");
					    
					    numberDialog.dismiss();
					}
				}
			});
			
			numberDialog.show();
			break;
		}
		adapter.notifyDataSetChanged();
	}

	protected void onResume() {
		pds.open();
		super.onResume();
	}

	protected void onPause() {
		pds.close();
		super.onPause();
	}

	protected void onDestroy() {
		pds.close();
		super.onDestroy();
		System.exit(0);
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public static class DatePickerFragment extends DialogFragment implements
			OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		// this always tries to add two identical participants for some reason. No idea why.
		public void onDateSet(DatePicker view, int year, int month, int day) {
			 Participant participant = pds.createParticipant(newNumber, Utility.D2MS(month, day, 0, 0));
			 if (participant != null) {
				 adapter.add(participant);
			 }
		}
	}
	
	private class SendSingleTextRunnable implements Runnable {
		
		private Participant participant;
		
		public SendSingleTextRunnable (Participant participant) {
			this.participant = participant;
		}

		public void run() {
						String link = Utility.getLink();
						SmsManager.getDefault()
								.sendTextMessage(
										participant.getNumber(),
										null,
										"This is your new diary link. Pls fill it in before "
												+ Utility.getCompleteBy()
												+ ": https://dl.qualtrics.com/SE/?SID="
												+ link
												+ "&pId="
												+ participant.getNumber()
												+ "&hour=" + Utility.getHourOfDay(),
										null, null);
		}
	}

}
