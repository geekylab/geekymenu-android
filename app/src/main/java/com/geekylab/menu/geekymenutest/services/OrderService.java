package com.geekylab.menu.geekymenutest.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.geekylab.menu.geekymenutest.DashBoardActivity;
import com.geekylab.menu.geekymenutest.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.Arrays;

public class OrderService extends Service {
    private static final String TAG = "MyService";
    private static final int REQUEST_CODE_MAIN_ACTIVITY = 1234;
    private static final int NOTIFICATION_CLICK = 12345;
    Socket socket;
    final IBinder binder = new MyBinder();
    private String url;
    private String mStoreId;
    private String mTableId;
    private String mUserToken;
    private String mOrderToken;

    public void disconnect() {
        if (socket != null)
            socket.disconnect();
    }

    public boolean isConnected() {
        if (null == socket) {
            return false;
        }
        Log.d(TAG, "isConnected");
        return socket.connected();
    }

    public Socket connect() {
        if (socket != null) {
            return socket.connect();
        }
        tryToConnect();
        return socket;
    }

    public void emit(String event) {
        if (socket != null && socket.connected()) {
            socket.emit(event, "johna");
        }
    }

    public void setStoreId(String storeId) {
        mStoreId = storeId;
//        tryToConnect();
    }

    public void setTableId(String tableId) {
        mTableId = tableId;
//        tryToConnect();
    }

    public void setUserToken(String userToken) {
        mUserToken = userToken;
//        tryToConnect();
    }

    public void setUrl(String url) {
        this.url = url;
//        tryToConnect();
    }

    public void setTableToken(String order_token) {
        mOrderToken = order_token;
    }


    public class MyBinder extends Binder {
        public OrderService getService() {
            return OrderService.this;
        }
    }

    public OrderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OrderService : onCreate");
    }


    private void sendNotification() {
        // Intent の作成
        Intent intent = new Intent(OrderService.this, DashBoardActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                OrderService.this, REQUEST_CODE_MAIN_ACTIVITY, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // LargeIcon の Bitmap を生成
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        // NotificationBuilderを作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());
        builder.setContentIntent(contentIntent);
        // ステータスバーに表示されるテキスト
        builder.setTicker("Ticker");
        // アイコン
        builder.setSmallIcon(R.drawable.ic_call);
        // Notificationを開いたときに表示されるタイトル
        builder.setContentTitle("ContentTitle");
        // Notificationを開いたときに表示されるサブタイトル
        builder.setContentText("ContentText");
        // Notificationを開いたときに表示されるアイコン
        builder.setLargeIcon(largeIcon);
        // 通知するタイミング
        builder.setWhen(System.currentTimeMillis());
        // 通知時の音・バイブ・ライト
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);
        // タップするとキャンセル(消える)
        builder.setAutoCancel(true);

        // NotificationManagerを取得
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        // Notificationを作成して通知
        manager.notify(NOTIFICATION_CLICK, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tryToConnect();
        return super.onStartCommand(intent, flags, startId);
    }

    private void tryToConnect() {
        if (url != null &&
                mTableId != null &&
                mUserToken != null &&
                mStoreId != null &&
                mOrderToken != null) {

            if (socket == null) {
                try {
                    IO.Options opts = new IO.Options();
                    opts.query = "service_token=" + mUserToken + "&store_id=" + mStoreId + "&order_token=" + mOrderToken;
                    socket = IO.socket(url, opts);

                    socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, Arrays.toString(args));
                            sendNotification();
                        }
                    }).on("news", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, Arrays.toString(args));
                        }

                    }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, Arrays.toString(args));
                        }

                    }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, Socket.EVENT_CONNECT_ERROR + "error" + Arrays.toString(args));
                        }
                    });

                    socket.connect();

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                if (!socket.connected()) {
                    socket.connect();
                }
            }
        } else {
            Log.d(TAG, "invalid parameters");
        }
    }
}
