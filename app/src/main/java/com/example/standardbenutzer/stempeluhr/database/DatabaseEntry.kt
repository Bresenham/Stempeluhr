package com.example.standardbenutzer.stempeluhr.database

import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.getPlusMinusWorktime
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString
import java.io.Serializable
import java.util.*

class DatabaseEntry : Serializable{

    private var id = 0
    private var date = Calendar.getInstance()
    private var worktime = 0L
    private var workload = 0L
    private var plusMinus = ""

    constructor(id : Int, date: Calendar, worktime: Long, workload : Long) {
        this.id = id
        this.date = date
        this.worktime = worktime
        this.workload = workload
        this.plusMinus = getPlusMinusWorktime(worktime, workload)
    }

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