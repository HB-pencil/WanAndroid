package com.example.shinelon.wanandroid

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.example.shinelon.wanandroid.helper.NavigationViewhelper
import com.example.shinelon.wanandroid.presenter.MainActivityPresenter
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class MainActivityImpl: AppCompatActivity(),IMainActivityView,NavigationView.OnNavigationItemSelectedListener {
    var presenter: MainActivityPresenter? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.btn_home -> {

            }
            R.id.btn_struct -> {

            }
            R.id.btn_project -> {

            }
            R.id.btn_nav -> {

            }
        }
        true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.panel_collect_art -> {

            }
            R.id.panel_love_web -> {

            }
            R.id.panel_night_mode -> {

            }
            R.id.panel_setting -> {

            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar_base)

        val toggle =ActionBarDrawerToggle(this,drawer_layout,toolbar_base,R.string.open_navigation,R.string.close_navigation)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        if (presenter == null ) setPresenter()

        navigation_view.setNavigationItemSelectedListener(this)

        navigation_bottom.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        NavigationViewhelper.disableShiftMode(navigation_bottom)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun setPresenter() {
        presenter = MainActivityPresenter()
        presenter?.addView(this)
    }
}

