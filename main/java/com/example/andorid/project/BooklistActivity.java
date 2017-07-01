package com.example.andorid.project;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 28243 on 2016/12/14.
 */
public class BooklistActivity extends Activity {

    String path;
    SimpleAdapter simpleAdapter;
    List<Map<String, Object>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist_activity);

        //获取小说对应的表名
        Intent intent = getIntent();
        final String table_name = intent.getStringExtra("table_name");

        //为单个小说创建一个数据库
        myDB DB = new myDB(BooklistActivity.this, table_name);
        //得到小说的章节目录
        get_content(DB);

        ListView lv = (ListView) findViewById(R.id.list_view);
        //点击章节
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BooklistActivity.this,ViewbookActivity.class);
                Bundle bundle = new Bundle();
                //获得章节的名字
                String chapter_name = data.get(position).get("chapter").toString();

                int pagenum=0;
                //如果点击标签
                if(chapter_name.equals("当前书签"))
                {
                    String w = "";
                    //读取标签
                    w = read_mark(table_name);
                    //若存在标签
                    if(!w.isEmpty()){
                        String word[] = w.split(" ");
                        //章节数
                        position = Integer.parseInt(word[0]);
                        //页码
                        pagenum = Integer.parseInt(word[1]);
                        bundle.putString("table_name",table_name);
                        bundle.putString("chapter",chapter_name);
                        bundle.putSerializable("pageNum",pagenum);
                        bundle.putSerializable("position",position);

                        intent.putExtras(bundle);
                        //跳转到对应的页数
                        BooklistActivity.this.startActivity(intent);
                    }
                }
                else{
                    //直接到达对应章节的第一页
                    bundle.putString("table_name",table_name);
                    bundle.putString("chapter",chapter_name);
                    bundle.putSerializable("pageNum",pagenum);
                    bundle.putSerializable("position",position);

                    intent.putExtras(bundle);
                    BooklistActivity.this.startActivity(intent);
                }

            }
        });

    }

    //找章节
    public String find_chapter(String str)
    {
        String chapter = "";
        int start = str.indexOf("第");
        int end = str.indexOf("章");
        //在str中寻找“第”和“章”，并且两者的距离少于10个字符
        if(start != -1 && end != -1 && end - start < 10) chapter = str.substring(start,end+1);

        return chapter;
    }

    //获取章节目录
    public void get_content(myDB DB)
    {
        data = new ArrayList<>();
        Cursor cursor = DB.getAll();
        //遍历数据库，获取章节
        if(cursor.moveToFirst()){  //到第一个数据库记录
            while(cursor.moveToNext()){ //逐个读取数据库记录

                int chapter = cursor.getColumnIndex("chapter");
                Map<String,Object>tmp = new LinkedHashMap<>();
                tmp.put("chapter", cursor.getString(chapter).toString());
                data.add(tmp);
            }
        }
        cursor.close();

        Map<String,Object> tmp = new LinkedHashMap<>();
        tmp.put("chapter", "当前书签");
        data.add(tmp);
        ListView lv = (ListView) findViewById(R.id.list_view);
        simpleAdapter = new SimpleAdapter(this, data, R.layout.booklist_item,
                new String[]{"chapter"}, new int[]{R.id.txt});
        lv.setAdapter(simpleAdapter);
    }

    //读取标签
    public String read_mark(String table_name)
    {
        String pos = "";
        try {
            //读取对应的小说标签的文件
            FileInputStream fileInputStream= openFileInput(table_name+"_data.txt");

            byte[] contents = new byte[fileInputStream.available()];
            fileInputStream.read(contents);
            pos = new String(contents,"utf-8");
            fileInputStream.close();

        }
        catch (IOException ex)
        {
            Toast.makeText(getApplicationContext(),"暂无书签",Toast.LENGTH_SHORT).show();
        }

        return pos;
    }

}
