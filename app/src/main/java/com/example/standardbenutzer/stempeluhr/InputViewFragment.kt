package com.example.standardbenutzer.stempeluhr

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import at.markushi.ui.CircleButton
import com.example.standardbenutzer.stempeluhr.R.mipmap.button_start
import com.example.standardbenutzer.stempeluhr.R.mipmap.button_stop
import com.ramotion.circlemenu.CircleMenuView
import kotlinx.android.synthetic.main.input_view.*

class InputViewFragment : Fragment() {

    private var currentState = State.RUNNING

    private var startTime = 0L

    private var handler = Handler()

    private var runnable = Runnable {  }

    private val MAX_TIME = 7 * 60 * 60 * 1000

    private lateinit var progressBar : CircleProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.input_view, container, false) as ViewGroup
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val circleButton = view!!.findViewById(R.id.circleButton) as CircleButton
        val buttonCross = view!!.findViewById(R.id.buttonCross) as Button
        val buttonCheck = view!!.findViewById(R.id.buttonCheck) as Button
        val menu  = view!!.findViewById(R.id.circleMenu) as CircleMenuView
        progressBar = view!!.findViewById(R.id.progressBar) as CircleProgressBar

        menu.eventListener = object : CircleMenuView.EventListener() {
            override fun onMenuOpenAnimationStart(view: CircleMenuView) {
            }

            override fun onMenuOpenAnimationEnd(view: CircleMenuView) {
            }

            override fun onMenuCloseAnimationStart(view: CircleMenuView) {
            }

            override fun onMenuCloseAnimationEnd(view: CircleMenuView) {
                menu.visibility = View.INVISIBLE
            }

            override fun onButtonClickAnimationStart(view: CircleMenuView, index: Int) {
                if(index == 0)
                    Toast.makeText(activity!!.applicationContext,"Time saved.", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity!!.applicationContext,"Time not saved.", Toast.LENGTH_SHORT).show()
            }

            override fun onButtonClickAnimationEnd(view: CircleMenuView, index: Int) {
                menu.visibility = View.INVISIBLE
                currentState = State.RUNNING
                circleButton.visibility = View.VISIBLE
                circleButton.setImageResource(button_start)
            }
        }

        circleButton.setOnClickListener {
            if(currentState == State.RUNNING) {
                startCounting()
                currentState = State.STOPPED
                circleButton.setImageResource(button_stop)
            } else if (currentState == State.STOPPED){
                endCounting()
                currentState = State.CHECKED
                circleButton.visibility = View.INVISIBLE
                menu.visibility = View.VISIBLE
            }
        }

        buttonCross.setOnClickListener {
            if(currentState == State.CHECKED) {
                setButtonsFinished()

            }
        }

        buttonCheck.setOnClickListener{
            if(currentState == State.CHECKED) {
                setButtonsFinished()
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    private fun startCounting() {
        progressBar.setProgress(0f)
        startTime = System.currentTimeMillis()

        handler = Handler()
        runnable = Runnable {
            updateProgressBar()
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }

    private fun updateProgressBar() {
        val difference = System.currentTimeMillis() - startTime
        val percentage = Math.min(100.0, difference.div(MAX_TIME.toDouble()) * 100).toFloat()
        progressBar.setProgress(percentage)
    }

    private fun endCounting() {
        handler = Handler()
        runnable = Runnable {  }
    }

    private fun setButtonsFinished() {
        currentState = State.RUNNING
        circleButton.visibility = View.VISIBLE
        buttonCheck.visibility = View.INVISIBLE
        buttonCross.visibility = View.INVISIBLE
        circleButton.setImageResource(button_start)
    }

    enum class State {
        RUNNING,
        CHECKED,
        STOPPED
    }
}