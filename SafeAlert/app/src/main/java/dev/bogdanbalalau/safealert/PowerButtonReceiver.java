package dev.bogdanbalalau.safealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerButtonReceiver extends BroadcastReceiver {
    private static int powerButtonPressCount = 0;
    private static long lastPressTime = 0;
    private static final long INTERVAL = 3000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPressTime > INTERVAL) {
                powerButtonPressCount = 1;
            } else {
                powerButtonPressCount++;
            }
            lastPressTime = currentTime;

            if (powerButtonPressCount == 3) {
                MainActivity.sendSosMessagesStatic(context);
                powerButtonPressCount = 0;
            }
        }
    }
}
