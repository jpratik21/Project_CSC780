/**
 * Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.content.SharedPreferences;
import android.graphics.Canvas;

/**
 * HoleActivity is responsible for drawing the holes in the way. The boolean
 * value of alive decides whether to draw the hole on the screen or not.
 * 
 * @author jpratik
 *
 */

class Hole {

	int id;
	float x, y, w, h;
	boolean alive;

	GameActivity game;

	/*
	 * The boolean alive is set to false when the reset() method of this
	 * HoleActivity is called.
	 */

	public void reset() {
		alive = false;
	}

	/*
	 * Spawn() method takes care of creating the new hole starting beyond right
	 * side of the display. Also, apply additional xOffset and vary the width of
	 * the pothole
	 */

	public void spawn(float xOffset) {

		w = game.random(game.MIN_HOLE_WIDTH, game.MAX_HOLE_WIDTH);
		x = game.width + w + xOffset;
		alive = true;
	}

	/*
	 * The update() method makes sure that it decrease the x value for the image
	 * everytime while it spawns a new hole. This creates the animation of hole
	 * being moved from right to left.
	 */

	public void update() {

		x -= 10.0f;

		/*
		 * if the hole goes beyond the left hand side of the display then make
		 * the alive as false to disable it.
		 */

		if (x < -w) {
			alive = false;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(x, y, x + w, y + h, game.clearPaint);
	}

	public Hole(int id, GameActivity game) {
		this.id = id;
		this.game = game;
		y = game.groundY;
		h = game.roadImage.getHeight();
		alive = false;
	}

	public void restore(SharedPreferences savedState) {
		x = savedState.getFloat("ph_" + id + "_x", 0);
		y = savedState.getFloat("ph_" + id + "_y", 0);
		w = savedState.getFloat("ph_" + id + "_w", 0);
		h = savedState.getFloat("ph_" + id + "_h", 0);
		alive = savedState.getBoolean("ph_" + id + "_alive", false);
	}

	public void save(SharedPreferences.Editor map) {
		map.putFloat("ph_" + id + "_x", x);
		map.putFloat("ph_" + id + "_y", y);
		map.putFloat("ph_" + id + "_w", w);
		map.putFloat("ph_" + id + "_h", h);
		map.putBoolean("ph_" + id + "_alive", alive);
	}
}
