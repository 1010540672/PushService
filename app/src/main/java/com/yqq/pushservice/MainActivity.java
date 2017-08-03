package com.yqq.pushservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.yqq.pushservice.utils.ServiceMananger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServiceMananger.startWork();

    }
}
