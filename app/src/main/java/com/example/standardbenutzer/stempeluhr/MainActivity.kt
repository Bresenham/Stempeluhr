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
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.stringToMs
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        val worktime = stringToMs(PreferenceManager.getDefaultSharedPreferences(this).getString("worktime","07:00"))

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = pager
        mPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, worktime, DBHandler(this))
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
        if(::inputFragment.isInitialized) {
            inputFragment.setWorktime(PreferenceManager.getDefaultSharedPreferences(this).getString("worktime", "07:00"))
        }
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

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, private val worktime: Long, private val database: DBHandler) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                inputFragment = InputViewFragment(worktime,database)
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
