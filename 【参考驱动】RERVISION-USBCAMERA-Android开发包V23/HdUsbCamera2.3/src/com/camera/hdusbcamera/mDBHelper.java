package com.camera.hdusbcamera;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class mDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CameraParams.db";
    public static final String TABLE_NAME = "CameraParams_C";
    private String TAG="mDBHelper";
    

    public mDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
      
        Log.d(TAG, "mDBHelper--------- Constructor");
    }
 
    public mDBHelper(Context context, String name, CursorFactory factory,
            int version) 
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        Log.d(TAG, TABLE_NAME +"---------- onCreate-------------");
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[_name] TEXT,");
        sBuffer.append("[min_val] INTEGER,");
        sBuffer.append("[def_val] INTEGER,");
        sBuffer.append("[max_val] INTEGER,");
        sBuffer.append("[usr_val] INTEGER)");
        db.execSQL(sBuffer.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        
        Log.d(TAG, "mDBHelper---------- onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        Log.d(TAG, "mDBHelper----------- onOpen");
    }

}


