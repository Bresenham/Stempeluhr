package com.example.standardbenutzer.stempeluhr

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import at.markushi.ui.CircleButton
import com.example.standardbenutzer.stempeluhr.R.mipmap.button_start
import com.example.standardbenutzer.stempeluhr.R.mipmap.button_stop
import com.example.standardbenutzer.stempeluhr.database.DBHandler
import com.example.standardbenutzer.stempeluhr.database.DatabaseEntry
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.stringToMs
import com.ramotion.circlemenu.CircleMenuView
import kotlinx.android.synthetic.main.input_view.*
import java.text.SimpleDateFormat
import java.util.*


class InputViewFragment() : Fragment() {

    private var MAX_TIME = 0L

    private lateinit var database : DBHandler

    @SuppressLint("ValidFragment")
    constructor(wrktime: Long, database : DBHandler) : this() {
        this.MAX_TIME = wrktime
        this.database = database
    }

    private var currentState = State.RUNNING

    private var handler = Handler()

    private var runnable = Runnable {  }

    private lateinit var progressBar : CircleProgressBar

    private lateinit var textView  : TextView

    private val timer = FunctionalTimer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.input_view, container, false) as ViewGroup

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val circleButton = view!!.findViewById(R.id.circleButton) as CircleButton
        val menu  = view!!.findViewById(R.id.circleMenu) as CircleMenuView

        progressBar = view!!.findViewById(R.id.progressBar) as CircleProgressBar
        textView = view!!.findViewById(R.id.textView) as TextView

        menu.eventListener = object : CircleMenuView.EventListener() {
            override fun onMenuOpenAnimationStart(view: CircleMenuView) {
            }

            override fun onMenuOpenAnimationEnd(view: CircleMenuView) {
            }

            override fun onMenuCloseAnimationStart(view: CircleMenuView) {
            }

            override fun onMenuCloseAnimationEnd(view: CircleMenuView) {

            }

            override fun onButtonClickAnimationStart(view: CircleMenuView, index: Int) {
                when (index) {
                    0 -> {
                        database.addEntry(DatabaseEntry(SimpleDateFormat("dd.MM.yy").format(Date()), timer.getRunningTime(), MAX_TIME))
                        Toast.makeText(activity!!.applicationContext, "Time saved.", Toast.LENGTH_SHORT).show()
                        endCounting()
                        currentState = State.RUNNING
                        circleButton.visibility = View.VISIBLE
                        circleButton.setImageResource(button_start)
                    }
                    1 -> {
                        Toast.makeText(activity!!.applicationContext, "Time not saved.", Toast.LENGTH_SHORT).show()
                        endCounting()
                        currentState = State.RUNNING
                        circleButton.visibility = View.VISIBLE
                        circleButton.setImageResource(button_start)
                    }
                    else -> {
                        timer.startTimer()
                        currentState = State.STOPPED
                        circleButton.visibility = View.VISIBLE
                        circleButton.setImageResource(button_stop)
                    }
                }
            }

            override fun onButtonClickAnimationEnd(view: CircleMenuView, index: Int) {
                menu.visibility = View.INVISIBLE
            }
        }

        circleButton.setOnClickListener {
            if(currentState == State.RUNNING) {
                startCounting()
                currentState = State.STOPPED
                circleButton.setImageResource(button_stop)
            } else if (currentState == State.STOPPED){
                timer.stopTimer()
                currentState = State.CHECKED
                circleButton.visibility = View.INVISIBLE
                menu.visibility = View.VISIBLE
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    fun setWorktime(worktime : String) {
        this.MAX_TIME = stringToMs(worktime)
    }

    private fun startCounting() {
        progressBar.setProgress(0f)

        timer.startTimer()

        handler = Handler()
        runnable = Runnable {
            updateProgressBar()
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }

    private fun updateProgressBar() {
        val difference = timer.getRunningTime()
        val percentage = getTimePercentage(difference)
        progressBar.setProgress(percentage)
        textView.text = msToString(difference)
        if(percentage < 100) {
            textViewDelay.setBackgroundColor(Color.RED)
            textViewDelay.text = "-" + msToString(MAX_TIME - difference)
        } else {
            textViewDelay.setBackgroundColor(Color.GREEN)
            textViewDelay.text = "+" + msToString(difference - MAX_TIME)
        }
    }

    private fun getTimePercentage(time : Long) : Float =
            time.div(MAX_TIME.toDouble()).toFloat() * 100.0f


    private fun endCounting() {
        handler = Handler()
        runnable = Runnable {  }
        textView.text = 0.toString()
        timer.resetTimer()
    }

    enum class State {
        RUNNING,
        CHECKED,
        STOPPED
    }
}