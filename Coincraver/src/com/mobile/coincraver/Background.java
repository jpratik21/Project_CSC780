package com.mobile.coincraver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.*;
import android.util.Log;

public class Background {
	
	GameActivity game;
	

	Background(GameActivity game) {
		this.game = game;
	}

	public void update() {
	    
	}

	public void draw(Canvas canvas) {
	    canvas.drawBitmap(game.backgroundImage[0], 0 , 0, null);
	}
	
}
