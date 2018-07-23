package com.example.standardbenutzer.stempeluhr.database

class DatabaseEntry(private val date: String, private val worktime: Long, private val workload : Long) {

    fun getDate() : String {
        return date
    }

    fun getWorktime() : Long {
        return worktime
    }

    fun getWorkload() : Long {
        return workload
    }
}