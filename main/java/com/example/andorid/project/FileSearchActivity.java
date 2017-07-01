package com.example.andorid.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by 28243 on 2017/1/14.
 */
public class FileSearchActivity extends AppCompatActivity {

    private File file;
    private String path;
    private String filename;
    private String info;
    private String key; //关键字
    private TextView result; // 显示结果
    private EditText et; // 编辑view
    private Button search_btn; // button view
    private Button add_file;
    private int book_id;
    private Handler mHandler;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filesearch);
        result = (TextView)findViewById(R.id.TextView_result);
        et = (EditText)findViewById(R.id.key);
        search_btn = (Button)findViewById(R.id.search);
        add_file = (Button)findViewById(R.id.add_file);
        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.file_toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        file = new File("/sdcard/");
        info = getString(R.string.info);

        //用sharePreferences保存书籍id
        final SharedPreferences preferences = getSharedPreferences("books", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        book_id =  preferences.getInt("books_id",1);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        count = 0;

        //设置监听器，当按下按钮时导入监听器
        add_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String table_name = "novel"+book_id;
                myDB DB = new myDB(getApplication(), table_name);
                boolean ans = DB.querybooksByName(filename);
                if (ans) { //书籍存在时弹出对话框提醒
                    Toast.makeText(getApplicationContext(),"书籍已存在",Toast.LENGTH_SHORT).show();
                }
                else { //否则导入书籍
                    DB.Create_table();
                    DB.insertbooks2DB(filename,table_name,"00");
                    if(name.equals("暂无书籍") && count == 0) {
                        count++;
                        DB.Create_table();
                        DB.insertbooks2DB(filename,table_name,"00");
                    }
                    depart_page(DB, path);
                    book_id = book_id + 1;
                    editor.putInt("books_id",book_id); //保存下一本书籍的id
                    editor.commit();
                }
            }
        });

        //设置文件搜索监听器
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path = "";
                result.setText("文件搜索中");
                result.setVisibility(View.VISIBLE);
                key = et.getText().toString();

                if (key.equals("")) { //当搜索关键字为空时，弹出“请输入关键字”
                    Toast.makeText(getApplication(), getString(R.string.pleaseInput), Toast.LENGTH_LONG).show();
                } else { //否则在sd卡中搜索文件

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            search(file);
                            Message message = new Message();
                            if(path.equals("")) { //当未找到文件是发送消息2
                                message.what = 2;
                                mHandler.sendMessage(message);
                            }
                            else { //当找到文件时发送消息1
                                message.what = 1;
                                mHandler.sendMessage(message);
                            }
                        }
                    }).start();
                }

                //根据收到的消息显示信息
                mHandler = new Handler() {
                    public void handleMessage (Message message) {
                        switch (message.what) {
                            case 1:
                                result.setText(info+path);
                                add_file.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                add_file.setVisibility(View.INVISIBLE);
                                result.setText(getString(R.string.notFound));
                                break;
                        }
                    }
                };
            }
        });

    }

    //分章
    public void depart_page(myDB DB,String novel_path)
    {
        try {
            File urlFile = new File(novel_path);
            //打开文件
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "GB2312");
            BufferedReader br = new BufferedReader(isr);
            String chapter = "";
            String last_chapter = "开始";
            StringBuilder chapter_deatil = new StringBuilder("");
            String mimeTypeLine = null ;
            //逐行读入文件
            while ((mimeTypeLine = br.readLine()) != null)
            {
                //找“第”和“章”
                chapter = find_chapter(mimeTypeLine);
                if(!chapter.isEmpty()) {
                    //插入章节和内容到数据库
                    DB.insert2DB(last_chapter, chapter_deatil.toString());
                    last_chapter = chapter;
                    //清空章节内容
                    chapter_deatil.replace(0, chapter_deatil.length(), "");
                }
                chapter_deatil.append(mimeTypeLine + "\n");
            }
            //没有章节的时候
            if(!chapter_deatil.equals("")){
                DB.insert2DB(last_chapter, chapter_deatil.toString());
                DB.insert2DB(last_chapter, chapter_deatil.toString());
            }
            Toast.makeText(getApplicationContext(),"书籍已导入",Toast.LENGTH_SHORT).show();
        }
        catch (IOException ex)
        {
            Toast.makeText(getApplicationContext(),"Fail to load file",Toast.LENGTH_SHORT).show();
        }
    }

    //分章
    public String find_chapter(String str)
    {
        String chapter = "";
        int start = str.indexOf("第");
        int end = str.indexOf("章");

        if(start != -1 && end != -1 && end - start < 10) chapter = str.substring(start,end+1);

        return chapter;
    }

    //搜索文件
    private void search(File fileold)
    {
        try{
            File[] files=fileold.listFiles();
            if(files.length>0)
            {
                for(int j=0;j<files.length;j++)
                {
                    if(!files[j].isDirectory())
                    {
                        if(files[j].getName().indexOf(key)> -1 && files[j].getName().toUpperCase().indexOf("TXT")> -1)
                        {
                            path =  files[j].getPath(); //记录文件名和路径
                            filename = files[j].getName();
                        }
                    }
                    else{
                        this.search(files[j]);
                    }
                }
            }
        }
        catch(Exception e)
        {
        }
    }
}
