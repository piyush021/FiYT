package com.newgendevelopers3.fiyt;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class Background extends Service {
    private MyWebView wv;
    private LinearLayout view;

    private final IBinder mBinder = new LocalBinder();

        public class LocalBinder extends Binder {
            Background getService() {
                return Background.this;
            }
        }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Deprecated
    @Override
    public void onCreate() {
        super.onCreate();

        WindowManager windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params=new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                ,WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                ,WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                ,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                ,PixelFormat.TRANSLUCENT);


        params.gravity= Gravity.TOP|Gravity.LEFT;
        params.x=0;
        params.y=0;
        params.width=0;//WindowManager.LayoutParams.MATCH_PARENT;
        params.height=0;//WindowManager.LayoutParams.MATCH_PARENT;

        view=new LinearLayout(this);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        windowManager.addView(view,params);

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(this, 0,notificationIntent , 0);

        Notification notification;

        if (Build.VERSION.SDK_INT < 16) {
            notification= new Notification.Builder(this).getNotification();
        } else {
            notification= new Notification.Builder(this).build();
        }

        startForeground(1024, notification);

    }


    public void removeFromBackground(){
        view.removeView(wv);
    }

    public void playInBackground(MyWebView w){
        wv=w;
        view.addView(wv);
        wv.onResume();
        wv.resumeTimers();
        wv.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");
    }

    public void quit(){
        if(wv!=null) {
            view.removeView(wv);
            wv.clearHistory();
            wv.clearCache(true);
            wv.loadUrl("about:blank");
            wv.onPause();
            wv.pauseTimers();
            wv = null;
        }
        stopForeground(true);
        stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void forceStop(){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
