/**
 *Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import java.util.Random;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * GameActivity is responsible for managing the coincraver objects. It keeps
 * track of the game state with the help of switch case and handles the game
 * logic.
 * 
 * @author jpratik
 *
 */

public class GameActivity {

	/*
	 * Manage the hole resources of coincraver. Hole[] stores the number of
	 * holes and there are variables that specifies the maximum and minimum
	 * width of the hole that can be spawned.
	 */

	final int MAX_holes = 10;
	float MIN_HOLE_WIDTH = 100.0f, MAX_HOLE_WIDTH = 210.0f;
	Hole[] holes;
	Hole lastHole;

	/*
	 * Different times to keep track of start game time, game pause time, game
	 * over time, tap time, and so on.
	 */

	long spawnHoleTime, tapToStartTime, getReadyGoTime, pauseStartTime;
	long gameOverTime, saveGameTime, scoreTime, spawnCoinTime;
	final long SPAWN_HOLE_TIME = 750, SPAWN_COIN_TIME = 150, SCORE_TIME = 100;

	boolean playerTap, showTapToStart;

	final int GAME_MENU = 0, GAME_READY = 1;
	final int GAME_PLAY = 2, GAME_OVER = 3;
	final int GAME_PAUSE = 4, SCORE_DEFAULT = 500;
	final int SCORE_INC = 5, SCORE_COIN_BONUS = 200;
	final int SHOW_GET_READY = 0, SHOW_GO = 1;
	int gameState, getReadyGoState, lastGameState;
	int highScore, curScore, height, width;

	/*
	 * Paint objects to be used by canvas class to draw the things. greenPaint
	 * is used for drawing the character images, clearPaint is used for clearing
	 * the stuff like creating the hole in the way of player, whitePaint is used
	 * for displaying the score and so on.
	 */
	Paint greenPaint, clearPaint, logoPaint;
	Paint whitePaint, emptyPaint;

	CoinActivity coin;
	RoadActivity road;

	Bitmap roadImage, dividerImage, coinImage, playerJumpImage;
	Bitmap[] playerImages;
	final int MAX_PLAYER_IMAGES = 4;

	SoundPool soundPool;
	int PlayerJumpSnd, PlayerGrabCoinSnd, PlayerCrashSnd;

	Random rng;

	PlayerActivity player;

	/*
	 * groundY, groundHeight variables are used by doPlayerFall() and
	 * doPlayerJump() methods of the PlayerActivity
	 */

	final float groundY = 400, groundHeight = 20;

	/*
	 * Managing the game resources which then, can be used to draw the things in
	 * Coincraver
	 */

	public GameActivity(Context context) {

		greenPaint = new Paint();
		greenPaint.setAntiAlias(true);
		greenPaint.setARGB(255, 0, 255, 0);
		greenPaint.setFakeBoldText(true);
		greenPaint.setTextSize(42.0f);

		clearPaint = new Paint();
		clearPaint.setARGB(255, 0, 0, 0);
		clearPaint.setAntiAlias(true);

		emptyPaint = new Paint();

		rng = new Random();

		loadImages(context);

		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		loadSounds(context);

		player = new PlayerActivity(this);

		holes = new Hole[MAX_holes];
		for (int i = 0; i < MAX_holes; i++) {
			holes[i] = new Hole(i, this);
		}

		whitePaint = new Paint();
		whitePaint.setAntiAlias(true);
		whitePaint.setARGB(255, 255, 255, 255);
		whitePaint.setFakeBoldText(true);
		whitePaint.setTextSize(55.0f);

		logoPaint = new Paint();
		logoPaint.setAntiAlias(true);
		logoPaint.setARGB(255, 0, 0, 255);
		logoPaint.setFakeBoldText(true);
		logoPaint.setTextSize(150f);

		coin = new CoinActivity(this);
		road = new RoadActivity(this);

		highScore = SCORE_DEFAULT;

		resetGame();
	}

	public void setScreenSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/*
	 * run() method has a switch case that handles the different game states
	 * like Game Menu, Game Ready, Game Play, Game Pause and so on.
	 */

