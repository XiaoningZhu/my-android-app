package com.example.andorid.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GridView bookShelf;
    private int[]data = {
            R.drawable.cover_txt
    };;
    private String[]name = {"暂无书籍"};
    private String[]tablename;
    private myDB DB;
    private ShelfAdapter shelfAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //设置ToolBar可用

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MusiclistActivity.class);
                startActivity(intent); //跳转到音乐界面
            }
        });

        //通过ActionBarDraweToggle，打开和关闭导航
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //设置导航界面的监听器
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        /*********************************************/
        DB = new myDB(MainActivity.this, "book");
        LoadData(); //加载书籍数据

        bookShelf = (GridView) findViewById(R.id.bookSelf);
        shelfAdapter = new ShelfAdapter();
        bookShelf.setAdapter(shelfAdapter); //设置书架监听器和适配器
        bookShelf.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击书籍，跳转到对应的目录
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i >= data.length) {
                }
                else {
                    //Toast.makeText(getApplicationContext(), ""+l, Toast.LENGTH_SHORT).show();
                    if (name[i].equals("暂无书籍")) {
                        Toast.makeText(getApplicationContext(),"请先导入书籍",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, BooklistActivity.class);
                        intent.putExtra("table_name",tablename[i]);
                        startActivity(intent);
                    }
                }
            }
        });
        bookShelf.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //长按书籍，删除
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(i >= data.length) {
                }
                else {
                    //Toast.makeText(getApplicationContext(), ""+l, Toast.LENGTH_SHORT).show();
                    if (name[i].equals("暂无书籍")) {
                        Toast.makeText(getApplicationContext(),"没有书籍可以删除",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("删除提示框").setMessage("确认删除该文件？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        DB.Drop_table(tablename[i]);
                                        DB.Delbooks2DB(name[i]);
                                        LoadData();
                                        shelfAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(),"书籍已删除",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getApplicationContext(),"取消删除",Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
                    }
                }
                return false;
            }
        });

    }

    public void LoadData() { //加载数据
        int sum = DB.get_bookstotal_number();
        Log.d("LoadData:            ", String.valueOf(sum));
        if (sum >= 1) {

            name = new String[sum];
            data = new int[sum];
            tablename = new String[sum];
            int position = 0;
            Cursor cursor = DB.getbooksAll();
            if(cursor.moveToFirst()){
                while(cursor.moveToNext())
                {
                    int booknamecol = cursor.getColumnIndex("book_name");
                    name[position] = cursor.getString(booknamecol).toString();
                    int tablenamecol = cursor.getColumnIndex("table_name");
                    tablename[position] = cursor.getString(tablenamecol).toString();
                    data[position] = R.drawable.cover_txt;
                    position++;
                }
            }
            cursor.close();
        }
        else {
            name = new String[1];
            data = new int[1];
            name[0] = "暂无书籍";
            data[0] = R.drawable.cover_txt;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData(); //加载数据并更新界面
        shelfAdapter.notifyDataSetChanged();
    }

    /***************************************grid适配器***************************************************/
class ShelfAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length+5;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup arg2) { //书架界面显示
        // TODO Auto-generated method stub

        contentView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.cover_item, null);

        TextView view=(TextView) contentView.findViewById(R.id.coverView);
        if(data.length>position){
            if(position<name.length){
                view.setText(name[position]);
            }
            view.setBackgroundResource(data[position]);
        }else{
            view.setBackgroundResource(data[0]);
            view.setClickable(false);
            view.setVisibility(View.INVISIBLE);
        }
        return contentView;
    }

}

/**************************************************************************************************/
    @Override
    public void onBackPressed() { //ToolBar的返回键函数重写
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //设置菜单
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //设置菜单监听器
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_settings1) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FileSearchActivity.class);
            intent.putExtra("name",name[0]);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //设置导航菜单监听器
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.account) {
            // Handle the camera action
        } else if (id == R.id.sign_in) {

        } else if (id == R.id.Book_self) {

        } else if (id == R.id.zone) {

        } else if (id == R.id.set) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
