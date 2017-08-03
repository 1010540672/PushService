
package com.yqq.pushservice.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.yqq.pushservice.MyApplication;
import com.yqq.pushservice.utils.NetTool;
import com.yqq.pushservice.utils.ThreadPoolUtils;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;



/**
 * @author  yqq
 * websocket 实现长连接推送
 */
public class WebSocketService extends Service {
	private final static String TAG = "WebSocketService";

	private final static OkHttpClient client = new OkHttpClient.Builder()
    .build();
	private static WebSocket mWebSocket=null;
	private static final String ACTION_START = "WS" + ".START";
	private static final String ACTION_STOP = "WS" + ".STOP";
	public static final String WS_HOST = WSProto.WS_URL;
	private static final String WS_SERVER = WS_HOST;
	private static final int WS_PORT = WSProto.WS_PORT; // 服务器推送端口
	private static final String WS_URL_FORMAT = "ws://%s:%d/"; // 推送url格式组装

	@Override
	public IBinder onBind(Intent intent) {
		// return mBinder;
		return null;
	}
	// Static method to start the service
	public static void actionStart(Context ctx) {

		Log.e(TAG, "start  WebSocketService");
		Intent i = new Intent(ctx, WebSocketService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {

		Log.e(TAG, "stop  WebSocketService");
		Intent i = new Intent(ctx, WebSocketService.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		try {
			if (null != intent && null != intent.getAction()) {
				if (intent.getAction().equals(ACTION_STOP) == true) {
					Log.e(TAG, "WS STOP.");
					stop();
					stopSelf();
				} else if (intent.getAction().equals(ACTION_START) == true) {
					Log.e(TAG, "WS START.");
					start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return super.onStartCommand(intent, flags, startId);
		//return START_STICKY;
	}



	/**
	 * 销毁
	 */
	@Override
	public void onDestroy() {

		super.onDestroy();

		Log.d(TAG, "Service Destroy");


	}

	private synchronized void start() {

		connect();

	}




	private synchronized void stop() {
		if(null!=mWebSocket){
			mWebSocket.close(1000, "Normal connection closure");
		mWebSocket=null;
		}

	}

	private static void connect() {

		ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String wsuri=null;

						String ip = null;

						try {
							Log.e(TAG, "ws  ===========" + WS_SERVER);
							ip = InetAddress.getByName(WS_SERVER).getHostAddress();

						Log.e(TAG, "ws  ip===========" + ip);

						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.e(TAG, "ws获取ip===========" + e1.toString());
							ip = WS_SERVER;
						}

						wsuri = String.format(Locale.US, WS_URL_FORMAT, ip,
								WS_PORT);
						Log.e(TAG, "ws 地址===========" + wsuri);


					Request request = new Request.Builder()
							.url(wsuri)
							.build();

					client.newWebSocket(request, new okhttp3.WebSocketListener() {

						/**
						 * Invoked when both peers have indicated that no more messages will be transmitted and the
						 * connection has been successfully released. No further calls to this listener will be made.
						 */
						@Override
						public void onClosed(WebSocket webSocket, int code, String reason) {
							// TODO Auto-generated method stub
							super.onClosed(webSocket, code, reason);
							Log.e(TAG, "ws onClosed  code=" +code+"reason="+reason );




							SystemClock.sleep(5000L);


							if(NetTool.isNetConnected(MyApplication.getInstance())) {
								Log.e(TAG, "ws 有网络----发送ws关闭广播 ");
								Intent _intent = new Intent();
								_intent.setAction(WSProto.WS_CLOSE);
								MyApplication.getInstance().sendBroadcast(_intent);
							}else{
							Log.e(TAG, "ws 无网络-------========不发送ws关闭广播 ");
							}

						}

						/** Invoked when the peer has indicated that no more incoming messages will be transmitted. */
						@Override
						public void onClosing(WebSocket webSocket, int code, String reason) {
							// TODO Auto-generated method stub
							super.onClosing(webSocket, code, reason);
							Log.e(TAG, "ws onClosing  code=" +code+"reason="+reason );

							if(null!=mWebSocket){
								mWebSocket.close(code, reason);
								mWebSocket=null;
							}

						}
						/**
						 * Invoked when a web socket has been closed due to an error reading from or writing to the
						 * network. Both outgoing and incoming messages may have been lost. No further calls to this
						 * listener will be made
						 */
						@Override
						public void onFailure(WebSocket webSocket, Throwable t,
                                              Response response) {
							// TODO Auto-generated method stub
							super.onFailure(webSocket, t, response);

							Log.e(TAG, "ws onFailure  Throwable=" +t+"response="+response );

							webSocket.cancel();
						Log.e(TAG, "ws     发起重连");
							SystemClock.sleep(10000L);


							//无网络下链接失败不进行重连
							if(NetTool.isNetConnected(MyApplication.getInstance())){
								Log.e(TAG, "ws    有网络 发起重连");
								connect();
							}else{
								Log.e(TAG, "-------------ws onFailure-------------   无网络 不发起重连");

							}


						}

						@Override
						public void onMessage(WebSocket webSocket, String text) {
							// TODO Auto-generated method stub
							super.onMessage(webSocket, text);
						Log.e(TAG, "ws onMessage from server  text=" +text );

							proccessPushMessage(text);
						}

						@Override
						public void onOpen(WebSocket webSocket, Response response) {
							// TODO Auto-generated method stub
							super.onOpen(webSocket, response);

							mWebSocket=webSocket;
							Login2();
							Log.e(TAG, "ws  onOpen   response=" +response );

						}




					});

				} catch (Exception e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
					return ;
				}




			}
		});



	}





	private static void proccessPushMessage(String text) {

		Log.e(TAG, "收到后台返回消息===="+text);

	}




	
	
	public static void SendMessage(String msg) {

		try {
//
			if(null!=mWebSocket){
			  if(!mWebSocket.send(msg)){
				Log.e(TAG, "ws 发送失败....发送失败广播-");

				  if(NetTool.isNetConnected(MyApplication.getInstance())){
					  SystemClock.sleep(200);
				Log.e(TAG, "--ws 发送失败....发送失败广播--");

				 Intent intent=new Intent();
				 intent.setAction(WSProto.WS_MSG_SEND_FAIL);
					  MyApplication.getInstance().sendBroadcast(intent);
				  }
				 
				  
			  }
			  
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			return;
		}

	}



	public static void Login2() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();



			sb.append("你自己的协议");

			SendMessage(sb.toString());
			Log.e(TAG, " WS Login()=======ws 登录");


	}


	
	public  static void checkOnLine(){
		try {
			//你自己的协议
			JSONObject object=new JSONObject();

			Log.e(TAG, object.toString());
			SendMessage(object.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			return ;
		}
		
		
		
	}



	

}
