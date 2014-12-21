/**
 *Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("check");
		editor.commit();

		final TextView controls_text = (TextView) findViewById(R.id.show_controls);

		addListenerOnButton();

		final MediaPlayer mpButtonClick = MediaPlayer.create(this,
				R.raw.game_play);

		
				controls_text.setText(R.string.ControlDisplay);

		ImageView img = (ImageView) findViewById(R.id.backsettings);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent backintent = new Intent(SettingsActivity.this,
						MenuActivity.class);
				startActivity(backintent);
			}
		});

		final Button BackButton = (Button) findViewById(R.id.back);
		BackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BackButton.setVisibility(View.GONE);
				activate_music.setVisibility(View.VISIBLE);
				activate_music.setChecked(true);
				mpButtonClick.start();
				mpButtonClick.setLooping(true);
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		save(activate_music.isChecked());

	}

	@Override
	public void onResume() {
		super.onResume();
		activate_music.setChecked(load());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("check");
		editor.commit();
	}

	private void save(final boolean isChecked) {
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("check", isChecked);
		editor.commit();
	}

	private boolean load() {
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean("check", true);
	}

	public void addListenerOnButton() {
		activate_music = (CheckBox) findViewById(R.id.music_check);
		activate_music.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					volumeControl.setStreamMute(AudioManager.STREAM_MUSIC,
							false);
					int maxVolume = volumeControl
							.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					volumeControl.setStreamVolume(AudioManager.STREAM_MUSIC,
							maxVolume, maxVolume);

					Toast.makeText(SettingsActivity.this, "Music enabled",
							Toast.LENGTH_LONG).show();
				} else if (((CheckBox) v).isChecked() == (false)) {
					AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					volumeControl
							.setStreamMute(AudioManager.STREAM_MUSIC, true);
					Toast.makeText(SettingsActivity.this, "Music disabled",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}
}
