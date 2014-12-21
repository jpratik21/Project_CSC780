/**
 * Android Application/game: Coincraver
 * 
 */

package com.mobile.coincraver;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * This TyperViewActivity creates the typing animation for the text in the
 * TextView of StartActivity(specified in res/activity_start.xml). This uses a
 * delay of 200ms after every chartacter and thus, provides a so-called virtual
 * animation. Context class is used so that the TyperViewActivity will know till
 * what character it is done with the typing.
 * 
 * @author jpratik
 *
 */

public class TyperViewActivity extends TextView {

	private CharSequence mText;
	private int mIndex;
	private long mDelay = 200;

	public TyperViewActivity(Context context) {
		super(context);
	}

	public TyperViewActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*
	 * Handler that adds the delay between each character. In other words, it
	 * introduces the delay of 200 ms after "C" of Coincraver, then 200 ms delay
	 * after "o" and so on. Thus, creates a virtual animation.
	 */

	private Handler mHandler = new Handler();
	private Runnable characterAdder = new Runnable() {
		@Override
		public void run() {
			setText(mText.subSequence(0, mIndex++));
			if (mIndex <= mText.length()) {
				mHandler.postDelayed(characterAdder, mDelay);
			}
		}
	};

	/*
	 * animateText() method is responsbile for inserting the blank text while
	 * the handler is inserting the delay. Check the setText() method written
	 * below.
	 */

	public void animateText(CharSequence text) {
		mText = text;
		mIndex = 0;

		setText("");
		mHandler.removeCallbacks(characterAdder);
		mHandler.postDelayed(characterAdder, mDelay);
	}

	public void setCharacterDelay(long millis) {
		mDelay = millis;
	}

}