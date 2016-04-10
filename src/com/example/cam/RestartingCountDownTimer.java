package com.example.cam;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

public abstract class RestartingCountDownTimer {
	 
	    private final long mMillisInFuture;
	 
	    /**
	     * The interval in millis that the user receives callbacks
	     */
	    private final long mCountdownInterval;
	 
	    private long mStopTimeInFuture;
	 
	    /**
	     * @param millisInFuture    The number of millis in the future from the call
	     *                          to {@link #startOrRestart()} until the countdown is done and {@link #onFinish()}
	     *                          is called.
	     * @param countDownInterval The interval along the way to receive
	     *                          {@link #onTick(long)} callbacks.
	     */
	    RestartingCountDownTimer(long millisInFuture, long countDownInterval) {
	        mMillisInFuture = millisInFuture;
	        mCountdownInterval = countDownInterval;
	    }
	 
	    /**
	     * Cancel the countdown.
	     */
	    public final void cancel() {
	        mHandler.removeMessages(MSG);
	    }
	 
	    /**
	     * Start the countdown, If it is already started it will reset the time and start again.
	     */
	    public synchronized final RestartingCountDownTimer startOrRestart() {
	        if (mMillisInFuture <= 0) {
	            onFinish();
	            return this;
	        }
	        mHandler.removeMessages(MSG);
	        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
	        mHandler.sendMessage(mHandler.obtainMessage(MSG));
	        return this;
	    }
	 
	    /**
	     * Callback fired on regular interval.
	     *
	     * @param millisUntilFinished The amount of time until finished.
	     */
	    public void onTick(long millisUntilFinished) {
	        // override if needed
	    }
	 
	    /**
	     * Callback fired when the time is up.
	     */
	    public abstract void onFinish();
	 
	    private static final int MSG = 1;
	 
	    // handles counting down
	    private Handler mHandler = new Handler(Looper.getMainLooper()) {
	 
	        @Override
	        public void handleMessage(Message msg) {
	 
	            synchronized (RestartingCountDownTimer.this) {
	                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
	 
	                if (millisLeft <= 0) {
	                    onFinish();
	                } else if (millisLeft < mCountdownInterval) {
	                    // no tick, just delay until done
	                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
	                } else {
	                    long lastTickStart = SystemClock.elapsedRealtime();
	                    onTick(millisLeft);
	 
	                    // take into account user's onTick taking time to execute
	                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
	 
	                    // special case: user's onTick took more than interval to
	                    // complete, skip to next interval
	                    while (delay < 0) delay += mCountdownInterval;
	 
	                    sendMessageDelayed(obtainMessage(MSG), delay);
	                }
	            }
	        }
	    };

}
