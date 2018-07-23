package com.example.standardbenutzer.stempeluhr.helper

class Utility {
    companion object {
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
    }
}