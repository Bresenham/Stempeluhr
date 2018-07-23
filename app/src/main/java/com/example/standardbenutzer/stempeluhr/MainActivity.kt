package com.example.standardbenutzer.stempeluhr

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.standardbenutzer.stempeluhr.database.DBHandler
import com.example.standardbenutzer.stempeluhr.database.DatabaseEntry
import kotlinx.android.synthetic.main.view_pager.*

class MainActivity : AppCompatActivity() {

    private val NUM_PAGES = 2

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private var mPager: ViewPager? = null

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private var mPagerAdapter: PagerAdapter? = null

    private lateinit var inputFragment : InputViewFragment

    private lateinit var database : DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        this.database = DBHandler(this)

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = pager
        mPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, PreferenceManager.getDefaultSharedPreferences(this).getString("worktime","07:00"))
        mPager!!.adapter = mPagerAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item!!.itemId == R.id.Settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        if(::inputFragment.isInitialized)
            inputFragment.setWorktime(PreferenceManager.getDefaultSharedPreferences(this).getString("worktime","07:00"))
    }

    override fun onBackPressed() {
        if (mPager!!.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager!!.currentItem = mPager!!.currentItem - 1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_menu,menu)
        return true
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private lateinit var worktime : String

        constructor(fm: FragmentManager, worktime: String) : this(fm) {
            this.worktime = worktime
        }

        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                inputFragment = InputViewFragment(worktime, database)
                return inputFragment
            } else {
                return OverviewFragment(database)
            }
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }
}
