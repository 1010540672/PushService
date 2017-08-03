package com.yqq.pushservice;

import android.app.Application;
import android.content.Intent;

import com.yqq.pushservice.service.WSManagerService;
import com.yqq.pushservice.service.WebSocketService;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class MyApplication extends Application {
    private static MyApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //startService(new Intent(this, WebSocketService.class));
        startService(new Intent(this, WSManagerService.class));
    }


    public synchronized static MyApplication getInstance() {

        return mApplication;
    }

}
