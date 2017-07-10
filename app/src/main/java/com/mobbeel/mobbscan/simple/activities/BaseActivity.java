package com.mobbeel.mobbscan.simple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobbeel.mobbscan.simple.BaseAction;

public class BaseActivity extends AppCompatActivity implements BaseAction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void goNext() {

    }

    @Override
    public void sendPetition() {

    }
}
