package com.example.book;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView introText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        introText = findViewById(R.id.introText);

        findViewById(R.id.btn_chapter1).setOnClickListener(v -> openChapter(1));
        findViewById(R.id.btn_chapter2).setOnClickListener(v -> openChapter(2));
        findViewById(R.id.btn_chapter3).setOnClickListener(v -> openChapter(3));
        findViewById(R.id.btn_chapter4).setOnClickListener(v -> openChapter(4));
    }

    private void openChapter(int chapterNumber) {
        introText.setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ChapterFragment(chapterNumber))
                .addToBackStack(null)
                .commit();
    }
}