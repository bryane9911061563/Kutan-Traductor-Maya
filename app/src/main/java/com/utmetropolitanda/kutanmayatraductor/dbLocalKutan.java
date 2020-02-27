package com.utmetropolitanda.kutanmayatraductor;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class dbLocalKutan extends SQLiteOpenHelper {
    public dbLocalKutan(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table Traducciones_recientes1(_iduser text, traduccionespan text, traduccionmaya text)");
        }
        catch (Exception e){
            String cadena;
            try {
                db.execSQL("Select * from Traducciones_recientes1")
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            Log.d("**********",);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {


    }
}
