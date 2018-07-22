package com.example.standardbenutzer.stempeluhr

import android.annotation.SuppressLint
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
import com.ramotion.circlemenu.CircleMenuView
import java.util.concurrent.TimeUnit


class InputViewFragment() : Fragment() {

    private lateinit var worktime : String

    @SuppressLint("ValidFragment")
    constructor(wrktime: String) : this() {
        this.worktime = wrktime
    }

    private var currentState = State.RUNNING

    private var handler = Handler()

    private var runnable = Runnable {  }

    private var MAX_TIME = 0L

    private lateinit var progressBar : CircleProgressBar

    private lateinit var textView  : TextView

    private val timer = FunctionalTimer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.input_view, container, false) as ViewGroup
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MAX_TIME = 60 * 1000L * (worktime.split(":")[0].toInt() * 60 + worktime.split(":")[1].toInt())

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
        this.MAX_TIME = 60 * 1000L * (worktime.split(":")[0].toInt() * 60 + worktime.split(":")[1].toInt())
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
        val percentage = Math.min(100.0, difference.div(MAX_TIME.toDouble()) * 100).toFloat()
        progressBar.setProgress(percentage)
        textView.text = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(difference),
                TimeUnit.MILLISECONDS.toSeconds(difference) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference))
        )
    }

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