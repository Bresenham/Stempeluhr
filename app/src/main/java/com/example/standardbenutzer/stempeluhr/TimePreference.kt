package com.example.standardbenutzer.stempeluhr

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker

class TimePreference(private val mContext: Context, attrs: AttributeSet) : DialogPreference(mContext, attrs) {
    private var lastHour = 0
    private var lastMinute = 0
    private var picker: TimePicker? = null

    init {
        positiveButtonText = "Set"
        negativeButtonText = "Cancel"

        lastHour = getHour(DEFAULT_TIME)
        lastMinute = getMinute(DEFAULT_TIME)

        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)

        onPreferenceChangeListener = OnPreferenceChangeListener { pref, value ->
            pref.summary = getFormattedSummary(value as String)
            true
        }
    }

    override fun onCreateDialogView(): View {
        picker = TimePicker(context)
        picker!!.setIs24HourView(true)
        return picker!!
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)

        picker!!.hour = lastHour
        picker!!.minute = lastMinute
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            lastHour = picker!!.hour
            lastMinute = picker!!.minute

            //            String time = String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
            val time = String.format("%02d:%02d", lastHour, lastMinute)

            if (callChangeListener(time)) {
                persistString(time)
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val time: String?

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_TIME)
            } else {
                time = getPersistedString(defaultValue.toString())
            }
        } else {
            time = defaultValue!!.toString()
        }

        lastHour = getHour(time!!)
        lastMinute = getMinute(time)
        summary = getFormattedSummary(time)
    }


    fun getFormattedSummary(time: String?): String {

        val h = getHour(time!!)
        val m = getMinute(time)

        return "$h Hour(s) $m Minute(s)"

    }

    companion object {

        val DEFAULT_TIME = "07:00"


        fun getHour(time: String): Int {
            val pieces = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            return Integer.parseInt(pieces[0])
        }

        fun getMinute(time: String): Int {
            val pieces = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            return Integer.parseInt(pieces[1])
        }
    }
}