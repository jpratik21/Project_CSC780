/**
 *Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import com.mobile.coincraver.TyperViewActivity;

/**
 * This StartActivity is responsible for displaying the first screen of
 * Coincraver. This makes use of an explicit intent to call the MenuActivity
 * where an user will get the options to move further into the game, like Start
 * Game, Settings and About. There is an use of "immersive mode" in this
 * activity to achieve full screen display and it hides the navigation buttons
 * too. Also, Mediaplayer class is used to play the background music.
 * 
 * @author jpratik
 *
 */

public class StartActivity extends Activity {

	MediaPlayer logoMusic;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		setContentView(R.layout.activity_start);

		/* calling animateText() method of TyperViewActivity */
		final TyperViewActivity typer = (TyperViewActivity) findViewById(R.id.typeText);
		typer.animateText(getString(R.string.animate_coincraver));

		logoMusic = MediaPlayer.create(StartActivity.this,
				R.raw.launch_coincraver);
		logoMusic.start();

		/*
		 * A seperate thread is created for calling the MenuActivity. There is a
		 * sleep of 4000ms for the thread, which makes sure that the entire text
		 * animation is visible to the user. This animation is written for the
		 * text "Coincraver" in the TyperViewActivity.
		 */

		Thread logoTimer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(4000);
					Intent i = new Intent(StartActivity.this,
							MenuActivity.class);
					startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					finish();
				}
			}
		};
		logoTimer.start();
	}

	/*
	 * onPause() calls MediaPlayer's release() method. It releases resources
	 * (memory) of the MediaPlayer object (in this case, logoMusic). According
	 * to Android API, it is a good practice to call this whenever our app is
	 * not using or is done with using the MediaPlayer object.
	 */

	@Override
	protected void onPause() {
		super.onPause();
		logoMusic.release();
	}
}
