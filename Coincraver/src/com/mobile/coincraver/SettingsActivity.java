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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This SettingsActivity has a checkbox which on check activates the music for
 * Coincraver. This also has a button which on click displays the basic controls
 * of the game. It makes use of the TextView to show the controls.
 * 
 * @author jpratik
 *
 */

public class SettingsActivity extends Activity {

	private CheckBox activate_music;

	MediaPlayer gameplay_music;

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

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

		/*
		 * Calling the checkbox listener to see if the user has checked the
		 * checkedbox so that it can activate the music.
		 */

		ListenerOnCheckActivateMusic();

		/*
		 * Setting up the OnClickListener for the control button in
		 * SettingsActivity. This calls setText() method of TextView.
		 */

		final TextView controls_text = (TextView) findViewById(R.id.show_controls);
		Button controls_button = (Button) findViewById(R.id.control_button);

		controls_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				controls_text.setText(R.string.ControlDisplay);

			}
		});

		ImageView img = (ImageView) findViewById(R.id.backsettings);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent backintent = new Intent(SettingsActivity.this,
						MenuActivity.class);
				startActivity(backintent);
			}
		});
	}

	public void ListenerOnCheckActivateMusic() {
		gameplay_music = MediaPlayer.create(SettingsActivity.this,
				R.raw.game_play);
		activate_music = (CheckBox) findViewById(R.id.music_check);
		activate_music
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (activate_music.isChecked()) {
							gameplay_music.setLooping(true);
							gameplay_music.start();
						} else {
							gameplay_music.stop();
						}
					}
				});
	}
}
