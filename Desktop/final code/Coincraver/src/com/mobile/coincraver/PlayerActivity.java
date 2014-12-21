/**
 *Android Application/game: Coincraver
 *
 */

package com.mobile.coincraver;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * 
 * This PlayerActivity is responsible for drawing the player, updating the
 * player logic, and collision will be detected here. This is the collision
 * between player and hole.
 * 
 * @author jpratik
 *
 */

class PlayerActivity {

	float x, y, w, h, vy;
	boolean jumping, falling;

	/*
	 * The specification of the character's position coordinates. StartX
	 * specifies the X position of the character and StartY specifies the Y
	 * position of the character. (0,0) coordinate is at the left top of the
	 * screen. "initialVelocity" is the velocity with which the character will
	 * begin to jump.
	 */

	final float startX = 380.0f, startY = 347.5f;
	final float initialVelocity = 16.0f;

	GameActivity game;

	/*
	 * RectF holds four float coordinates for a rectangle. The rectangle is
	 * represented by the coordinates of its 4 edges (left, top, right bottom).
	 * Under this dimensions, the character image will be drawn.
	 */

	RectF rect;

	/*
	 * CurFrame variable takes care of the next frame(image) to be drawn on the
	 * SurfaceView. This takes help of the curFrameTime varibale to determine
	 * the time for the next image display.
	 */

	int curFrame;
	long curFrameTime = 0;

	public PlayerActivity(GameActivity game) {
		this.game = game;

		this.rect = new RectF();

		w = game.playerJumpImage.getWidth();
		h = game.playerJumpImage.getHeight();

		reset();
	}

	/*
	 * reset() method resets player attributes back to the their original
	 * states.
	 */

	public void reset() {

		jumping = false;
		falling = false;

		x = startX;
		y = startY;

		rect.left = x;
		rect.top = y;
		rect.bottom = y + h;
		rect.right = x + w;

		curFrame = 0;
		curFrameTime = System.currentTimeMillis();
	}

	/*
	 * update() handles the curFrame to be updated on the SurfaceView. It
	 * checks: If falling is true, call PlayerActvity's doPlayerFall(), else if
	 * false, call doPlayerJump(). A check for tapping by seeing if the player
	 * is tapping while not currently jumping or falling and if this is the
	 * case, call PlayerActvity's startPlayerJump() and play the jump sound.
	 */

	public void update() {

		doCollisionDetection();

		if (falling) {
			doPlayerFall();
		}

		if (jumping) {
			doPlayerJump();
		}

		if (game.playerTap && !jumping && !falling) {
			startPlayerJump();
			game.soundPool.play(game.PlayerJumpSnd, 1.0f, 1.0f, 0, 0, 1.0f);
		}

		long now;

		now = System.currentTimeMillis() - curFrameTime;

		if (now > 180) {
			curFrame++;
			if (curFrame > 3) {
				curFrame = 1;
			}
			curFrameTime = System.currentTimeMillis();
		}

	}

	/*
	 * draw() : Check if the player is jumping or falling, and if so, draw the
	 * character to look in a certain way
	 */

	public void draw(Canvas canvas) {

		if (jumping || falling) {
			canvas.drawBitmap(game.playerJumpImage, x, y, game.clearPaint);
		} else {
			canvas.drawBitmap(game.playerImages[curFrame], x, y,
					game.clearPaint);
		}
	}

	private void doCollisionDetection() {

		float ey = y + h;

		for (Hole h : game.holes) {
			if (!h.alive) {
				continue;
			}

			float lx = x;
			float rx = x + w;

			/*
			 * Condition 1: Is player over the hole? Condition 2: Is player
			 * still inside the hole? Condition 3: Has player fallen into the
			 * hole?
			 */

			if ((h.x < lx) && ((h.x + h.w) > rx) && (h.y <= ey)) {
				game.initGameOver();
			}
		}

		/*
		 * Collision will be detected here. The intersect() method of RectF
		 * class checks whether the rectangle intersects with another rectangle.
		 */

		rect.left = x;
		rect.top = y;
		rect.bottom = y + h;
		rect.right = x + w;

		if (game.coin.alive && rect.intersect(game.coin.rect)) {
			game.doPlayerGrabCoin();
		}
	}

	/*
	 * doPlayerFall() : Uses physics algorithm to increase vy by 0.1f and then
	 * adding that to y which is the y direction of the player.
	 */

	private void doPlayerFall() {
		vy += 1.0f;
		y += vy;
		float tmpY = y + h;
		if (tmpY > game.groundY) {
			y = startY;
			falling = false;
		}
	}

	/*
	 * doPlayerJump() : It decreases y by vy and then decreasing vy by 1.0f.
	 * Finally, makes jumping and falling as false for the player will be on the
	 * ground now.
	 */

	private void doPlayerJump() {
		y -= vy;
		vy -= 1.0f;
		if (vy <= 0.0f) {
			jumping = false;
			falling = true;
		}
	}

	/*
	 * startPlayerJump(): Sets the jumping as true and player starts to jump
	 * with the initial velocity.
	 */

	private void startPlayerJump() {
		jumping = true;
		game.playerTap = false;
		vy = initialVelocity;
	}

	/*
	 * restore(): restores game states when the Coincraver is paused or left.
	 * Uses SharedPreferences to restore persistant data that is stored.
	 * SharedPreferences get*() to get certain data types like Float, or Boolean
	 * and save them.
	 */

	public void restore(SharedPreferences savedState) {
		x = savedState.getFloat("player_x", 0);
		y = savedState.getFloat("player_y", 0);
		vy = savedState.getFloat("player_vy", 0);
		jumping = savedState.getBoolean("player_jumping", false);
		falling = savedState.getBoolean("player_falling", false);
		curFrame = savedState.getInt("player_curFrame", 0);
		curFrameTime = savedState.getLong("player_curFrameTime", 0);
	}

	/*
	 * save(): saves game states and data when the game is paused or left. Uses
	 * SharedPreferences to save data that we want to preserve like position of
	 * the player, velocity, curFrame displayed & curFrameTime.SharedPreferences
	 * put*() to insert certain data types like Float, or Boolean and save them.
	 */

	public void save(SharedPreferences.Editor map) {
		map.putFloat("player_x", x);
		map.putFloat("player_y", y);
		map.putFloat("player_vy", vy);
		map.putBoolean("player_jumping", jumping);
		map.putBoolean("player_falling", falling);
		map.putInt("player_curFrame", curFrame);
		map.putLong("player_curFrameTime", curFrameTime);
	}
}
