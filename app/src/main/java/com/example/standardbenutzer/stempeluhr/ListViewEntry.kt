package com.example.standardbenutzer.stempeluhr

class ListViewEntry {

    private var date : String
    private var worktime : String
    private var plusMinus: String

    constructor(date : String, worktime : String, plusMinus : String) {
        this.date = date
        this.worktime = worktime
        this.plusMinus = plusMinus
    }

    fun getDate() : String {
        return date
    }

    fun getWorktime() : String {
        return worktime
    }

    fun getPlusMinus() : String {
        return plusMinus
    }

}