package dev.bogdanbalalau.safealert;

import android.content.BroadcastReceiver;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class BatteryLevelReceiver extends BroadcastReceiver {
    private static boolean hasAlertBeenSent = false;
    private static final String CHANNEL_ID = "battery_saver_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level * 100 / (float) scale;

            if (batteryPct <= 10) {
                if (!hasAlertBeenSent) {
                    Toast.makeText(context, "Suggestion: Activate power saving mode", Toast.LENGTH_SHORT).show();
                    hasAlertBeenSent = true;

                    sendAlertToFavoriteContacts(context);

                    suggestPowerSavingMode(context);

                    sendLocationIfNeeded(context);
                }
            } else {
                hasAlertBeenSent = false;
            }
        }
    }

    private void sendAlertToFavoriteContacts(Context context) {
        String phoneNumber = "0747011750";
        String message = "The battery is below 10%! Please contact me urgently!";

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(context, "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error sending SMS: permission was denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Permission to send SMS was not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void suggestPowerSavingMode(Context context) {
        Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
                .setContentTitle("Battery Saver Mode")
                .setContentText("The battery is below 10%. Would you like to activate power saving mode?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Low Battery Alert";
            String description = "Notification to activate power saving mode";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(context, "Notification permission is not granted.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            notificationManager.notify(1, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error displaying notification: permission was not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void sendLocationIfNeeded(Context context) {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(context, "Permisiunea pentru locație nu a fost acordată", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();

                            String locationMessage = "Localizare: Lat=" + lat + ", Lng=" + lng;
                            String phoneNumber = "0747011750";

                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumber, null,
                                        "Baterie scăzută! " + locationMessage, null, null);
                                Toast.makeText(context, "SMS cu locația a fost trimis.", Toast.LENGTH_SHORT).show();
                            } catch (SecurityException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Eroare la trimiterea SMS-ului cu locația: permisiunea a fost refuzată", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Nu s-a putut obține locația curentă.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(context, "Eroare la obținerea locației: permisiunea a fost refuzată", Toast.LENGTH_SHORT).show();
        }
    }
}
