package com.example.standardbenutzer.stempeluhr

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.overview.*
import ListViewAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import com.example.standardbenutzer.stempeluhr.database.DBHandler
import com.example.standardbenutzer.stempeluhr.database.DatabaseEntry
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.reduceTimeByLunchbreak
import android.content.Intent
import com.example.standardbenutzer.stempeluhr.edit.EditListViewActivity


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
        if(!::database.isInitialized)
            return
        listView.adapter = ListViewAdapter(this.requireActivity().applicationContext, updateListView(database.getAllEntries(), database.getSumPlusMinus()))
        swiperefresh.setOnRefreshListener {
            listView.adapter = ListViewAdapter(this.requireActivity().applicationContext, updateListView(database.getAllEntries(), database.getSumPlusMinus()))
            swiperefresh.isRefreshing = false
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val clickedItem = parent.getItemAtPosition(position) as DatabaseEntry
            val intent = Intent(activity, EditListViewActivity::class.java)
            intent.putExtra("entry", clickedItem)
            startActivity(intent)
        }

        super.onActivityCreated(savedInstanceState)
    }

    private fun updateListView(databaseEntries : ArrayList<DatabaseEntry>, sPlusMinus : Long) : ArrayList<DatabaseEntry> {
        val listEntries = ArrayList<DatabaseEntry>()

        databaseEntries.forEach {
            var plusMinus : String
            var difference = reduceTimeByLunchbreak(it.getWorktime()) - it.getWorkload()
            if(difference < 0) {
                difference *= -1
                plusMinus = "-"
            } else
                plusMinus = "+"
            plusMinus += msToString(difference)
            listEntries.add(DatabaseEntry(it.getID(), it.getDate(), it.getWorktime(), it.getWorkload(), plusMinus))
        }

        var sumPlusMinus = sPlusMinus
        var sumPlusMinusStr : String
        if(sumPlusMinus < 0) {
            sumPlusMinusStr = "-"
            sumPlusMinus *= -1
            txtSumPlusMinus.setBackgroundColor(Color.RED)
        }
        else {
            sumPlusMinusStr = "+"
            txtSumPlusMinus.setBackgroundColor(Color.GREEN)
        }
        sumPlusMinusStr += msToString(sumPlusMinus)

        txtSumPlusMinus.text = sumPlusMinusStr

        return listEntries
    }

}