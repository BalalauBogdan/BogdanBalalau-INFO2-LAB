package dev.bogdanbalalau.safealert;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public class GeofencingActivity extends AppCompatActivity {
    private static final double HOME_LATITUDE = 44.4268;
    private static final double HOME_LONGITUDE = 26.1025;
    private static final float GEOFENCE_RADIUS = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2001;
    private static final int BACKGROUND_LOCATION_REQUEST_CODE = 2024;

    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private Button btnAddGeofence;
    private TextView tvGeofenceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofencing);

        btnAddGeofence = findViewById(R.id.btn_add_geofence);
        tvGeofenceInfo = findViewById(R.id.tv_geofence_info);
        geofencingClient = LocationServices.getGeofencingClient(this);

        btnAddGeofence.setOnClickListener(v -> checkLocationPermissionsAndAddGeofence());

        // Cerem permisiunea de locație în background dacă e Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_REQUEST_CODE);
            }
        }
    }

    private void checkLocationPermissionsAndAddGeofence() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            addHomeGeofence();
        }
    }

    private void addHomeGeofence() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId("HOME_GEOFENCE_ID")
                .setCircularRegion(HOME_LATITUDE, HOME_LONGITUDE, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(geofence)
                .build();

        geofencePendingIntent = getGeofencePendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisiune locație lipsă!", Toast.LENGTH_SHORT).show();
            return;
        }

        geofencingClient.addGeofences(request, geofencePendingIntent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Geofence activat!", Toast.LENGTH_SHORT).show();
                    tvGeofenceInfo.setText("Geofence setat. Ieșirea va trimite SMS.");
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Eroare necunoscută";
                    if (e instanceof ApiException) {
                        int code = ((ApiException) e).getStatusCode();
                        switch (code) {
                            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                                errorMsg = "Geofence indisponibil. Verifică Google Play Services.";
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                                errorMsg = "Prea multe geofence-uri.";
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                                errorMsg = "Prea multe PendingIntent-uri.";
                                break;
                            default:
                                errorMsg = "Eroare cod: " + code;
                        }
                    }
                    Log.e("Geofence", "Eroare la activare: " + errorMsg, e);
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) return geofencePendingIntent;
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return geofencePendingIntent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addHomeGeofence();
        }

        if (requestCode == BACKGROUND_LOCATION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisiune background location acordată!", Toast.LENGTH_SHORT).show();
        }
    }
}