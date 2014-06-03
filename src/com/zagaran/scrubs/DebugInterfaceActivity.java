package com.zagaran.scrubs;
 
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zagaran.scrubs.CSVFileManager;
import com.zagaran.scrubs.BackgroundProcess;

public class DebugInterfaceActivity extends Activity {
	
	CSVFileManager logFile = null;
	Context appContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_interface);
		appContext = this.getApplicationContext();
		
		//start logger
		CSVFileManager.startFileManager(this.getApplicationContext());
		logFile = CSVFileManager.getDebugLogFile();
		
		//start background service
		Intent backgroundProcess = new Intent(this, BackgroundProcess.class);
		appContext.startService(backgroundProcess);
		
		//TODO: move to background service
		startScreenOnOffListener();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debug_interface, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private void startScreenOnOffListener() {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		final BroadcastReceiver mReceiver = new ScreenOnOffListener();
		registerReceiver(mReceiver, filter);
	}
	
	
	public void printInternalLog(View view) {
		Log.i("print log button pressed", "press.");
		String log = logFile.read();
		Log.i("logfile", log);
	}


	public void clearInternalLog(View view) {
		Log.i("clear log button pressed", "poke.");
		logFile.deleteMeSafely();
	}

	
	public void goToAudioRecorder(View view) {
		Intent audioRecorderIntent = new Intent(this, AudioRecorderActivity.class);
		startActivity(audioRecorderIntent);
	}
}
