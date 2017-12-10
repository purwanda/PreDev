package com.example.kanjeng.predev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kanjeng on 12/10/2017.
 */

public class DB_Controller extends SQLiteOpenHelper {

    public DB_Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "TEST.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USER(EMAIL TEXT UNIQUE,PASSWORD TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS STUDENTS;");
        onCreate(sqLiteDatabase);
    }

    public void insert_email(String email,String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL",email);
        contentValues.put("PASSWORD",password);
        this.getWritableDatabase().insertOrThrow("USER","",contentValues);
    }

    public void list_email(TextView textView){
        Log.d("DB_Controller", "list_email : mulai");
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * from USER",null);
        textView.setText("");
        while (cursor.moveToNext()){
            textView.append(cursor.getString(0) +" "+cursor.getString(1)+"\n" );
        }
    }
}
