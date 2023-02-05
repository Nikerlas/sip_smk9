package com.smkn9.semarang.sip_smk9.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

//class DatabaseHelper {
//}

class DatabaseHelper(context: Context): ManagedSQLiteOpenHelper(context, com.smkn9.semarang.sip_smk9.helper.Constant.DATABASE_SOAL_NAME,null,1) {
    companion object {
        private var instance :DatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context):DatabaseHelper{
            if(instance == null){
                instance = DatabaseHelper(context.applicationContext)
            }

            return instance as DatabaseHelper
        }
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
                Constant.TABLE_SOAL,
                true,
                Constant.COLUMN_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                Constant.COLUMN_NO_SOAL to INTEGER,
                Constant.COLUMN_SOAL to TEXT,
                Constant.COLUMN_SOAL_GAMBAR to TEXT,
                Constant.COLUMN_SOAL_VIDEO to TEXT,
                Constant.COLUMN_JAWABAN_A to TEXT,
                Constant.COLUMN_JAWABAN_B to TEXT,
                Constant.COLUMN_JAWABAN_C to TEXT,
                Constant.COLUMN_JAWABAN_D to TEXT,
                Constant.COLUMN_JAWABAN_E to TEXT,
                Constant.COLUMN_JAWABAN_A_GAMBAR to TEXT,
                Constant.COLUMN_JAWABAN_B_GAMBAR to TEXT,
                Constant.COLUMN_JAWABAN_C_GAMBAR to TEXT,
                Constant.COLUMN_JAWABAN_D_GAMBAR to TEXT,
                Constant.COLUMN_JAWABAN_E_GAMBAR to TEXT,
                Constant.COLUMN_JAWABAN_FINAL to TEXT
        )

        db.createTable(
                Constant.TABLE_SOAL_ESSAY,
                true,
                Constant.COLUMN_ID_ESSAY to INTEGER + PRIMARY_KEY + UNIQUE,
                Constant.COLUMN_NO_SOAL_ESSAY to INTEGER,
                Constant.COLUMN_SOAL_ESSAY to TEXT,
                Constant.COLUMN_SOAL_GAMBAR_ESSAY to TEXT,
                Constant.COLUMN_JAWABAN_FINAL_ESSAY to TEXT
        )






    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(Constant.TABLE_SOAL,true)
        db.dropTable(Constant.TABLE_SOAL_ESSAY,true)

    }
}

val Context.database
    get() = DatabaseHelper.getInstance(applicationContext)