package dev.bogdanbalalau.safealert;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class InactivityService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private static Handler inactivityHandler;
    private static Runnable inactivityRunnable;
    private static InactivityService instance;

    private static final long INACTIVITY_TIMEOUT = 10 * 1000; // 10 secunde pentru test
    private float motionThreshold = 1.5f;

    private FusedLocationProviderClient locationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d("INACTIVITY", "Service creat");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        inactivityHandler = new Handler();
        inactivityRunnable = new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.sendInactivityAlert();
                }
            }
        };

        startMonitoring();
    }

    private void startMonitoring() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        resetInactivityTimer();
        Log.d("INACTIVITY", "Senzori înregistrați și timer pornit");
    }

    public static void resetExternalTimer() {
        if (inactivityHandler != null && inactivityRunnable != null) {
            inactivityHandler.removeCallbacks(inactivityRunnable);
            inactivityHandler.postDelayed(inactivityRunnable, INACTIVITY_TIMEOUT);
            Log.d("INACTIVITY", "Timer resetat din interacțiune UI");
        }
    }

    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable, INACTIVITY_TIMEOUT);
        Log.d("INACTIVITY", "Timer resetat din senzor");
    }

    private void sendInactivityAlert() {
        Log.d("INACTIVITY", "Trimit mesaj de inactivitate...");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            String msg = "Nu am mai fost activ de 10 minute. Locația mea este: ";

            if (location != null) {
                msg += "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
            } else {
                msg += "Locație necunoscută.";
            }

            String phoneNumber = "0747011750";

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", msg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // <== IMPORTANT în servicii
            startActivity(intent);

            Log.d("INACTIVITY", "Intent trimis pentru SMS");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        inactivityHandler.removeCallbacks(inactivityRunnable);
        Log.d("INACTIVITY", "Service oprit și senzori dezactivați");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Magnitudinea totală a accelerației
            double magnitude = Math.sqrt(x * x + y * y + z * z);

            Log.d("INACTIVITY", "Accelerație totală: " + magnitude);

            // Dacă devierea e semnificativă față de gravitație, considerăm că s-a mișcat
            if (Math.abs(magnitude - SensorManager.GRAVITY_EARTH) > motionThreshold) {
                Log.d("INACTIVITY", "Mișcare reală detectată, reset timer");
                resetInactivityTimer();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}