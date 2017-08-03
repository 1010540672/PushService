package com.yqq.pushservice.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;



/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class WSManagerService extends Service {
    private static  final String  TAG="WSManagerService";
    private WSManagerReceiver mReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver=new WSManagerReceiver();
        IntentFilter filter = new IntentFilter();

// 时钟信息发生变化
        filter.addAction(Intent.ACTION_TIME_TICK);
        // 开机广播
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        // 网络状态发生变化
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // 屏幕打开
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(WSProto.WS_CLOSE);
        filter.addAction(WSProto.WS_MSG_SEND_FAIL);
        filter.addAction(WSProto.WS_OFFLINE);
        filter.addAction(WSProto.WS_ONLINE);
        filter.setPriority(Integer.MAX_VALUE);


        // 注册广播
        registerReceiver(mReceiver, filter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mReceiver){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
