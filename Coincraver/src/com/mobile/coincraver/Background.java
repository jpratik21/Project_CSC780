/**
 * Android Application
 */

package com.mobile.coincraver;

import android.graphics.Canvas;

/*
 * Background class takes care of drawing the backgroud image on to the main
 * game screen. Canvas class is used to draw the background.
 * 
 */

public class Background {

	GameActivity game;

	Background(GameActivity game) {
		this.game = game;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(game.backgroundImage[0], 0, 0, null);
	}

}
