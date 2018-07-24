package com.example.standardbenutzer.stempeluhr.helper

class FunctionalTimer {

    private val timeFractions = mutableListOf<Long>()

    private var isRunning = false

    private var startTime = 0L

    fun startTimer() {
        startTime = System.currentTimeMillis()
        isRunning = true
    }

    fun stopTimer() {
        if(!isRunning)
            throw IllegalArgumentException()
        val fraction = System.currentTimeMillis() - startTime
        timeFractions.add(fraction)
        isRunning = false
    }

    fun getRunningTime() : Long {
        val sum = timeFractions.stream().reduce(0) { x, y -> x + y }
        return if(isRunning)
            sum + (System.currentTimeMillis() - startTime)
        else
            sum
    }

    fun resetTimer() {
        timeFractions.clear()
    }
}