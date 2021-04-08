package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class ViewGroupAnimatorActivity extends AppCompatActivity {
    private Button add,del;
    private int num;
    private LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_animator);

        add = findViewById(R.id.add);
        del = findViewById(R.id.del);
        container = findViewById(R.id.container);

        add.setOnClickListener(v -> addButton());

        del.setOnClickListener(v -> delButton());


        addAnimator();
    }

    private void addButton(){
        final Button btn = new Button(this);
        btn.setText(String.valueOf(num++));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(layoutParams);
        container.addView(btn,0);
    }

    private void delButton(){
        if (num >=0 ){
            container.removeViewAt(0);
            num--;
        }
    }

    private void addAnimator(){
        final LayoutTransition transition = new LayoutTransition();
        //transition.setAnimator(LayoutTransition.DISAPPEARING, ObjectAnimator.ofFloat(null,"rotation",0,360));
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, ObjectAnimator.ofPropertyValuesHolder(container,PropertyValuesHolder.ofFloat("translationX",0,18,0)));

        container.setLayoutTransition(transition);
    }
}