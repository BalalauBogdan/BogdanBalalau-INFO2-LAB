package dev.bogdanbalalau.safealert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class ScheduledMessageActivity extends AppCompatActivity {

    private TextInputEditText etPhoneNumber, etMessage;
    private MaterialButton btnSchedule, btnCancel, btnSelectDelay;
    private TextView tvSelectedDelay;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private long selectedDelayMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_message);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etMessage = findViewById(R.id.etMessage);
        btnSelectDelay = findViewById(R.id.btnSelectDelay);
        tvSelectedDelay = findViewById(R.id.tvSelectedDelay);
        btnSchedule = findViewById(R.id.btnSchedule);
        btnCancel = findViewById(R.id.btnCancel);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        btnSelectDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleMessage();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelScheduledMessage();
            }
        });
    }

    private void openTimePicker() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTitleText("Selectează timpul de așteptare")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        picker.show(fragmentManager, "MTPicker");

        picker.addOnPositiveButtonClickListener(dialog -> {
            int hours = picker.getHour();
            int minutes = picker.getMinute();
            selectedDelayMinutes = hours * 60 + minutes;
            tvSelectedDelay.setText("Timpul selectat: " + hours + " ore, " + minutes + " minute");
        });
    }

    private void scheduleMessage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Permisiunea pentru alarme exacte nu este activată. Activează-o în setări!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        String phoneNumber = etPhoneNumber.getText() != null ? etPhoneNumber.getText().toString().trim() : "";
        String message = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";

        if (phoneNumber.isEmpty() || message.isEmpty() || selectedDelayMinutes <= 0) {
            Toast.makeText(this, "Completează toate câmpurile și selectează un timp valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        long triggerTime = System.currentTimeMillis() + selectedDelayMinutes * 60_000;

        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("message", message);

        pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        Toast.makeText(this, "Mesaj programat!", Toast.LENGTH_SHORT).show();
    }

    private void cancelScheduledMessage() {
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Mesaj anulat!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nu există niciun mesaj programat.", Toast.LENGTH_SHORT).show();
        }
    }
}