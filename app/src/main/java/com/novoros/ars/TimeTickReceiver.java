package com.novoros.ars;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;


public class TimeTickReceiver extends BroadcastReceiver {
    private final static String TAG = TimeTickReceiver.class.getSimpleName();

    private final ITimerTickReceiver iCallback;

    public TimeTickReceiver(ITimerTickReceiver iCallback) {
        this.iCallback = iCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        if (action == null) return;

        if (action.equals(Intent.ACTION_TIME_TICK)) iCallback.onTimeChange();

        Log.d(TAG, "onTimeChangeDetected : " + new Date(System.currentTimeMillis()));
    }

    public interface ITimerTickReceiver {
        void onTimeChange();
    }

}
