/**
 * Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

/**
 * This AboutActivity is responsible for displaying the general information
 * about Coincraver. The default view of this activity is the world created by
 * the BallWorldActivity that extends view. You guys can reach us at
 * coincraver@gmail.com for any concern/question.
 * 
 * @author jpratik
 *
 */

public class AboutActivity extends Activity {

	BallWorldActivity mWorld;
	private Handler mHandler;

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * DisplayMetrics class gives general information about the display like
		 * its size, density, etc. Then, this information can be used to get the
		 * absolute values for the width and height of display.
		 */

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mWorld = new BallWorldActivity(this, dm.widthPixels, dm.heightPixels);

		this.setContentView(this.mWorld);

		/*
		 * Add the 12 balls with the help of addBall() method declared in the
		 * BallWorldActivity.
		 */
		for (int i = 0; i < 12; i++) {
			mWorld.addBall();
		}

		/*
		 * The regular update is done with the help of handler. The post()
		 * method takes "update" as a argument which is the Runnable object. The
		 * run() method of runnable update calls update() method of
		 * BallWorldActivity.
		 */
		mHandler = new Handler();
		mHandler.post(update);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(update);
	}

	private Runnable update = new Runnable() {
		@Override
		public void run() {
			mWorld.update();
			mHandler.postDelayed(update, (long) (10 / mWorld.timeStep));
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/*
	 * onBackPressed() method takes back to the MenuActivity. This is done with
	 * the help of explicit intent where intent declaration has 1st parameter as
	 * the context of current class(here, AboutActivity) and 2nd parameter as
	 * the Activity which you want to call(here, MenuActivity)
	 */

	@Override
	public void onBackPressed() {
		Intent home = new Intent(AboutActivity.this, MenuActivity.class);
		startActivity(home);
	}
}