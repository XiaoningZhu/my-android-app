package com.example.andorid.project;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewbookActivity extends Activity{
    /*private TextView booktitleTv;
    private TextView bookcontentTv;*/
    String table_name = "";
    private PageWidget pageWidget;
    private Bitmap curBitmap, nextBitmap;
    private Canvas curCanvas, nextCanvas;
    private BookPage bookpage ;
    float down_x = 0, down_y=0, up_x=0, up_y=0;
    private Chapter chapter = new Chapter();
    private int totNumChapt = 0;
    int flag=0;


    myDB db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chapter2);
        //final Chapter chapter = new Chapter();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        table_name = bundle.getString("table_name");
        int position = bundle.getInt("position");   //章节序号
        int pageNum = bundle.getInt("pageNum");    //章节内页码

        db = new myDB(ViewbookActivity.this, table_name);
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.window, null);
        // 创建PopupWindow对象
        final PopupWindow pop = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, false);
        pop.setHeight(300);      //界面高度
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);   //设置点击窗口外边窗口消失
        pop.setFocusable(true);     // 设置此参数获得焦点，否则无法点击


        chapter.setOrder(position);
        int order = position+1;
        chapter.setTitle("第"+order+"章");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int w = dm.widthPixels;
        final int h = dm.heightPixels;

        curBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        nextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        curCanvas = new Canvas(curBitmap);
        nextCanvas = new Canvas(nextBitmap);
        bookpage = new BookPage(w, h, chapter, db);

        bookpage.setPageNum1(pageNum);
        bookpage.setBgBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.bg3));

        bookpage.draw(curCanvas);

        pageWidget = new PageWidget(this, w, h);

        setContentView(pageWidget);
        pageWidget.setBitmaps(curBitmap, nextBitmap);

        pageWidget.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                // TODO Auto-generated method stub
                Log.i("mmmmmmmmm", "kkkkkkkkkkkkkkk");

                boolean ret = false;
                if (v == pageWidget) {

                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        pageWidget.abortAnimation();
                        pageWidget.calcCornerXY(e.getX(), e.getY());
                        down_x=e.getX();
                        down_y=e.getY();
                        pageWidget.Paging();
                        Log.i(bookpage.getPageNum()+"    oooooo", "sssssssss");
                        bookpage.draw(curCanvas);
                        if (pageWidget.DragToRight()) {
                            if (bookpage.prePage()) {
                                //pageWidget.Paging();
                                bookpage.draw(nextCanvas);
                            } else {
                                pageWidget.stopPaging();
                                //bookpage.draw(curCanvas);
                                return false;

                            }
                        } else {
                            if (bookpage.nextPage()) {
                                //pageWidget.Paging();
                                bookpage.draw(nextCanvas);
                            } else {
                                pageWidget.stopPaging();
                                return false;
                            }
                        }
                        //pageWidget.setBitmaps(curBitmap, nextBitmap);

                    }
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        up_x=e.getX();
                        up_y=e.getY();
                        //if(pageWidget.canShowSeekbar())

                        //当是点击事件而非滑动事件时
                        if (Math.abs(down_x-up_x)<3 && Math.abs(down_y-up_y)<3)
                        {
                            if (pop.isShowing()) {
                                // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                                pop.dismiss();
                            } else {
                                // 在指定位置显示窗口
                                pop.showAtLocation(v, Gravity.NO_GRAVITY, w / 2, h);
                            }
                        }
                    }
                    //
                    ret=pageWidget.onTouchEvent(e);

                    return ret;
                }
                return false;
            }

        });

        //变大变小字体
        final Button text_larger = (Button) view.findViewById(R.id.larger);
        final Button text_smaller = (Button) view.findViewById(R.id.smaller);
        text_larger.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bookpage.setLtextSize();
                bookpage.draw(curCanvas);
                if (bookpage.nextPage() && bookpage.prePage()){
                    bookpage.draw(nextCanvas);
                }

                pageWidget.postInvalidate();
                return false;
            }
        });
        text_smaller.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bookpage.setStextSize();
                if (bookpage.nextPage() && bookpage.prePage()){
                    bookpage.draw(nextCanvas);
                }

                pageWidget.postInvalidate();
                return false;
            }
        });

        final SeekBar seekBar = (SeekBar)view.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Log.i(progress+"mm", "kkkkkkkkk");
                    double percent = (double)progress/100;
                    Log.i(percent+"jj", "kkkkkkkkk");
                    bookpage.setPageNum(percent);
                    pageWidget.setBitmaps(curBitmap, nextBitmap);
                    Log.i("jjjjjjjj", "kkkkkkkkk");
                    bookpage.draw(curCanvas);
                    if (bookpage.nextPage() && bookpage.prePage()){
                        Log.i("bbbbbb", "kkkkkkkkk");
                        bookpage.draw(nextCanvas);
                    }
                    pageWidget.postInvalidate();
                }


            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //bookpage.draw(curCanvas);
            }

        });

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {// 在此处判断消息类型并更新UI
                    case 123:{
                        int totChapNum = db.get_total_number();
                        int progress = 100*(chapter.getOrder()+1)/totChapNum;
                        seekBar.setProgress(progress);   //进度条更新为当前时间
                    }

                    break;
                }

            }
        };

        Thread mThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    // 定义接收消息的Handler对象，并将消息加入队列
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.obtainMessage(123).sendToTarget();
                }
            }
        };
        mThread.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        int position1 = chapter.getOrder();
        int pageNum = bookpage.getPageNum();

        String w = position1 + " " + pageNum+" ";
        write_mark(w,table_name);
    }

    void write_mark(String position, String table_name)
    {
        FileOutputStream outputStream;
        try
        {
            outputStream = openFileOutput(table_name+"_data.txt",Context.MODE_PRIVATE);
            outputStream.write(position.getBytes());
            outputStream.close();
            Toast.makeText(getApplicationContext(),position,Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"save failed",Toast.LENGTH_SHORT).show();
        }
    }


}
