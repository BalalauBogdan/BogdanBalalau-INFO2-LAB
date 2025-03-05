package com.example.smartlightcontrol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.graphics.drawable.ColorDrawable;
import android.app.PendingIntent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout background;
    private Button toggleLightButton, cu
    private boolean isLightOn = false;
    private boolean isAutoMode = false;
    private static final String CHANNEL_ID = "light_notifications";

    public static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.background);
        toggleLightButton = findViewById(R.id.toggleLightButton);
        autoModeSwitch = findViewById(R.id.autoModeSwitch);
        customModeButton = findViewById(R.id.customModeButton);

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        toggleLightButton.setOnClickListener(view -> toggleLight());
        autoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAutoMode = isChecked;
            if (isAutoMode) {
                updateLightBasedOnTime();

            }
        });
        customModeButton.setOnClickListener(view -> cycleCustomColors());
    }

    private void toggleLight() {
        if (isAutoMode) return;
        isLightOn = !isLightOn;
        updateBackgroundColor();
    }

    private void updateBackgroundColor() {
        if (isLightOn) {
            background.setBackgroundColor(Color.YELLOW);
        } else {
            background.setBackgroundColor(Color.DKGRAY);
        }
    }

    private void updateLightBasedOnTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 7 && hour < 18) {
            background.setBackgroundColor(Color.WHITE);
            sendNotification("Good morning! Light is now white.");
        } else if (hour >= 18 && hour < 22) {
            background.setBackgroundColor(Color.rgb(255, 165, 0));
            sendNotification("Evening mode activated! Light is now orange.");
        } else {
            background.setBackgroundColor(Color.DKGRAY);
            sendNotification("Good night! The light has turned off automatically.");
        }
    }

    private void cycleCustomColors() {
        int currentColor = ((ColorDrawable) background.getBackground()).getColor();
        int newColor;
        switch (currentColor) {
            case Color.WHITE:
                newColor = Color.BLUE;
                sendNotification("Relaxation mode activated! Light is now blue.");
                break;
            case Color.BLUE:
                newColor = Color.RED;
                sendNotification("Alert mode activated! Light is now red.");
                break;
            case Color.RED:
                newColor = Color.GREEN;
                sendNotification("Nature mode activated! Light is now green.");
                break;
            default:
                newColor = Color.WHITE;
                sendNotification("Standard mode activated! Light is now white.");
                break;
        }
        background.setBackgroundColor(newColor);
    }

    private void sendNotification(String textMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Notification")
                .setContentText(textMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission granted");
            } else {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission denied");
            }
        }
    }

}