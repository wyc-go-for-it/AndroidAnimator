package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PorterDuffXferModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porter_duff_xfer_mode);
        final PorterDuffXferModeView view = findViewById(R.id.porterDuffXferModeView);
        final Button btn = findViewById(R.id.reset_btn);
        btn.setOnClickListener(v -> view.reset());

    }
}