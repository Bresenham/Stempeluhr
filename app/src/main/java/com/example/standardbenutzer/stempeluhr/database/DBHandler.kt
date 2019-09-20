package com.example.standardbenutzer.stempeluhr.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.LUNCHBREAK_6H
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.LUNCHBREAK_9H
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.NINE_HOURS
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.SIX_HOURS
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.formatDateToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.formatStringToDate

class DBHandler(context: Context) : SQLiteOpenHelper(context, "StempeluhrDB", null, 8) {

    private val DATABASE_NAME = "STEMPELUHR_DB"
    private val DATABASE_VERSION = 8

    private val ENTRY_TABLE_NAME = "STEMPEL_ZEITEN"

    private val PRIMARY_KEY = "ID"
    private val DAY = "DAY"
    private val WORKTIME = "WORKTIME"
    private val WORKLOAD = "WORKLOAD"

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $ENTRY_TABLE_NAME($PRIMARY_KEY INTEGER PRIMARY KEY AUTOINCREMENT, $DAY TEXT, $WORKTIME UNSIGNED BIG INT, $WORKLOAD UNSIGNED BIG INT);"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $ENTRY_TABLE_NAME")
        onCreate(db)
    }

    fun addEntry(entry : DatabaseEntry) {
        val db = this.readableDatabase

        val values = ContentValues()
        values.put(DAY, formatDateToString(entry.getDate()))
        values.put(WORKTIME, entry.getWorktime())
        values.put(WORKLOAD,entry.getWorkload())

        db.insert(ENTRY_TABLE_NAME, null, values)
        db.close()
    }

    fun editEntry(entry : DatabaseEntry) {
        val db = this.readableDatabase

        val values = ContentValues()
        values.put(DAY, formatDateToString(entry.getDate()))
        values.put(WORKTIME, entry.getWorktime())
        values.put(WORKLOAD, entry.getWorkload())

        db.update(ENTRY_TABLE_NAME, values, "$PRIMARY_KEY=${entry.getID()}", null)
        db.close()
    }

    fun getAllEntries() : ArrayList<DatabaseEntry> {
        val entries = ArrayList<DatabaseEntry>()
        val selectQuery = "SELECT * FROM $ENTRY_TABLE_NAME ORDER BY $PRIMARY_KEY DESC"

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery,null)

        if(cursor.moveToFirst()) {
            do {
                entries.add(DatabaseEntry(cursor.getInt(0), formatStringToDate(cursor.getString(1)), cursor.getLong(2), cursor.getLong(3)))
            } while(cursor.moveToNext())
        }

        cursor.close()

        return entries
    }

    fun getSumPlusMinus() : Long {
        val selectQuery = "SELECT * FROM $ENTRY_TABLE_NAME ORDER BY $PRIMARY_KEY DESC"

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery,null)

        var sum = 0L

        if(cursor.moveToFirst()) {
            do {
                sum += cursor.getLong(2) - cursor.getLong(3)
                if(cursor.getLong(2) >= SIX_HOURS)
                    sum -= LUNCHBREAK_6H
                else if(cursor.getLong(2) >= NINE_HOURS)
                    sum -= LUNCHBREAK_9H
            } while(cursor.moveToNext())
        }

        cursor.close()

        return sum
    }

}