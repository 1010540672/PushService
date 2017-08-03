package com.yqq.pushservice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.yqq.pushservice.MyApplication;
import com.yqq.pushservice.utils.NetTool;
import com.yqq.pushservice.utils.ServiceMananger;

/**
 * ws链接监听
 * 
 * @author yqq
 * 
 */
public class WSManagerReceiver extends BroadcastReceiver {
	private static final String TAG = "WSManagerReceiver";



	@Override
	public void onReceive(Context ctx, Intent intent) {

		String action = intent.getAction();

		Log.e(TAG, "action=" + action);

		if (Intent.ACTION_TIME_TICK.equals(action)
				|| ConnectivityManager.CONNECTIVITY_ACTION.equals(action)
				|| Intent.ACTION_BOOT_COMPLETED.equals(action)
				|| Intent.ACTION_SCREEN_ON.equals(action)
				|| Intent.ACTION_SCREEN_OFF.equals(action) || WSProto.WS_ONLINE.equals(action) || WSProto.WS_OFFLINE.equals(action) || WSProto.WS_CLOSE.equals(action) || WSProto.WS_MSG_SEND_FAIL.equals(action)) {


			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				if (NetTool.isNetConnected(MyApplication.getInstance())) {
					ServiceMananger.startWork();

				} else {
					Log.e(TAG, "无网络，ws离线情况下不处理");
				}
			}


			//检测ws是否在线
			if (!WSProto.WS_ONLINE.equals(action) && !WSProto.WS_OFFLINE.equals(action) && !ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				//YJBToastUtils.show(ctx, "ws链接----心跳检测");
				Log.e(TAG, "ws链接----心跳检测");
				ServiceMananger.checkWsOnLine();


			}


			if (WSProto.WS_OFFLINE.equals(action)) {

				//ws不在线重新登录
				if (NetTool.isNetConnected(MyApplication.getInstance())) {

					ServiceMananger.loginWS();


				} else {
					Log.e(TAG, "无网络，ws离线情况下不处理");
				}

			}


			if (WSProto.WS_ONLINE.equals(action)) {

				Log.e(TAG, "ws在线情况下不处理");


			}


			if (WSProto.WS_CLOSE.equals(action)) {

				Log.e(TAG, "ws已经关闭");
				ServiceMananger.startWork();


			}

			if (WSProto.WS_MSG_SEND_FAIL.equals(action)) {

				Log.e(TAG, "ws消息发送失败 有网络重连");
				if (NetTool.isNetConnected(MyApplication.getInstance())) {
					ServiceMananger.startWork();


				}


			}


		}


	}

}
