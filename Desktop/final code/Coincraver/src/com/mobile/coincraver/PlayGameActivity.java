/**
 * Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.mobile.coincraver.GameViewActivity.PlayerMoveThread;

/**
 * This PlayGameActivity is invoked after the press of Start Game button in the
 * MenuActivity. This uses the SharedPreferences class to save the state of
 * Coincraver(it is like SQLite database which is used for storing considerably
 * small amound of data). Here, isfinishing() method provided by the Activity
 * class plays a major role triggering the appropriate method on detection of
 * user intention to close the current activity. This activity implements
 * SensorEventListener for accelerometer.
 * 
 * @author jpratik
 *
 */

public class PlayGameActivity extends Activity implements SensorEventListener {

	public static final String PREFS_NAME = "track_pref";

	private SensorManager sensorManager;
	private long lastUpdate;

	GameViewActivity cView;
	PlayerMoveThread cThread;

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameplay);

		/*
		 * Sets up the immersive mode which allows the device to get true full
		 * screen ability. Uses setSystemUiVisibitty() to hide the system bars
		 * and navigation buttons.
		 */

		getWindow().getDecorView()
				.setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE
								| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
								| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
								| View.INVISIBLE);

		cView = (GameViewActivity) findViewById(R.id.playermove);

		/*
		 * Initialising the sensor's lastUpdate varibale and the sensor manager
		 * to access the sensor.
		 */

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();

		ImageView img = (ImageView) findViewById(R.id.backplay);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent backintent = new Intent(PlayGameActivity.this,
						MenuActivity.class);
				startActivity(backintent);
			}
		});

	}

	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		cThread = cView.getThread();

		if (isFinishing()) {
			cThread.resetGame();
			System.exit(0);
		} else {
			cThread.pause();
		}

		/*
		 * While the activity is in pause mode, we do not need the sensor. So,
		 * calling the unregisterListener() to serve our purpose. It is a good
		 * practice.
		 */

		sensorManager.unregisterListener(this);

		cThread.saveGame(editor);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cThread = cView.getThread();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		/*
		 * Registering this class as a listener for the orientation and
		 * accelerometer sensor. This is necessary in order to use
		 * Accelerometer.
		 */

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		cThread.restoreGame(settings);
	}

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");
		Intent setIntent = new Intent(PlayGameActivity.this, MenuActivity.class);
		startActivity(setIntent);
	}

	/*
	 * Gets the values for accelerometer. It also checks whether values go past
	 * a certain mark. If they do so, fire some event.
	 */

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = event.timestamp;

		if (accelationSquareRoot >= 2) //
		{
			if (actualTime - lastUpdate < 400) {
				return;
			}
			lastUpdate = actualTime;
			cThread.doAccelerometerEvent();

		}
	}

	// Fire event
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}