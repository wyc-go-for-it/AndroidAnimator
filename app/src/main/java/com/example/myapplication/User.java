package com.example.myapplication;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class User {
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static class Handler{
        public void clickFirstName(final View view){
            Log.d("FirstName",((TextView)view).getText().toString());
        }
        public void clickLastName(final View view){
            Log.d("LastName",((TextView)view).getText().toString());
        }
    }
}
