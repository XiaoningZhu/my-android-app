package com.example.andorid.project;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

/**
 * Created by 28243 on 2016/12/14.
 */
public class MyService extends Service {


    public MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    public void onCreate() {

        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;

    }

    private IBinder myBinder = new MyBinder();
    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }

    public void Playerplay(String path)
    {
        try{
            AssetManager am = getAssets();
            AssetFileDescriptor afd = am.openFd(path);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());

            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }
    public int get_playing()
    {
        if(mediaPlayer.isPlaying()) return 1;
        else return 0;
    }

    public void Playerstop()
    {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Playerquit(){
        mediaPlayer.release();
    }
}
