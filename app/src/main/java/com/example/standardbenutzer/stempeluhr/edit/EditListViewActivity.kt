package com.example.standardbenutzer.stempeluhr.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.standardbenutzer.stempeluhr.R
import com.example.standardbenutzer.stempeluhr.database.DBHandler
import com.example.standardbenutzer.stempeluhr.database.DatabaseEntry
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.formatDateToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.getHoursFromMs
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.getMinutesFromMs
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString
import kotlinx.android.synthetic.main.edit_listview_entry.*
import java.util.*

class EditListViewActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_listview_entry)

        val entry = intent.getSerializableExtra("entry") as DatabaseEntry

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
            view, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                entry.setDate(calendar)
                txtEntryDate.setText(formatDateToString(entry.getDate()))
            }, entry.getDate().get(Calendar.YEAR), entry.getDate().get(Calendar.MONTH), entry.getDate().get(Calendar.DAY_OF_MONTH))

        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener {
            view, hourOfDay, minute ->
            entry.setWorktime(60 * 1000L * (hourOfDay * 60 + minute))
            txtEntryWorktime.setText(msToString(entry.getWorktime()))
        }, getHoursFromMs(entry.getWorktime()), getMinutesFromMs(entry.getWorktime()),true)

        txtEntryDate.setText(formatDateToString(entry.getDate()))

        txtEntryDate.setOnClickListener {
            datePicker.show()
        }

        txtEntryWorktime.setText(msToString(entry.getWorktime()))

        txtEntryWorktime.setOnClickListener {
            timePicker.show()
        }

        btnSave.setOnClickListener {
            DBHandler(this).editEntry(entry)
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}