	public void run(Canvas canvas) {
		switch (gameState) {
		case GAME_MENU:
			gameMenu(canvas);
			break;
		case GAME_READY:
			gameReady(canvas);
			break;
		case GAME_PLAY:
			gamePlay(canvas);
			break;
		case GAME_OVER:
			gameOver(canvas);
			break;
		case GAME_PAUSE:
			gamePause(canvas);
			break;
		}
	}

	public void doTouch() {
		playerTap = true;
	}

	/*
	 * resetGame() method is called when the player wants to reset the
	 * Coincraver. All the variables get reset here. For an instance, currrent
	 * score will go 0.
	 */

	public void resetGame() {
		tapToStartTime = System.currentTimeMillis();
		showTapToStart = true;

		playerTap = false;

		player.reset();

		spawnHoleTime = System.currentTimeMillis();
		for (Hole h : holes) {
			h.reset();
		}

		lastHole = null;

		gameState = GAME_MENU;
		lastGameState = gameState;

		getReadyGoState = SHOW_GET_READY;
		getReadyGoTime = 0;

		curScore = 0;

		coin.reset();
		spawnCoinTime = System.currentTimeMillis();

		road.reset();
	}

	/*
	 * initGameOver() and gameOver() will be called after the collision is
	 * detected between the player and hole.
	 */

	public void initGameOver() {

		gameState = GAME_OVER;
		gameOverTime = System.currentTimeMillis();

		soundPool.play(PlayerCrashSnd, 1.0f, 1.0f, 0, 0, 1.0f);

		if (curScore > highScore) {
			highScore = curScore;
		}
	}

	private void gameOver(Canvas canvas) {

		canvas.drawRect(0, 0, width, height, clearPaint);

		canvas.drawText("GAME OVER", width / 2.4f, height / 2, whitePaint);

		long now = System.currentTimeMillis() - gameOverTime;
		if (now > 2000) {
			resetGame();
		}
	}

	/*
	 * This is the first state which will be called when the player starts the
	 * game. It draws player, coin, road and holes in it.
	 */

	private void gamePlay(Canvas canvas) {
		canvas.drawRect(0, 0, width, height, clearPaint);
		road.update();
		road.draw(canvas);

		for (Hole h : holes) {
			if (h.alive) {
				h.update();
				h.draw(canvas);
			}
		}

		if (coin.alive) {
			coin.update();
			coin.draw(canvas);
		}

		player.update();
		player.draw(canvas);

		spawnHole();
		spawnCoin();

		doScore(canvas);
	}

	private void gameReady(Canvas canvas) {

		long now;
		canvas.drawRect(0, 0, width, height, clearPaint);

		switch (getReadyGoState) {
		case SHOW_GET_READY:
			canvas.drawText("GET READY", (width / 2.3f), height / 1.8f,
					whitePaint);
			now = System.currentTimeMillis() - getReadyGoTime;
			if (now > 2000) {
				getReadyGoTime = System.currentTimeMillis();
				getReadyGoState = SHOW_GO;
			}
			break;
		case SHOW_GO:
			canvas.drawText("GO!", (width / 2.1f), height / 1.8f, whitePaint);
			now = System.currentTimeMillis() - getReadyGoTime;
			if (now > 500) {
				gameState = GAME_PLAY;
				scoreTime = System.currentTimeMillis();
			}
			break;
		}

		canvas.drawText("SCORE: 0", width / 2.250f, 120, whitePaint);

		road.draw(canvas);
		player.draw(canvas);
	}

	private void gameMenu(Canvas canvas) {

		canvas.drawRect(0, 0, width, height, clearPaint);

		canvas.drawText("COINCRAVER", (width / 3.5f), 200.0f, logoPaint);
		canvas.drawText("TOP SCORE: " + highScore, (width / 2.55f),
				height / 2.7f, whitePaint);

		if (playerTap) {
			gameState = GAME_READY;
			playerTap = false;
			getReadyGoState = SHOW_GET_READY;
			getReadyGoTime = System.currentTimeMillis();

			holes[0].spawn(0);
			lastHole = holes[0];
		}

		long now = System.currentTimeMillis() - tapToStartTime;
		if (now > 550) {
			tapToStartTime = System.currentTimeMillis();
			showTapToStart = !showTapToStart;
		}

		if (showTapToStart) {
			canvas.drawText("TOUCH TO START", width / 2.55f, height - 450.0f,
					whitePaint);
		}
	}

