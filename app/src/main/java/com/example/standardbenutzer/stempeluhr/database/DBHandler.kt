package com.example.standardbenutzer.stempeluhr.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler(context: Context) : SQLiteOpenHelper(context, "StempeluhrDB", null, 3) {

    private val DATABASE_NAME = "STEMPELUHR_DB"
    private val DATABASE_VERSION = 2

    private val ENTRY_TABLE_NAME = "STEMPEL_ZEITEN"

    private val PRIMARY_KEY = "ID"
    private val DAY = "DAY"
    private val WORKLOAD = "WORKLOAD"
    private val WORKTIME = "WORKTIME"

    private var latestID = 0

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $ENTRY_TABLE_NAME($PRIMARY_KEY UNSIGNED BIG INT PRIMARY KEY, $DAY TEXT, $WORKTIME UNSIGNED BIG INT, $WORKLOAD UNSIGNED BIG INT);"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $ENTRY_TABLE_NAME")
        onCreate(db)
    }

    fun addEntry(entry : DatabaseEntry) {
        val db = this.readableDatabase

        val values = ContentValues()
        values.put(PRIMARY_KEY, latestID++)
        values.put(DAY, entry.getDate())
        values.put(WORKTIME, entry.getWorktime())
        values.put(WORKLOAD,entry.getWorkload())

        db.insert(ENTRY_TABLE_NAME, null, values)
        db.close()
    }

    fun getAllEntries() : ArrayList<DatabaseEntry> {
        val entries = ArrayList<DatabaseEntry>()
        val selectQuery = "SELECT * FROM $ENTRY_TABLE_NAME"

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery,null)

        if(cursor.moveToFirst()) {
            do {
                entries.add(DatabaseEntry(cursor.getString(1), cursor.getLong(2), cursor.getLong(3)))
            } while(cursor.moveToNext())
        }

        cursor.close()

        return entries
    }

}