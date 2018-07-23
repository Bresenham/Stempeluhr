package com.example.standardbenutzer.stempeluhr

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.overview.*
import ListViewAdapter
import android.annotation.SuppressLint
import com.example.standardbenutzer.stempeluhr.database.DBHandler
import com.example.standardbenutzer.stempeluhr.database.DatabaseEntry
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString

class OverviewFragment : Fragment {

    private lateinit var database : DBHandler

    constructor() : super()

    @SuppressLint("ValidFragment")
    constructor(database : DBHandler) : this() {
        this.database = database
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.overview, container, false) as ViewGroup
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        listView.adapter = ListViewAdapter(this.requireActivity().applicationContext, convertToListViewEntry(database.getAllEntries()))

        swiperefresh.setOnRefreshListener {
            listView.adapter = ListViewAdapter(this.requireActivity().applicationContext, convertToListViewEntry(database.getAllEntries()))
            swiperefresh.isRefreshing = false
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }


    private fun convertToListViewEntry(databaseEntries : ArrayList<DatabaseEntry>) : ArrayList<ListViewEntry> {
        val listEntries = ArrayList<ListViewEntry>()

        databaseEntries.forEach {
            val timeString = msToString(it.getWorktime())
            var plusMinus : String
            var difference = it.getWorktime() - it.getWorkload()
            if(difference < 0) {
                difference *= -1
                plusMinus = "-"
            } else
                plusMinus = "+"
            plusMinus += msToString(difference)
            listEntries.add(ListViewEntry(it.getDate(),timeString,plusMinus))
        }

        return listEntries
    }

}