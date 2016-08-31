package com.spreadtrum.iit.zpayapp.Log;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class LogTestActivity extends Activity {

	private LogWriter mLogWriter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);

		File logf = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "DemoLog.txt");

		try {
			mLogWriter = LogWriter.open(logf.getAbsolutePath());
		} catch (IOException e) {

		}

		log("onCreate()");
	}

	public void log(String msg) {
		Log.d("---test---", msg);

		try {
			mLogWriter.print(LogTestActivity.class, msg);
		} catch (IOException e) {

		}
	}
}
