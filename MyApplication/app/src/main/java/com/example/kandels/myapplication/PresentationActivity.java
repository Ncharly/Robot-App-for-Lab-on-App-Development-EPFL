package com.example.kandels.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PresentationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
    }

    public void StartManual(View view) {
        Intent manualActivity = new Intent(PresentationActivity.this, MainActivity.class);
        startActivity(manualActivity);
    }
}
