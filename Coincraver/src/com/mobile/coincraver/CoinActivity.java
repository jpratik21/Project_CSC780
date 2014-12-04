/**
 * Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * CoinActivity is responsible for drawing the coins in sky. This makes use of
 * the RecF class to hold the coordinates of the coin image to be drawn. The
 * boolean value decides whether to draw the coin on the screen or not.
 * 
 * @author jpratik
 *
 */

public class CoinActivity {

	float x, y, w, h;
	boolean alive;

	GameActivity game;
	RectF rect;

	public CoinActivity(GameActivity game) {

		this.game = game;

		w = game.coinImage.getWidth();
		h = game.coinImage.getHeight();

		rect = new RectF();
	}

	/*
	 * The boolean alive is set to false when the reset() method of this
	 * CoinActivity is called.
	 */

	public void reset() {
		alive = false;
	}

	/*
	 * Spawn() method takes care of creating the new coin with its coordinates
	 * calculated as below in the method.
	 */

	public void spawn() {
		x = game.width + w;
		y = game.groundY - h - game.random(h, 100.0f);
		rect.top = y;
		rect.bottom = y + h;
		alive = true;
	}

	/*
	 * The update() method makes sure that it decrease the x value for the image
	 * everytime while it spawns a new coin. This creates the animation of coin
	 * being moved from right to left.
	 */

	public void update() {

		x -= 10.0f;
		rect.left = x;
		rect.right = x + w;

		/*
		 * if the coin goes beyond the left hand side of the display then make
		 * the alive as false to disable it.
		 */

		if (x < -w) {
			alive = false;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(game.coinImage, rect.left, rect.top, game.clearPaint);
	}

	/*
	 * save() method saves the last coin spawned on the screen by storing its
	 * coordinates. It also stores the boolean value of alive. This is used by
	 * the restore method when the player returns back into the game.
	 */

	public void restore(SharedPreferences savedState) {
		x = savedState.getFloat("c_x", 0);
		y = savedState.getFloat("c_y", 0);
		w = savedState.getFloat("c_w", 0);
		h = savedState.getFloat("c_h", 0);
		alive = savedState.getBoolean("c_alive", false);
	}

	/*
	 * restore() method restores the coordinates of the last spawned coin along
	 * with its alive's boolean state.
	 */

	public void save(SharedPreferences.Editor map) {
		map.putFloat("c_x", x);
		map.putFloat("c_y", y);
		map.putFloat("c_w", w);
		map.putFloat("c_h", h);
		map.putBoolean("c_alive", alive);
	}
}
