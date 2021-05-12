package com.w20079934.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.w20079934.fragments.DiaryFragment
import com.w20079934.fragments.EntryFragment
import com.w20079934.fragments.RenameDiaryFragment
import com.w20079934.main.DiaryApp
import com.w20079934.mydiary_2.R
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import java.time.LocalDate

class Home : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {
    lateinit var ft: FragmentTransaction
    lateinit var app : DiaryApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)

        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
                this, homeLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        homeLayout.addDrawerListener(toggle)
        toggle.syncState()

        app = application as DiaryApp


        ft = supportFragmentManager.beginTransaction()

        val fragment = DiaryFragment.newInstance()
        ft.replace(R.id.homeFrame, fragment)
        ft.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        openFragment(item.itemId)


        homeLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun openFragment(id : Int)
    {
        when (id) {
            R.id.nav_Diary -> {
                app.finishEditingEntry()
                navigateTo(DiaryFragment.newInstance())
            }
            R.id.nav_newEntry -> {
                if(app.getCurrEntry()==null) {
                    val currDate = LocalDate.now()
                    app.entries.findAll().forEach {
                        //if same date
                        if (it.date.get("day") == currDate.dayOfMonth && it.date.get("month") == currDate.monthValue && it.date.get(
                                        "year"
                                ) == currDate.year
                        ) {
                            app.editEntry(it)
                        }
                    }
                }
                navigateTo(EntryFragment.newInstance())
            }
            R.id.nav_renameDiary -> {
                app.finishEditingEntry()
                navigateTo(RenameDiaryFragment.newInstance())
            }
            else -> Toast.makeText(this, getString(R.string.feature_notImplemented), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_diary, menu)
        return true
    }

    override fun onBackPressed() {
        if (homeLayout.isDrawerOpen(GravityCompat.START))
            homeLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }



}