	void spawnHole() {
		long now = System.currentTimeMillis() - spawnHoleTime;

		if (now > SPAWN_HOLE_TIME) {

			if ((int) random(10) > 2) {

				/*
				 * Find an available hole to use. So, check its alive value and
				 * then continue with the spawnning of hole.
				 */

				for (Hole h : holes) {

					if (h.alive) {
						continue;
					}

					float xOffset = 0.0f;

					/*
					 * If the last hole in Coincraver is alive then use its
					 * width to adjust the position of the new hole.
					 */

					if (lastHole.alive) {

						float tmp = lastHole.x + lastHole.w;

						if (tmp > width) {
							tmp = tmp - width;
							xOffset = tmp + random(10.0f);
						} else {
							tmp = width - tmp;
							if (tmp < 20.0f) {
								xOffset = tmp + random(10.0f);
							}
						}
					}

					h.spawn(xOffset);
					lastHole = h;
					break;
				}
			}

			spawnHoleTime = System.currentTimeMillis();
		}
	}

	private void loadSounds(Context context) {
		PlayerCrashSnd = soundPool.load(context, R.raw.playercrash, 1);
		PlayerGrabCoinSnd = soundPool.load(context, R.raw.grabcoin, 1);
		PlayerJumpSnd = soundPool.load(context, R.raw.playerjump, 1);
	}

	private void loadImages(Context context) {
		Resources res = context.getResources();

		roadImage = BitmapFactory.decodeResource(res, R.drawable.road_image);
		dividerImage = BitmapFactory.decodeResource(res, R.drawable.divider);

		coinImage = BitmapFactory.decodeResource(res, R.drawable.coin_image);

		playerJumpImage = BitmapFactory.decodeResource(res,
				R.drawable.playerjump);

		playerImages = new Bitmap[MAX_PLAYER_IMAGES];
		playerImages[0] = BitmapFactory.decodeResource(res, R.drawable.player0);
		playerImages[1] = BitmapFactory.decodeResource(res, R.drawable.player1);
		playerImages[2] = BitmapFactory.decodeResource(res, R.drawable.player2);
		playerImages[3] = BitmapFactory.decodeResource(res, R.drawable.player3);
	}

	/*
	 * gamePause() state is called when the player leaves the game. Coincraver
	 * makes sure that it stores the current state of the game so that it can
	 * restore after the player returns.
	 */

	private void gamePause(Canvas canvas) {

		canvas.drawRect(0, 0, width, height, clearPaint);

		canvas.drawText("GAME PAUSED", width / 2.4f, height / 2, whitePaint);

		if (playerTap) {
			playerTap = false;
			gameState = lastGameState;

			long deltaTime = System.currentTimeMillis() - pauseStartTime;

			spawnHoleTime += deltaTime;
			tapToStartTime += deltaTime;
			getReadyGoTime += deltaTime;
			gameOverTime += deltaTime;
			scoreTime += deltaTime;
			spawnHoleTime += deltaTime;
		}
	}

	public void pause() {

		if (gameState == GAME_PAUSE) {
			return;
		}

		lastGameState = gameState;
		gameState = GAME_PAUSE;
		pauseStartTime = System.currentTimeMillis();
	}

	/*
	 * Coin is spawned in a random manner. This will increase the difficulty
	 * level to a certain extent for the player (he/she will not be able to
	 * guess the right time to jump)
	 */

	void spawnCoin() {
		long now = System.currentTimeMillis() - spawnCoinTime;

		if (now > SPAWN_COIN_TIME) {
			if ((int) random(10) > 7) {
				if (!coin.alive) {
					coin.spawn();
				}
			}
			spawnCoinTime = System.currentTimeMillis();
		}
	}

	public void doPlayerGrabCoin() {

		soundPool.play(PlayerGrabCoinSnd, 1.0f, 1.0f, 0, 0, 1.0f);
		curScore += SCORE_COIN_BONUS;
		coin.alive = false;
		spawnCoinTime = System.currentTimeMillis();
	}

