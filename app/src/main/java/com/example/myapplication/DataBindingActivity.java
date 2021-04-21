package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.myapplication.databinding.DatabindingTestLayoutBinding;

public class DataBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabindingTestLayoutBinding binding = DataBindingUtil.setContentView(this,R.layout.databinding_test_layout);
        final User user = new User();
        user.setFirstName("wyc");
        user.setLastName("8888");
        binding.setUser(user);
    }
}