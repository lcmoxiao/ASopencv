package com.example.wocaowocao.witch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class elfDataBaseManager extends SQLiteOpenHelper {

    public int[] downXs,downYs,xs,ys;
    public int[] types;
    public Bitmap[] bitmaps;

    //(int type,int x1,int y1,int x2,int y2,blob img)
    public void getInitData(int MovNub)
    {
        int size = getMovSize(MovNub);
        if(size == 0)return;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("MOV"+MovNub, new String[]{"type","x1","y1","x2","y2"}, null, null, null, null, null);
        types = new int [size];
        downXs  = new int [size];
        downYs = new int [size];
        xs = new int [size];
        ys = new int [size];
        for(int id=0 ; id < size ; id++)
        {
            cursor.moveToNext();
            types[id] = cursor.getInt(0);logxx(types[id]+"");
            downXs[id] = cursor.getInt(1);logxx(downXs[id]+"");
            downYs[id] = cursor.getInt(2);logxx(downYs[id]+"");
            if(types[id]==2) {//滑动手势
                xs[id] = cursor.getInt(3);logxx(xs[id]+"");
                ys[id] = cursor.getInt(4);logxx(ys[id]+"");
            }else {
                xs[id] = -1;
                ys[id] = -1;
            }
        }
        cursor.close();

        cursor = db.query("MOV"+MovNub, new String[]{"img"}, null, null, null, null, null);
        bitmaps = new Bitmap [size];
        for(int id=0 ; id < size ; id++)
        {
            cursor.moveToNext();
            byte[] imgdata = cursor.getBlob(0);
            bitmaps[id] = BitmapFactory.decodeByteArray(imgdata,0,imgdata.length);
         }
        cursor.close();
        db.close();
    }

    public int getMovNub()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from sqlite_master where type='table'",null);
        int MovNub = 0 ;
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0)-1;
            if (count > 0) {
                MovNub = count;
            } else {
                onCreate(db);
            }
        }
        cursor.close();
        db.close();
        return MovNub;
    }

    public int getMovSize(int MovNub)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("MOV"+MovNub, new String[]{"type"}, null, null, null, null, null);
        int size = cursor.getCount();
        cursor.close();
        db.close();
        return size;

    }

    public void create_MOV(int MovNub){
        SQLiteDatabase db = this.getWritableDatabase();
        if(isNotLivingTable(MovNub, db)) {
            String sql = "create table MOV"+MovNub+"(type integer,x1 integer ,y1 integer,x2 integer,y2 integer,img Blob)";
            db.execSQL(sql);
        }
        db.close();
    }

    public void delete_MOV(int MovNub)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!isNotLivingTable(MovNub, db)) {
            String sql = "drop table MOV"+MovNub;
            db.execSQL(sql);
        }
        db.close();
    }

    public void clear_MOV(int MovNub)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!isNotLivingTable(MovNub, db)) {
            String sql = "delete from MOV"+MovNub;
            db.execSQL(sql);
        }
        db.close();
    }

    public void add(int MovNub, int type, int x1, int y1, Bitmap img)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type",type);
        values.put("x1",x1);
        values.put("y1",y1);
        values.put("x2",-1);
        values.put("y2",-1);
        values.put("img",bitmapToBytes(img));
        db.insert("MOV"+MovNub,null,values);
        db.close();
    }

    public void add(int MovNub, int type, int x1, int y1,int x2, int y2, Bitmap img)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type",type);
        values.put("x1",x1);
        values.put("y1",y1);
        values.put("x2",x2);
        values.put("y2",y2);
        values.put("img",bitmapToBytes(img));
        db.insert("MOV"+MovNub,null,values);
        db.close();
    }

    private boolean isNotLivingTable(int MovNub, SQLiteDatabase db)
    {
        boolean b =false;
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='MOV" + MovNub + "';";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                b = true;
                Log.e("xxx","MOV" + MovNub +"数据表已经存在");
            } else {
                onCreate(db);
            }
        }
        cursor.close();
        return !b;
    }

    //图片转为二进制数据
    private byte[] bitmapToBytes( Bitmap bitmap){

        if(bitmap == null) {logxx("娘的");return new byte[0];}
        //将图片转化为位图
        int size = bitmap.getByteCount();
        logxx("size=" +size );
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            //将字节数组输出流转化为字节数组byte[]
            logxx("成功截图");
            return baos.toByteArray();
        }catch (Exception ignored){
        }finally {
            try {
                bitmap.recycle();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logxx("fuck");
        return new byte[0];
    }

    private void logxx(String str){
        Log.e("xx",str);
    }


    public elfDataBaseManager(@Nullable Context context) {
        super(context, "elf_db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
