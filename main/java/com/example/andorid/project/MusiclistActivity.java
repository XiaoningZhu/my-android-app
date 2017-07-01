package com.example.andorid.project;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 28243 on 2016/12/14.
 */
public class MusiclistActivity extends Activity{

    MyService ms = new MyService();
    private static Resources res;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);

       FloatingActionButton stop = (FloatingActionButton)findViewById(R.id.quit);
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View  view) {
                ms.Playerstop();
            }
        });
        Context context = getApplicationContext();
        res = context.getResources();
        String name[] = res.getStringArray(R.array.music_name);
        final String musicPaths[] = res.getStringArray(R.array.music);
        ListView lv = (ListView)findViewById(R.id.musiclist_tv);

        final List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("name", name[i]);
            data.add(temp);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(MusiclistActivity.this, data, R.layout.music_item,
                new String[]{"name"}, new int[]{R.id.music_name});
        lv.setAdapter(simpleAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String path = musicPaths[position];
                ms.Playerplay(path);
            }
        });
        final Intent intent = new Intent(this, MyService.class);
        startService(intent);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);

    }
    private void connection(){
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ms = ((MyService.MyBinder)service).getService();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            sc=null;
        }
    };
}
