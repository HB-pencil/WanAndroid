package com.example.shinelon.wanandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.shinelon.wanandroid.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_toolbar.*

/**
 * 2019/03/20
 */
class SettingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar_base)
        val fm = fragmentManager
        var fragment = fm.findFragmentById(R.id.activity_container)
        if(fragment==null) fragment = SettingFragment.newInstance()
        fm.beginTransaction()
                .add(R.id.activity_container,fragment)
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}