package com.passeapp.dark_legion.cradioapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;


public class RadioService extends Service{
    private MediaPlayer mediaPlayer;
    private String streamURL = "http://vm1.hostingcaminointernet.com:9934/stream.mp3";
    public static boolean prepared = false;
    public static boolean started = false;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private int headsetSwitch = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //generateMusicStreamNotification();
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

    }

    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("state")){
                if(headsetConnected && intent.getIntExtra("status",0)==1){
                    headsetConnected = false;
                    headsetSwitch = 0;
                }else if(!headsetConnected && intent.getIntExtra("status",0)==1){
                    headsetConnected = true;
                    headsetSwitch = 1;
                }
            }

            switch (headsetSwitch){
                case 0:
                    if(mediaPlayer != null && prepared){
                        mediaPlayer.pause();
                    }
                    break;
                case 1:
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(mediaPlayer != null && prepared){
                            mediaPlayer.pause();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(mediaPlayer != null && prepared){
                            if(isPausedInCall){
                                isPausedInCall = false;
                                mediaPlayer.start();
                            }
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i("stream info","service STARTFOREGROUND_ACTION");
            generateMusicStreamNotification();
        } else if (intent.getAction().equals(Constants.ACTION.MAIN_ACTION)){
            Log.i("stream info","service MAIN_ACTION");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            new PlayerTask().execute();

        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i("stream info","service PLAY_ACTION");
            if(prepared && !started && mediaPlayer != null){
                mediaPlayer.start();
                started = true;
            }
        } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {
            Log.i("stream info","service PAUSE_ACTION");
            if(prepared && started && mediaPlayer != null){
                mediaPlayer.pause();
                started = false;
            }
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i("stream info","service STOPFOREGROUND_ACTION");
            stopForeground(true);
            //stopSelf();
        } else if (intent.getAction().equals(Constants.ACTION.CLOSE_ACTION)) {
            Log.i("stream info","service CLOSE_ACTION");
            if(prepared && mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer = null;
                prepared = false;
                started = false;
            }
            stopForeground(true);
            //closeAplication();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            if(started){
                mediaPlayer.stop();
            }
            mediaPlayer.release();

        }
        if(phoneStateListener != null){
            telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_NONE);
        }

        unregisterReceiver(headsetReceiver);
    }


    public class PlayerTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            MainActivity.isReadyStream = aBoolean;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if(OnlineConnectClass.isOnline(getApplicationContext())){
                try {
                    mediaPlayer.setDataSource(streamURL);
                    mediaPlayer.prepare();
                    Log.i("stream info","stream prepared");
                    prepared = true;
                } catch (Exception e) {
                    Log.i("stream info","error stream "+ e.getLocalizedMessage());
                }
            }
            return prepared;
        }
    }

    public void generateMusicStreamNotification(){
        //Intent intent = new Intent();
        //PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent intentPlay = new Intent(this,RadioService.class);
        intentPlay.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pendingIntentPlay = PendingIntent.getService(this, 0, intentPlay, 0);

        Intent intentStop = new Intent(this,RadioService.class);
        intentStop.setAction(Constants.ACTION.PAUSE_ACTION);
        PendingIntent pendingIntentStop = PendingIntent.getService(this, 0, intentStop, 0);

        Intent intentClose = new Intent(this,RadioService.class);
        intentClose.setAction(Constants.ACTION.CLOSE_ACTION);
        PendingIntent pendingIntentClose = PendingIntent.getService(this, 0, intentClose, 0);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setTicker("CRadio")
                .setContentTitle("CRadio.ec")
                .setContentText("Escuchando en VIVO")
                .setSmallIcon(R.drawable.ic_stat_equalizer)
                .addAction(R.drawable.ic_stat_play_circle_filled,"Play",pendingIntentPlay)
                .addAction(R.drawable.ic_stat_pause_circle_filled,"Stop",pendingIntentStop)
                .addAction(R.drawable.ic_stat_close,"Close",pendingIntentClose)
                .setContentIntent(pendingIntent).build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        startForeground(2525,notification);
        //NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //manager.notify(0,notification);

    }

    private void closeAplication() {
        Intent intent = new Intent("closing-app");
        // You can also include some extra data.
        intent.putExtra("message", "Closing APP");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
