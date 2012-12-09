/*
 * Copyright (C) 2012 Ben Bedwell
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */

package uk.ac.horizon.pszbb.scheduledtexter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScheduledTexterActivity extends Activity {

	private static ParticipantsDataSource pds;
	private static SmsManager smsManager;
	private static Handler h;
	private static TextView tv;
	private static WakeLock wl;

	private static final long ONE_DAY = 24 * 60 * 60 * 1000;
	private static final long STUDY_LENGTH = ONE_DAY * 21;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "scheduledtexter");
		wl.acquire();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		tv = (TextView) findViewById(R.id.tv);

		pds = new ParticipantsDataSource(this);
		pds.open();
		
		runOnUiThread(new toScreen(pds.getAllParticipants().size()
				+ " participant details loaded"));

		smsManager = SmsManager.getDefault();

		h = new Handler();

		Button endButton = (Button) findViewById(R.id.end_button);
		endButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				wl.release();
				finish();
			}

		});

		Button forceButton = (Button) findViewById(R.id.day);
		forceButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				runOnUiThread(new toScreen("Forcing text"));
				new Thread(new SendTextRunnable(false)).start();
			}

		});

		new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new toScreen("Scheduling night SMS"));
				long night = Utility.D2MS(21, 5) - System.currentTimeMillis();
				h.postDelayed(new SendTextRunnable(true), night);
				SystemClock.sleep(500);
				runOnUiThread(new toScreen("Scheduling day SMS 1"));
				long day = Utility.D2MS(9, 5) - System.currentTimeMillis();
				h.postDelayed(new SendTextRunnable(true), day);
				SystemClock.sleep(500);
				runOnUiThread(new toScreen("Scheduling day SMS 2"));
				day = Utility.D2MS(12, 5) - System.currentTimeMillis();
				h.postDelayed(new SendTextRunnable(true), day);
				SystemClock.sleep(500);
				runOnUiThread(new toScreen("Scheduling day SMS 3"));
				day = Utility.D2MS(15, 5) - System.currentTimeMillis();
				h.postDelayed(new SendTextRunnable(true), day);
				SystemClock.sleep(500);
				runOnUiThread(new toScreen("Scheduling day SMS 4"));
				day = Utility.D2MS(18, 5) - System.currentTimeMillis();
				h.postDelayed(new SendTextRunnable(true), day);
			}
		}).start();
	}

	public class toScreen implements Runnable {

		private String text;

		public toScreen(String text) {
			this.text = text;
		}

		public void run() {
			if (tv != null) {
				tv.setText(text + "\n" + tv.getText());
			}
		}

	}

	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}

	private class SendTextRunnable implements Runnable {

		private boolean scheduleNext = false;

		public SendTextRunnable(boolean scheduleNext) {
			this.scheduleNext = scheduleNext;
		}

		public void run() {
			for (Participant participant : pds.getAllParticipants()) {
				if (participant.getStart_date() < System.currentTimeMillis()) {
					if (participant.getStart_date() + STUDY_LENGTH > System
							.currentTimeMillis()) {
						String link = Utility.getLink();
						smsManager
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
						runOnUiThread(new toScreen("Sent " + link + " to "
								+ participant.getNumber()));
						SystemClock.sleep(1000);
					} else {
						runOnUiThread(new toScreen("Participant "
								+ participant.getNumber() + " finished study"));
					}
				} else {
					runOnUiThread(new toScreen("Participant "
							+ participant.getNumber()
							+ " not ready to start study"));
				}
			}

			runOnUiThread(new toScreen("Hour " + Utility.getHourOfDay()
					+ " SMS sent to all participants"));

			if (scheduleNext) {
				h.postDelayed(new SendTextRunnable(scheduleNext), ONE_DAY);
				runOnUiThread(new toScreen("Scheduled next SMS for "
						+ ((int) Math.round(ONE_DAY / 1000 / 60))
						+ " minutes in the future"));
			}
		}
	}

}