package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     }

    private void doAnimator(){
        ValueAnimator animator = ValueAnimator.ofInt(0, 400);
        animator.setDuration(2000);

        DecelerateInterpolator decelerateInterpolator;
        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                Log.i("", "input :" + input);
                return input;
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (Integer) animation.getAnimatedValue();
                Log.i("", "curValue :" + curValue);
                tv.layout(curValue,curValue,tv.getWidth() + curValue,tv.getHeight() + curValue);
            }
        });
        animator.start();
    }
}