package com.example.standardbenutzer.stempeluhr.helper

import java.text.SimpleDateFormat
import java.util.*

class Utility {
    companion object {

        fun formatDateToString(date : Calendar) : String {
            return SimpleDateFormat("dd.MM.yyyy").format(date.time)
        }

        fun formatStringToDate(date : String) : Calendar {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, date.split(".")[2].toInt())
            cal.set(Calendar.MONTH, date.split(".")[1].toInt())
            cal.set(Calendar.DAY_OF_MONTH, date.split(".")[0].toInt())
            return cal
        }

        fun getHoursFromMs(ms : Long) : Int {
            return (ms * (2.7777777777778E-7)).toInt()
        }

        fun getMinutesFromMs(ms : Long) : Int {
            return ((ms * (1.6666666666667E-5)) % 60).toInt()
        }

        fun msToString(ms : Long) : String {
            val totalSecs = ms / 1000
            val hours = totalSecs / 3600
            val mins = totalSecs / 60 % 60
            val secs = totalSecs % 60
            val minsString = if (mins == 0L)
                "00"
            else
                if (mins < 10)
                    "0$mins"
                else
                    "" + mins
            val secsString = if ((secs == 0L))
                "00"
            else
                (if ((secs < 10))
                    "0$secs"
                else
                    "" + secs)
            return (hours).toString() + "h:" + minsString + "m:" + secsString + "s"
        }

        fun stringToMs(time : String) : Long {
            return 60 * 1000L * (time.split(":")[0].toInt() * 60 + time.split(":")[1].toInt())
        }

        fun reduceTimeByLunchbreak(time : Long) : Long {
            val reducedTime : Long
            if(time in SIX_HOURS..(NINE_HOURS - 1))
                reducedTime = time - LUNCHBREAK_6H
            else if(time >= NINE_HOURS)
                reducedTime = time - LUNCHBREAK_9H
            else
                reducedTime = time
            return reducedTime
        }

        const val SIX_HOURS = 6 * 60 * 60 * 1000L
        const val NINE_HOURS = 9 * 60 * 60 * 1000L

        const val LUNCHBREAK_6H = 30 * 60 * 1000L
        const val LUNCHBREAK_9H = 45 * 60 * 1000L
    }
}