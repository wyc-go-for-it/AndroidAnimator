package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    private Button btn1,btn2,btn3,btn4,btn5,menu;
    boolean isOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_animator_layout);

        btn1 = findViewById(R.id.one);
        btn2 = findViewById(R.id.two);
        btn3 = findViewById(R.id.three);
        btn4 = findViewById(R.id.four);
        btn5 = findViewById(R.id.five);
        menu = findViewById(R.id.menu);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),v.toString(),Toast.LENGTH_LONG).show();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen){
                    isOpen = true;
                    open();
                }else {
                    isOpen = false;
                    close();
                }
            }
        });
    }

    private void open() {
        doAnimateOpen(btn1,0,5,500);
        doAnimateOpen(btn2,1,5,500);
        doAnimateOpen(btn3,2,5,500);
        doAnimateOpen(btn4,3,5,500);
        doAnimateOpen(btn5,4,5,500);
    }

    private void close(){
        doAnimateClose(btn1,0,5,500);
        doAnimateClose(btn2,1,5,500);
        doAnimateClose(btn3,2,5,500);
        doAnimateClose(btn4,3,5,500);
        doAnimateClose(btn5,4,5,500);
    }

    private void doAnimateOpen(View view, int index , int total , int radius) {
        if (view.getVisibility() != View.VISIBLE){
            view.setVisibility(View.VISIBLE);
        }

        double radian = ( Math.PI / 2) / (total - 1) * index;
        float x = -(float) (radius * Math.cos(radian));
        float y = -(float) (radius * Math.sin(radian));

/*        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view,"rotation",0f,360),ObjectAnimator.ofFloat(view,"translationX",0f,x),
                ObjectAnimator.ofFloat(view,"translationY",0f,y),
                ObjectAnimator.ofFloat(view,"scaleX",0f,1f),
                ObjectAnimator.ofFloat(view,"scaleY",0f,1f),
                ObjectAnimator.ofFloat(view,"alpha",0f,1f));
        animatorSet.setDuration(500).start();*/
        view.animate().translationX(x).translationY(y).scaleX(1).scaleY(1).alpha(1).setDuration(500).start();
    }

    private void doAnimateClose(View view, int index , int total , int radius) {
        if (view.getVisibility() != View.VISIBLE){
            view.setVisibility(View.VISIBLE);
        }

        double radian = (Math.PI / 2) / (total - 1) * index;
        float x = -(float) (radius * Math.cos(radian));
        float y = -(float) (radius * Math.sin(radian));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view,"translationX",x,0f),
                ObjectAnimator.ofFloat(view,"translationY",y,0f),
                ObjectAnimator.ofFloat(view,"scaleX",1f,0f),
                ObjectAnimator.ofFloat(view,"scaleY",1f,0f),
                ObjectAnimator.ofFloat(view,"alpha",1f,0f));

        animatorSet.setDuration(500).start();
    }

}