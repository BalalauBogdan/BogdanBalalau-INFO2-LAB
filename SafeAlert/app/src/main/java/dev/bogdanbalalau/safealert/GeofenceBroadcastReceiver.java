package dev.bogdanbalalau.safealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event == null || event.hasError()) return;

        int transition = event.getGeofenceTransition();
        if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            String message = "Ai ieșit din zona de siguranță!";
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("0747011750", null, message, null, null);
            Toast.makeText(context, "Geofence EXIT! SMS trimis!", Toast.LENGTH_LONG).show();
            Log.d("GEOFENCE", "EXIT detectat și SMS trimis.");
        }
    }
}