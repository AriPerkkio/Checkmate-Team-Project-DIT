package com.theateam.checkmate;

import android.os.CountDownTimer;

/**
 * Created by AriPerkkio on 16/04/16.
 */
public class ChessTimer extends CountDownTimer {
    Player player;

    public ChessTimer(long startTime, long interval, Player _player) {
        super(startTime, interval);
        player = _player;
    }

    @Override
    public void onFinish() {
        // Not used
    }

    @Override
    public void onTick(long millisUntilFinished) {
        player.increaseTimer();
    }

    public void pause(){
        this.cancel();
    }

    public void resume(){
        this.start();
    }
}
