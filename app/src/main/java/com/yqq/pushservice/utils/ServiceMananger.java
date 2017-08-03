package com.yqq.pushservice.utils;

import android.content.Context;

import com.yqq.pushservice.MyApplication;
import com.yqq.pushservice.service.WebSocketService;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class ServiceMananger {

    public static void startWork() {


               ThreadPoolUtils.execute(new Runnable() {
                   @Override
                   public void run() {
                       WebSocketService.actionStart(MyApplication.getInstance());
                   }
               });





    }



    public static void stopWork() {

       ThreadPoolUtils.execute(new Runnable() {
           @Override
           public void run() {
               WebSocketService.actionStop(MyApplication.getInstance());
           }
       });

    }

    /**
     * 检测ws链接状态
     *
     * @return
     */
    public static void checkWsOnLine() {



        WebSocketService.checkOnLine();
    }


    public static void loginWS() {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                WebSocketService.Login2();
            }
        });
    }
}
