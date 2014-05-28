package com.dhara.googlecalendartrial;

import java.lang.Thread.UncaughtExceptionHandler;

import com.dhara.googlecalendartrial.MyApplication.TrackerName;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity{
	private UncaughtExceptionHandler myHandler;
	private Tracker tracker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = ((MyApplication)getApplication()).getTracker(TrackerName.GLOBAL_TRACKER);
		myHandler = new ExceptionReporter(tracker, 
				Thread.getDefaultUncaughtExceptionHandler(), 
				(MyApplication)getApplication());
		Thread.setDefaultUncaughtExceptionHandler(myHandler);
	}
}
