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