	/*
	 * doScore() mehtod does the scoring in Coincraver. Also, there is a score
	 * bonus if the player grabs the coin.
	 */

	private void doScore(Canvas canvas) {

		long now = System.currentTimeMillis() - scoreTime;

		if (now > SCORE_TIME) {
			curScore += SCORE_INC;
			scoreTime = System.currentTimeMillis();
		}

		StringBuilder buf = new StringBuilder("SCORE: ");
		buf.append(curScore);
		canvas.drawText(buf.toString(), width / 2.250f, 120, whitePaint);
	}

	/*
	 * restore() method restores the game state and values of different
	 * vairables. This makes use of SharedPreferences.
	 */

	public void restore(SharedPreferences savedState) {

		if (savedState.getInt("game_saved", 0) != 1) {
			return;
		}

		SharedPreferences.Editor editor = savedState.edit();
		editor.remove("game_saved");
		editor.commit();

		highScore = savedState.getInt("game_highScore", SCORE_DEFAULT);

		int lastHoleId = savedState.getInt("game_lastHole_id", -1);

		if (lastHoleId != -1) {
			lastHole = holes[lastHoleId];

		} else {
			lastHole = null;
		}

		spawnHoleTime = savedState.getLong("game_spawnHoleTicks", 0);
		playerTap = savedState.getBoolean("game_playerTap", false);
		gameState = savedState.getInt("game_gameState", 0);
		tapToStartTime = savedState.getLong("game_tapToStartTime", 0);
		showTapToStart = savedState.getBoolean("game_showTapToStart", false);
		getReadyGoTime = savedState.getLong("game_getReadyGoTime", 0);
		getReadyGoState = savedState.getInt("game_getReadyGoState", 0);
		gameOverTime = savedState.getLong("game_gameOverTime", 0);

		lastGameState = savedState.getInt("game_lastGameState", 1);
		pauseStartTime = savedState.getLong("game_pauseStartTime", 0);

		spawnCoinTime = savedState.getLong("game_spawnCoinTime", 0);

		scoreTime = savedState.getLong("game_scoreTime", 0);
		curScore = savedState.getInt("game_curScore", 0);

		player.restore(savedState);

		for (Hole h : holes) {
			h.restore(savedState);
		}

		coin.restore(savedState);

		road.restore(savedState);
	}

	/*
	 * save() method saves the game state and state's of different variables so
	 * that they can be restored when the player resumes/comes back to the game.
	 * This is achieved with the help of SharedPreferences that makes the data
	 * persistent.
	 */

	public void save(SharedPreferences.Editor map) {

		if (map == null) {
			return;
		}

		map.putInt("game_saved", 1);

		map.putInt("game_highScore", highScore);

		if (lastHole == null) {
			map.putInt("game_lastHole_id", -1);
		} else {
			map.putInt("game_lastHole_id", lastHole.id);
		}

		map.putLong("game_spawnHoleTicks", spawnHoleTime);
		map.putBoolean("game_playerTap", playerTap);
		map.putInt("game_gameState", gameState);
		map.putLong("game_tapToStartTime", tapToStartTime);
		map.putBoolean("game_showTapToStart", showTapToStart);
		map.putLong("game_getReadyGoTime", getReadyGoTime);
		map.putInt("game_getReadyGoState", getReadyGoState);
		map.putLong("game_gameOverTime", gameOverTime);
		map.putInt("game_lastGameState", lastGameState);
		map.putLong("game_pauseStartTime", pauseStartTime);
		map.putLong("game_spawnCoinTime", spawnCoinTime);
		map.putLong("game_scoreTime", scoreTime);
		map.putInt("game_curScore", curScore);
		player.save(map);

		for (Hole h : holes) {
			h.save(map);
		}

		coin.save(map);
		road.save(map);
		map.commit();
	}

	public float random(float a) {
		return rng.nextFloat() * a;
	}

	public float random(float a, float b) {
		return Math.round(a + (rng.nextFloat() * (b - a)));
	}

}