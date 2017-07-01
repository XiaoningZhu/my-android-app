package com.example.andorid.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by 28243 on 2016/12/14.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        gotoHome();
    }

    public void gotoHome() { //延迟1s在跳转
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.setClass(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}
