<<<<<<< HEAD
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
=======
package com.mobile.coincraver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.*;
import android.util.Log;

public class Background {
	
	GameActivity game;
	
>>>>>>> 9f06e6baa6c5a7855e81863eb380617dd7fa7e75

	Background(GameActivity game) {
		this.game = game;
	}

<<<<<<< HEAD
	public void draw(Canvas canvas) {
		canvas.drawBitmap(game.backgroundImage[0], 0, 0, null);
	}

=======
	public void update() {
	    
	}

	public void draw(Canvas canvas) {
	    canvas.drawBitmap(game.backgroundImage[0], 0 , 0, null);
	}
	
>>>>>>> 9f06e6baa6c5a7855e81863eb380617dd7fa7e75
}
