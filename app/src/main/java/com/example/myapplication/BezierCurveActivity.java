package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BezierCurveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezier_curve_layout);

        final BezierCurveView bezierCurveView = findViewById(R.id.bezierCurveView);
        final Button btn = findViewById(R.id.start_btn),clear_btn = findViewById(R.id.clear_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bezierCurveView.startAnimator();
            }
        });
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bezierCurveView.clear();
            }
        });
    }
}
