package com.example.standardbenutzer.stempeluhr

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
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
import com.example.standardbenutzer.stempeluhr.helper.FunctionalTimer
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.formatDateToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.reduceTimeByLunchbreak
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.stringToMs
import com.ramotion.circlemenu.CircleMenuView
import kotlinx.android.synthetic.main.input_view.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RemoteViews
import kotlinx.android.synthetic.main.notification_layout.*
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationBuilderWithBuilderAccessor


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

    private lateinit var mRemoteViews : RemoteViews

    private lateinit var mNotificationManager: NotificationManagerCompat

    private lateinit var mBuilder : NotificationCompat.Builder

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
                        val cal = Calendar.getInstance()
                        cal.time = Date()
                        database.addEntry(DatabaseEntry(0, cal, timer.getRunningTime(), MAX_TIME))
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
                createRemoteView()
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
        var totalWorktime = timer.getRunningTime()

        val percentage = getTimePercentage(reduceTimeByLunchbreak(totalWorktime))
        progressBar.setProgress(percentage)
        textView.text = msToString(totalWorktime)
        totalWorktime = reduceTimeByLunchbreak(totalWorktime)
        if(percentage < 100) {
            textViewDelay.setBackgroundColor(Color.RED)
            textViewDelay.text = "-" + msToString(MAX_TIME - totalWorktime)
        } else {
            textViewDelay.setBackgroundColor(Color.GREEN)
            textViewDelay.text = "+" + msToString(totalWorktime - MAX_TIME)
        }

        mRemoteViews.setTextViewText(R.id.txtCurrentWorktime, msToString(totalWorktime))
        mNotificationManager.notify(0, mBuilder.build())
    }

    private fun createRemoteView() {
        mNotificationManager = NotificationManagerCompat.from(activity!!.applicationContext)

        val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = PendingIntent.getActivity(activity!!.applicationContext, 0,
                intent, 0)

        mRemoteViews = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_layout)
        mRemoteViews.setImageViewResource(R.id.imageView, R.mipmap.time_left_blue)
        mRemoteViews.setTextViewText(R.id.txtCurrentWorktime, "This text is created programmatically.")

        mBuilder = NotificationCompat.Builder(activity!!.applicationContext,"1")
        mBuilder.setSmallIcon(R.mipmap.time_left).setAutoCancel(false).setOngoing(true).setContentIntent(pendingIntent).setContent(mRemoteViews)

        mNotificationManager.notify(0, mBuilder.build())
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