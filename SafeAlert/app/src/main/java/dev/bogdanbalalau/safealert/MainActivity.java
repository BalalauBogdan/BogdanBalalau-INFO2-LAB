package dev.bogdanbalalau.safealert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String API_URL = "https://api.weatherapi.com/v1/forecast.json?key=d493acb6429a4c899de160409252203&q=Bucuresti&alerts=yes";
    private static final int REQUEST_CALL_PHONE_PERMISSION = 200;
    private static final int REQUEST_SMS_PERMISSION = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final int PERMISSION_REQUEST_SMS = 1001;
    private static final int PERMISSION_REQUEST_LOCATION = 1002;
    private FusedLocationProviderClient fusedLocationClient;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private Button btnSos;
    private TextView tvCountdown;
    private Button btnCancelCall;
    private CountDownTimer countDownTimer;
    private BatteryLevelReceiver batteryLevelReceiver;
    private IntentFilter batteryIntentFilter;
    private static final String[] emergencyNumbers = {
            "0747011750", "0747011751", "0747011752", "0747011753", "0747011754"
    };
    private final String emergencyCallNumber = "0747011750";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCountdown = findViewById(R.id.tv_countdown);
        btnCancelCall = findViewById(R.id.btn_cancel_call);
        btnSos = findViewById(R.id.btn_sos);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        batteryLevelReceiver = new BatteryLevelReceiver();
        batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        checkSmsPermission();
        checkLocationPermission();
        checkCallPermission();
        checkPermissions();
        checkAudioPermission();
        checkAndRequestSmsPermission();
        checkAndRequestLocationPermission();

        Button alertButton = findViewById(R.id.btnCheckAlerts);
        alertButton.setOnClickListener(view -> checkWeatherAlerts());

        configureSpeechRecognizer();
        findViewById(R.id.btn_voice_command).setOnClickListener(view -> speechRecognizer.startListening(speechRecognizerIntent));

        btnSos.setOnClickListener(view -> {
            getCurrentLocationAndSendSms();
            startEmergencyCallTimer();
        });

        btnCancelCall.setOnClickListener(view -> cancelEmergencyCallTimer());

        findViewById(R.id.btn_go_to_geofencing).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GeofencingActivity.class)));

        findViewById(R.id.btn_inactivity).setOnClickListener(v -> {
            startService(new Intent(this, InactivityService.class));
            Toast.makeText(this, "Monitorizare inactivitate pornită", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_stop_inactivity).setOnClickListener(v -> {
            stopService(new Intent(this, InactivityService.class));
            Toast.makeText(this, "Monitorizare inactivitate oprită", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_auto_message).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ScheduledMessageActivity.class);
            startActivity(intent);
        });
    }


    private void configureSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    for (String result : matches) {
                        if (result.equalsIgnoreCase("sos")) {
                            sendSosMessagesStatic(MainActivity.this);
                            break;
                        }
                    }
                }
            }
        });

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro-RO");
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1234);
        }
    }

    private void checkWeatherAlerts() {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "Alertă de test detectată!", Toast.LENGTH_LONG).show();
            sendEmergencySms("Test Alert - Funcționează!");
        });
    }

    private void sendEmergencySms(String alertText) {
        String phoneNumber = "0774043750";
        String message = "ALERTĂ GRAVĂ: " + alertText;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS trimis: " + message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Eroare la trimiterea SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSosMessage(String messageText) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                for (String number : emergencyNumbers) {
                    smsManager.sendTextMessage(number, null, messageText, null, null);
                }
                Toast.makeText(this, "Mesaj SOS trimis cu succes!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Eroare la trimiterea mesajului.", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    }

    public static void sendSosMessagesStatic(Context context) {
        String sosText = "SOS! Apăsare rapidă de 3 ori a butonului POWER. Nu s-a putut obține locația.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : emergencyNumbers) {
                smsManager.sendTextMessage(number, null, sosText, null, null);
            }
            Toast.makeText(context, "Mesaj SOS trimis prin apăsări multiple!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Eroare la trimiterea mesajelor SOS.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocationAndSendSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    String sosText = "SOS! Am nevoie de ajutor urgent! Locația mea: " +
                            location.getLatitude() + ", " + location.getLongitude();
                    sendSosMessage(sosText);
                } else {
                    sendSosMessage("SOS! Am nevoie de ajutor urgent! Locația nu a putut fi determinată.");
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }
    private void startEmergencyCallTimer() {
        tvCountdown.setVisibility(View.VISIBLE);
        btnCancelCall.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(10_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvCountdown.setText("Apel de urgență în " + secondsLeft + " secunde...");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Se apelează contactul de urgență...");
                callEmergencyNumber();
            }
        }.start();
    }

    private void cancelEmergencyCallTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        tvCountdown.setVisibility(View.GONE);
        btnCancelCall.setVisibility(View.GONE);
        Toast.makeText(this, "Apelul de urgență a fost anulat.", Toast.LENGTH_SHORT).show();
    }

    private void callEmergencyNumber() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + emergencyCallNumber));
            startActivity(callIntent);
            tvCountdown.setVisibility(View.GONE);
            btnCancelCall.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Nu ai acordat permisiunea de a efectua apeluri.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.SEND_SMS
        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                break;
            }
        }
    }

    private void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        2024);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PHONE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisiune apel acordată.", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_SMS_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisiunea de a trimite SMS a fost acordată.", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisiunea de locație a fost acordată.", Toast.LENGTH_SHORT).show();
        }
    }
    // ================= Battery Level Receiver =================
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryLevelReceiver, batteryIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryLevelReceiver);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        InactivityService.resetExternalTimer();
    }

    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_REQUEST_SMS);
        }
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }
}