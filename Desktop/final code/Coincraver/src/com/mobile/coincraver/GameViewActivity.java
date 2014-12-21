/**
 * Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This GameViewActivity extends SurfaceView which provides a decidated view
 * surface to draw the objects for Coincraver. It implements the callbacks that
 * notifies the UI thread whenever there is any change in the surface. The
 * methods onSurfaceCreate(), onSurfaceDestroy() and onSurfaceChanged() are used
 * for returning the results of callbacks. In order to get the surface size,
 * setScreenSize() method specified in the GameActivity is used. Here, the
 * secondary thread and the UI thread communicate with the help of touch. There
 * is a touch event handler for the SurfaceView that triggers the doTouch()
 * method of secondary thread. Also, it creates a secondary thread to run the
 * game independent of the background view(UI thread)
 * 
 * @author jpratik
 *
 */

public class GameViewActivity extends SurfaceView implements
		SurfaceHolder.Callback {

	/*
	 * Class for the secondary thread "PlayerMoveThread" that responds to the
	 * onTouchEvent() of the SurfaceView. It has a doAccelerometer() method that
	 * triggers the doTouch() method of the GameActivity. So, we have a option
	 * of a finger touch / use accelerator in order to jump.
	 */

	class PlayerMoveThread extends Thread {

		private SurfaceHolder surfaceHolder;
		boolean run;
		GameActivity game;

		public PlayerMoveThread(SurfaceHolder surfaceHolder, Context context,
				GameActivity game) {
			run = false;
			this.surfaceHolder = surfaceHolder;
			this.game = game;
		}

		public void setSurfaceSize(int width, int height) {
			synchronized (surfaceHolder) {
				game.setScreenSize(width, height);
			}
		}

		public void setRunning(boolean b) {
			run = b;
		}

		/*
		 * Android executes PlayerMoveThread.run() method which triggers the
		 * run() method of GameActivity. This takes the control to the
		 * GameActivity's gameplay() method.
		 */

		@Override
		public void run() {

			while (run) {
				Canvas c = null;
				try {
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						game.run(c);
					}
				} finally {
					if (c != null) {
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		/*
		 * This doTouchEvent() listens to the onTouchEvent() method defined in
		 * the SurfaceView below. The onTouchEvent() method of SurfaceView
		 * returns thread's doTouchEvent() which in turn calls the doTouch()
		 * method to trigger the jump
		 */

		boolean doTouchEvent(MotionEvent event) {
			boolean handled = false;

			synchronized (surfaceHolder) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					game.doTouch();
					handled = true;
					break;
				}
			}

			return handled;
		}

		boolean doAccelerometerEvent() {
			boolean handled = false;

			synchronized (surfaceHolder) {
				game.doTouch();
			}

			return handled;
		}

		public void pause() {
			synchronized (surfaceHolder) {
				game.pause();
				run = false;
			}
		}

		public void resetGame() {
			synchronized (surfaceHolder) {
				game.resetGame();
			}
		}

		public void restoreGame(SharedPreferences savedInstanceState) {
			synchronized (surfaceHolder) {
				game.restore(savedInstanceState);
			}
		}

		public void saveGame(SharedPreferences.Editor editor) {
			synchronized (surfaceHolder) {
				game.save(editor);
			}
		}
	}

	private PlayerMoveThread thread;
	private Context context;
	private GameActivity game;
	private SurfaceHolder holder;

	public GameViewActivity(Context context, AttributeSet attrs) {
		super(context, attrs);

		holder = getHolder();
		holder.addCallback(this);

		this.context = context;
		game = new GameActivity(context);

		thread = null;

		/*
		 * Sets the view so that it can receive the focus. Setting this to false
		 * will also ensure that this view is not focusable in touch mode. Here,
		 * focusable is true and so, this view can receive the focus.
		 */

		setFocusable(true);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {

				/*
				 * join() blocks the current Thread (Thread.currentThread())
				 * until the receiver finishes its execution and dies.
				 */

				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}

		thread = null;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return thread.doTouchEvent(event);
	}

	public PlayerMoveThread getThread() {
		if (thread == null) {
			thread = new PlayerMoveThread(holder, context, game);
		}
		return thread;
	}
}
