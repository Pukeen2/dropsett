package com.dropsett.app.util;

import android.os.CountDownTimer;

public class RestTimerManager {

    public interface TimerListener {
        void onTick(long secondsRemaining);
        void onFinish();
    }

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;

    public void start(long seconds, TimerListener listener) {
        cancel();
        isRunning = true;
        countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                listener.onTick(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                listener.onFinish();
            }
        }.start();
    }

    public void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}