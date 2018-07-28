package com.example.standardbenutzer.stempeluhr.database

import java.io.Serializable
import java.util.*

class DatabaseEntry(private val id : Int, private var date: Calendar, private var worktime: Long, private val workload : Long) : Serializable{

    private lateinit var plusMinus : String

    constructor( id : Int, date: Calendar, worktime: Long, workload : Long, plusMinus : String) : this(id,date,worktime, workload) {
        this.plusMinus = plusMinus
    }

    fun getID() : Int {
        return id
    }

    fun getDate() : Calendar {
        return date
    }

    fun getWorktime() : Long {
        return worktime
    }

    fun getWorkload() : Long {
        return workload
    }

    fun getPlusMinus() : String {
        return plusMinus
    }

    fun setDate(date : Calendar) {
        this.date = date
    }

    fun setWorktime(worktime : Long) {
        this.worktime = worktime
    }
}