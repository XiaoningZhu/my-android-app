package com.example.andorid.project;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/1/13 0013.
 */
public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "friend";
    private String TABLE_NAME = "";
    private String TABLE_NAME1 = "books";
    private static final int DB_version = 1 ;

    public myDB(Context context, String table){
        super(context,DB_NAME,null,DB_version);
        TABLE_NAME = table;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        //创建数据库
        String CREATE_TABLE1 ="CREATE TABLE if not exists books (_id INTEGER PRIMARY KEY,book_name TEXT, table_name TEXT, time TET)";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
    }

    public void Create_table() {
        SQLiteDatabase db = getWritableDatabase();
        String CREATE_TABLE ="CREATE TABLE if not exists "
                +TABLE_NAME
                +" (_id INTEGER PRIMARY KEY,chapter TEXT, chapter_detail TEXT)";
        db.execSQL(CREATE_TABLE);
        db.close();
    }

    public void Drop_table(String table_name) {
        SQLiteDatabase db = getWritableDatabase();
        String CREATE_TABLE ="DROP TABLE if exists " + table_name;
        db.execSQL(CREATE_TABLE);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i, int i1){

    }
    /*****************************对书籍相关信息操作***********************************************************/
    //删除数据
    public int Delbooks2DB(String book_name) {
        SQLiteDatabase db = getWritableDatabase();
        int id = db.delete(TABLE_NAME1, "book_name=?",new String[]{book_name});
        return id;
    }

    //插入记录
    public void insertbooks2DB(String book_name, String table_name, String time){
        //对数据库进行写操作
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("book_name",book_name);
        cv.put("table_name",table_name);
        cv.put("time",time);
        db.insert(TABLE_NAME1,null,cv);
        db.close();
    }

    public Cursor getbooksAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME1,new String[]{"_id","book_name","table_name","time"},null,null,null,null,null);
        return cursor;
    }

    //通过名字判断数据库中，是否有这条记录了
    public boolean querybooksByName(String name){
        boolean in = false;
        String chapter_de="";
        //对数据库进行读操作
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME1,new String[]{"_id","book_name","table_name"},null,null,null,null,null);
        //遍历数据库记录
        if(cursor.moveToFirst()){
            while(cursor.moveToNext()){
                //找到名字对应的列
                int namecol = cursor.getColumnIndex("book_name");
                //获取名字的具体内容
                String na = cursor.getString(namecol).toString();
                //判断获取的名字与当前的名字书否相等
                if(na.equals(name)){
                    in = true;
                }
            }
        }
        cursor.close();
        db.close();
        return in;
    }

    public int get_bookstotal_number()
    {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME1,new String[]{"_id","book_name","table_name","time"},null,null,null,null,null);
        //遍历数据库记录
        if(cursor.moveToFirst()){
            while(cursor.moveToNext())
            {
                count++;
            }
        }
        cursor.close();
        db.close();
        return count;
    }
    /*****************************对书籍内容操作***************************************************************/
    //插入记录
    public void insert2DB(String chapter, String chapter_detail){
        //对数据库进行写操作
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("chapter",chapter);
        cv.put("chapter_detail",chapter_detail);
        db.insert(TABLE_NAME,null,cv);
        db.close();
    }

    //获取数据库所有的记录
    public Cursor getAll() {
        //对数据库进行读操作
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"_id","chapter","chapter_detail"},null,null,null,null,null);
        return cursor;
    }

    public String queryByPosition(int position)
    {
        int count = 0;
        String result="";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"_id","chapter","chapter_detail"},null,null,null,null,null);
        //遍历数据库记录
        if(cursor.moveToFirst()){
            while(cursor.moveToNext())
            {
                if(position == count){
                    //找到名字对应的列
                    int namecol = cursor.getColumnIndex("chapter_detail");
                    result = cursor.getString(namecol).toString();
                    break;
                }
                count++;
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public int get_total_number()
    {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"_id","chapter","chapter_detail"},null,null,null,null,null);
        //遍历数据库记录
        if(cursor.moveToFirst()){
            while(cursor.moveToNext())
            {
                count++;
            }
        }
        cursor.close();
        db.close();
        return count;
    }

    //通过名字判断数据库中，是否有这条记录了
    public boolean queryByName(String name){
        boolean in = false;
        String chapter_de="";
        //对数据库进行读操作
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"_id","chapter","chapter_detail"},null,null,null,null,null);
        //遍历数据库记录
        if(cursor.moveToFirst()){
            while(cursor.moveToNext()){
                //找到名字对应的列
                int namecol = cursor.getColumnIndex("chapter");
                //获取名字的具体内容
                String na = cursor.getString(namecol).toString();
                //判断获取的名字与当前的名字书否相等
                if(na.equals(name)){
                    in = true;

                }
            }
        }
        cursor.close();
        db.close();
        return in;
    }
}

