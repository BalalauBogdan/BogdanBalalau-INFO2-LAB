package com.example.simplecalcapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView firstNumber = findViewById(R.id.editTextText);
        TextView secondNumber = findViewById(R.id.editTextText2);
        TextView result = findViewById(R.id.editTextText3);
        Button addButton = findViewById(R.id.button);
        Button subButton = findViewById(R.id.button2);
        Button divButton = findViewById(R.id.button3);
        Button mulButton = findViewById(R.id.button4);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(firstNumber.getText().toString());
                int b = Integer.parseInt(secondNumber.getText().toString());
                int r = a + b;
                result.setText("" + r);
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(firstNumber.getText().toString());
                int b = Integer.parseInt(secondNumber.getText().toString());
                int r = a - b;
                result.setText("" + r);
            }
        });

        divButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(firstNumber.getText().toString());
                int b = Integer.parseInt(secondNumber.getText().toString());
                double r = (double)a / b;
                result.setText("" + r);
            }
        });

        mulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(firstNumber.getText().toString());
                int b = Integer.parseInt(secondNumber.getText().toString());
                int r = a * b;
                result.setText("" + r);
            }
        });
    }
}