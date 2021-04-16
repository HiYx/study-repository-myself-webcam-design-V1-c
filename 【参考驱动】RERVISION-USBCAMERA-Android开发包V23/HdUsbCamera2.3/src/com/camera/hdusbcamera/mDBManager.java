
package com.camera.hdusbcamera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class mDBManager
{
    private mDBHelper helper;
    private SQLiteDatabase db;
    private String TAG = "mDBHelper";
    
    public mDBManager(Context context) 
    {
        Log.d(TAG, "DBManager -----------------------------> Constructor");
       
        helper = new mDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void add(List<mCParam> cparams) 
    {
       db.beginTransaction();
        try
        {
            for (mCParam cparam : cparams) 
            {
                        db.execSQL("INSERT INTO " + mDBHelper.TABLE_NAME
                        + " VALUES(?,?,?,?,?)", new Object[] {
                        cparam.get_id(), cparam.get_name(),
                        cparam.getMin_val(), cparam.getDef_val(),
                        cparam.getMax_val(),cparam.getUsr_val() });
                        Log.d(TAG, "DBManager -----------------------------> add------------"+cparam.get_name());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void add(mCParam cparam) 
    {
        db.beginTransaction();
        try
        {
            db.execSQL("INSERT INTO " + mDBHelper.TABLE_NAME
                    + " VALUES(?,?,?,?,?,?)", new Object[] {
                    cparam.get_id(), cparam.get_name(), 
                    cparam.getMin_val(), cparam.getDef_val(), 
                    cparam.getMax_val(), cparam.getUsr_val()
            });
           db.setTransactionSuccessful();
           Log.d(TAG, "DBManager --------------------------> add---one----------"+cparam.get_name());
        } finally 
        {
            db.endTransaction();
        }
    }

    public void update(mCParam cparam) 
    {
        Log.d(TAG, "DBManager --------------------------> updateAge");
        ContentValues cv = new ContentValues();
        cv.put("usr_val", cparam.getUsr_val());
        db.update(mDBHelper.TABLE_NAME , cv, "_name = ?",
                new String[] { cparam.get_name() });
    }

    public void deleteOldPerson(mCParam cparam,int id) 
    {
        Log.d(TAG, "DBManager ------------------------------> deleteOldPerson");
        db.delete(mDBHelper.TABLE_NAME , "age >= ?",
                new String[] {
                    String.valueOf(cparam.get_name()+id)
                });
    }

   

    public mCParam findByName(int id,String name) 
    {
        Log.d(TAG, "DBManager --------------------------------> query--------" + name);
        mCParam cparam = new mCParam();
        Cursor c = db.rawQuery("select * from "+mDBHelper.TABLE_NAME +" where _name=?", new String[] {
            name+id
        });
        if (c.moveToFirst()) 
        {   
            cparam.set_id(c.getInt(c.getColumnIndex("_id")));
            cparam.set_name(c.getString(c.getColumnIndex("_name")));
            cparam.setMin_val(c.getInt(c.getColumnIndex("min_val")));
            cparam.setDef_val(c.getInt(c.getColumnIndex("def_val")));
            cparam.setMax_val(c.getInt(c.getColumnIndex("max_val")));
            cparam.setUsr_val(c.getInt(c.getColumnIndex("usr_val")));
        }
        c.close();
        Log.d(TAG, "DBManager --------------------------------> query-------------" + cparam.get_name());
        return cparam;
    }
    
    public List<mCParam> find() 
    {
        List<mCParam> cparams = new ArrayList<mCParam>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            mCParam cparam = new mCParam();
            cparam.set_id(c.getInt(c.getColumnIndex("_id")));
            cparam.set_name(c.getString(c.getColumnIndex("_name")));
            cparam.setMin_val(c.getInt(c.getColumnIndex("min_val")));
            cparam.setDef_val(c.getInt(c.getColumnIndex("def_val")));
            cparam.setMax_val(c.getInt(c.getColumnIndex("max_val")));
            cparam.setUsr_val(c.getInt(c.getColumnIndex("usr_val")));
            cparams.add(cparam);
            Log.d(TAG, "DBManager ---------------------------> query----------------------all");
        }
        c.close();
        return cparams;
    }

    public boolean dataIsExist(int id,String name) 
    {
        boolean result =true;
        if (id==0)
            return true;
        Cursor cursor = querybyId(id,name);
        if (cursor.moveToNext())
            result =false;
        cursor.close();
        return result;
    }

    public Cursor queryTheCursor() 
    {
        Log.d(TAG, "DBManager ------------------------------------> queryTheCursor----------------all");
        Cursor c = db.rawQuery("SELECT * FROM " + mDBHelper.TABLE_NAME , null);
        return c;
    }
    

    public Cursor querybyId(int id,String name) 
    {
        Log.d(TAG, "DBManager ------------------------------------> queryTheCursor--------------name");
        Cursor c = db.rawQuery("SELECT * FROM " + mDBHelper.TABLE_NAME +" where _name=?",new String[]{name+id});
        return c;
    }

    public void closeDB() 
    {
        Log.d(TAG, "DBManager ------------------------------------> closeDB");
        db.close();
    }

}
