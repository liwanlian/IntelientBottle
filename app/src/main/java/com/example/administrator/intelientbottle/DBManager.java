package com.example.administrator.intelientbottle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by Administrator on 2018/8/14.
 */
//对数据库的数据进行操作的类
public class DBManager {
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase db;
   private String table4="LYsql";


    public DBManager(Context context){
        myDatabaseHelper=new MyDatabaseHelper(context);
        db=myDatabaseHelper.getWritableDatabase();
    }
  //添加新数据
    public void addnewdata(int id,String address,String name,String date,double water,int standardtempture){
        db.beginTransaction();//开始事务
        try{
            ContentValues cv=new ContentValues();
            cv.put("id",id);
            cv.put("name",name);
            cv.put("standardwater",water);
            cv.put("address",address);
            cv.put("date",date);
            cv.put("standardtempture",standardtempture);
            cv.put("watered",0);
            cv.put("e_moring",0);
            cv.put("moring",0);
            cv.put("noon",0);
            cv.put("night",0);
            db.insert(table4,"id",cv);
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }
    //今天已喝水和当天某阶段喝水量的更新
    public void updatewater(String address,String date,int flag,double shuiliang){
        int id;
         String[] timearray={"e_moring","moring","noon","night"};
        db.beginTransaction();//开始事务
            try{
                double result=0.0;
                double resule1=0.0;
                Cursor c=db.rawQuery("select * from "+table4,null);
                for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                    if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
                        id=searchid(address,date);
                         result=shuiliang+c.getDouble(flag+6);
                         resule1=shuiliang+c.getDouble(6);
                        ContentValues cv=new ContentValues();
                        cv.put("watered",resule1);
                        cv.put(timearray[flag-1],result);
                        db.update(table4,cv,"id="+id,null);
                        break;
                    }
                }
                c.close();
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
    }
    //查找某地址和某日期对应的id
    public int searchid(String address,String date){
        int result=0;
        Cursor c =db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
                result=c.getInt(0);
            }
        }
        c.close();
        return result;
    }
    //更新标准的喝水量
    public void updatestand(String address,String date,double standard){
        db.beginTransaction();//开始事务
        int id;
        id=searchid(address,date);
        try{
            Cursor c=db.rawQuery("select * from "+table4,null);
            for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
                    ContentValues cv=new ContentValues();
                    cv.put("standardwater",standard);
                    db.update(table4,cv,"id="+id,null);
                    break;
                }
            }
            c.close();
            db.setTransactionSuccessful();

        }finally {
            db.endTransaction();
        }
    }
    //更新标准温度
    public void updatetemptuare(String address,String date,int standard){
        db.beginTransaction();//开始事务
        int id;
        id=searchid(address,date);
        try{
            Cursor c=db.rawQuery("select * from "+table4,null);
            for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
                    ContentValues cv=new ContentValues();
                    cv.put("standardtempture",standard);
                    db.update(table4,cv,"id="+id,null);
                    break;
                }
            }
            c.close();
            db.setTransactionSuccessful();

        }finally {
            db.endTransaction();
        }
    }
//判断某蓝牙地址在某日期下是否已经存在数据条
    public boolean judge_existence(String address,String date){
        boolean bj=false;
        Cursor c=db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
               bj=true;
                break;
            }
            else
                bj=false;
        }
        c.close();
        return  bj;
    }
    //为插入数据库的数据条给出合适的id
    public  int countid(){
        return db.query(table4, null, null, null, null, null, null).getCount()+1;
    }
  //查找数据
    public double searchdata(String address,String date,int dataflag){
        double result=0.0;
        Cursor c =db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
                result=c.getDouble(dataflag);
            }
        }
        c.close();
        return result;
    }
    //获取某个阶段的喝水量
    public double receivewater(String address,String date,int flag){
        double result=0.0;
        Cursor c =db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)&&c.getString(3).equals(date)){
                result=c.getDouble(flag+6);
            }
        }
        c.close();
        return  result;
    }
    //找到合适的id
    public int[] searchproperid(String address){
        int[] result = new int[100];
        int i=0;
        Cursor c =db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)){
                result[i]=c.getInt(0);
                i++;
            }
        }
        int[] rr=new int[i];
        for (int y=0;y<i;y++){
            rr[y]=result[y];
        }
        c.close();
        return  rr;
}
//找到合适的标准喝水量
    public double searcharraystandardw(int id,String address){
    double result=0.0;

        Cursor c =db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)&&c.getInt(0)==id){
                result=c.getDouble(5);
            }
        }
        c.close();
    return  result;
}
//找到合适的已喝水量
    public double searcharraywatered(int id,String address){
        double result=0.0;

        Cursor c =db.rawQuery("select * from "+table4,null);
        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            if (c.getString(1).equals(address)&&c.getInt(0)==id){
                result=c.getDouble(6);

            }
        }
        c.close();
        return  result;
    }
    public void closedb(){
        db.close();
    }
}
