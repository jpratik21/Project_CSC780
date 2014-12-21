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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This MenuActivity is invoked from the explicit intent specified in
 * StarActivity. This has a cyclic & linear animation which are achieved with
 * the help of cycle_effect.xml & linear_effect defined in the res/anim folder
 * of the Coincraver project. Cycle_effect.xml uses a <rotate> tag that can be
 * used to specify the attributes for a particular rotation. Similarly,
 * linear_effect.xml uses a <scale> tag that can be used to specify the
 * attributes for a particular linear displacement of the text. Also, this
 * activity has ButtonClickListeners for Start Game, Settings and About button
 * that takes you to the corresponding activity.
 * 
 * @author jpratik
 *
 */

public class MenuActivity extends Activity {

	MediaPlayer background_music;
	Animation cycle_animate, linear_animate;
	TextView linear_Text;
	ImageView cycle_image;
	int counttap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/*
		 * Sets up the immersive mode which allows the device to get true full
		 * screen ability. Uses setSystemUiVisibitty() to hide the system bars
		 * and navigation buttons
		 */

		getWindow().getDecorView()
				.setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE
								| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
								| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_FULLSCREEN
								| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
								| View.INVISIBLE);

		setContentView(R.layout.activity_menu);

		/*
		 * MediaPlayer class is used to start the background music for
		 * Coincraver. The setLooping() method of MediaPlayer class makes sure
		 * that the music is played continuously without an interruption.
		 * Finally, start() method starts the music (music file should be in the
		 * res/raw folder)
		 */

		background_music = MediaPlayer.create(MenuActivity.this,
				R.raw.game_menu);
		background_music.setLooping(true);
		background_music.start();

		/*
		 * Making use of LinearInterpolator class to give linear effect to the
		 * main title "Coincraver". The class specification is in
		 * res/anim/linear_effect.xml.
		 */

		linear_Text = (TextView) findViewById(R.id.main_title);
		linear_animate = AnimationUtils.loadAnimation(this,
				R.anim.linear_effect);

		linear_Text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				linear_Text.startAnimation(linear_animate);
			}
		});

		/*
		 * Making use of CycleInterpolator class to give cyclic effect to the
		 * "Coincraver" image. The class specification is in
		 * res/anim/cycle_effect.xml.
		 */

		cycle_image = (ImageView) findViewById(R.id.cycleimg);
		cycle_image.setImageResource(R.drawable.cycleimg);
		cycle_animate = AnimationUtils.loadAnimation(this, R.anim.cycle_effect);

		cycle_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				counttap += 1;
				// start animation at first tap
				if (counttap == 1) {
					cycle_image.startAnimation(cycle_animate);
				}
				// stop animation at second tap
				else if (counttap == 2) {
					cycle_image.setAnimation(null);
					counttap = 0;
				}
			}
		});

		final MediaPlayer btnSound = MediaPlayer.create(MenuActivity.this,
				R.raw.button_click);
		final Animation animRotate = AnimationUtils.loadAnimation(this,
				R.anim.button_rotate);

		/*
		 * Start Game, Settings and About buttons with rotate effect. This is
		 * achieved with the help of res/anim/button_rotate.xml
		 */

		Button start_game = (Button) findViewById(R.id.start_game_button);
		start_game.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(animRotate);
				Intent intent1 = new Intent(MenuActivity.this,
						PlayGameActivity.class);
				btnSound.start();
				startActivity(intent1);

			}
		});

		Button settings_button = (Button) findViewById(R.id.settings_button);
		settings_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(animRotate);
				Intent intent2 = new Intent(MenuActivity.this,
						SettingsActivity.class);
				btnSound.start();
				startActivity(intent2);
			}
		});

		Button about_button = (Button) findViewById(R.id.about_button);
		about_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(animRotate);
				Intent intent3 = new Intent(MenuActivity.this,
						AboutActivity.class);
				btnSound.start();
				startActivity(intent3);
			}
		});

		ImageView img = (ImageView) findViewById(R.id.backmain);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (background_music != null) {
			background_music.stop();
			if (isFinishing()) {
				background_music.stop();
				background_music.release();
			}
		}
	}

	@Override
	protected void onResume() {
		if (background_music != null && !background_music.isPlaying())
			background_music.start();
		super.onResume();
	}

	/*
	 * onCreateOptionsMenu() : calls super with androids Menu class. A
	 * MenuInflater object is made to instiate menu xml into Menu Objects. We
	 * call MenuInflaters inflate() in context of our menu xml, and the Menu
	 * interface to handle the menu items.
	 */

	public boolean onCreateOptionsMenu(MenuActivity menu) {
		super.onCreateOptionsMenu((android.view.Menu) menu);
		MenuInflater awesome = getMenuInflater();
		awesome.inflate(R.menu.main, (android.view.Menu) menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		}
		return false;
	}

	/*
	 * onBackPressed() : Handles when the back button is pressed on the
	 * MenuActvity. Sets an intent to ACTION_MAIN which is defined in the
	 * AndroidManifest.xml. ACTION_MAIN is the entry point of the app. We call
	 * Intents addCategory() to set CATEGORY_HOME to define Intent home as the
	 * first activity to be displayed. setFlags() allows us to set flags on our
	 * intent. We use FLAG_ACTIVITY_NEW_TASK so that when the back button is
	 * pressed, we do not end up at the Home screen. FLAG_ACTIVITY_NEW_TASK
	 * starts the intent as a new task, since the previous one had the Home
	 * screen on its backstack.
	 */

	@Override
	public void onBackPressed() {

		Intent home = new Intent(Intent.ACTION_MAIN);
		home.addCategory(Intent.CATEGORY_HOME);
		home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(home);
	}
}
