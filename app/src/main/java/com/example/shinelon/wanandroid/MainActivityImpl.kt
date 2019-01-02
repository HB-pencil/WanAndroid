package com.example.shinelon.wanandroid

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.shinelon.wanandroid.fragment.CommonDialogFragment
import com.example.shinelon.wanandroid.fragment.CommonDialogListener
import com.example.shinelon.wanandroid.helper.NavigationViewhelper
import com.example.shinelon.wanandroid.presenter.MainActivityPresenter
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.header_layout_main.*
import kotlinx.android.synthetic.main.header_layout_main.view.*

class MainActivityImpl: AppCompatActivity(),IMainActivityView,NavigationView.OnNavigationItemSelectedListener,
CommonDialogListener{
    val TAG = "MainActivityImpl"
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
        val permissions = arrayOf("android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE")
        presenter?.checkPermissions(permissions)

        navigation_view.setNavigationItemSelectedListener(this)

        navigation_bottom.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        NavigationViewhelper.disableShiftMode(navigation_bottom)

        val stateTv = navigation_view.getHeaderView(0).state_tv
        stateTv.setOnClickListener {
            if(!presenter!!.userState) {
                presenter?.login()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun setPresenter() {
        presenter = MainActivityPresenter()
        presenter?.addView(this)
    }

    override fun getActivityContext() = this

    override fun updateHeaderView(isOnline: Boolean,name: String?) {
        val header = navigation_view.getHeaderView(0)
        if (isOnline) {
            header.name_tv.text = name
            header.state_tv.text = resources.getString(R.string.user_login)
        }else {
            header.name_tv.text = resources.getString(R.string.unknown_user)
            header.state_tv.text = resources.getString(R.string.user_logout)
        }
    }

    /**
     * 登录 Main -> Login 点击 -> Main 回调此方法，singleTask模式 传入intent
     * 登录 Main -> Login 返回 -> Main 回调此方法，intent == null
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val name = intent?.getStringExtra("name")
        val isOnline = intent?.getBooleanExtra("onLineState",false)
        Log.d(TAG,"name:$name;isOnline:$isOnline")
        if (intent != null) updateHeaderView(isOnline!!,name)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPositiveClick() {
        val i = Intent()
        i.action= Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package",this.packageName,null)
        i.data = uri
        startActivity(i)
    }

    override fun onNegativeClick() {
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==0&& Build.VERSION.SDK_INT>=23){
            grantResults.forEach {
                if(it== PackageManager.PERMISSION_DENIED){
                    if(!shouldShowRequestPermissionRationale(permissions[grantResults.indexOf(it)])){
                        showWarnDialog()
                    }else{
                        finish()
                    }
                }
            }
        }
    }

    fun showWarnDialog(){
        val dialog = CommonDialogFragment.newInstance("警告","为了让软件正常工作，请您允许通过申请的权限，否则将无法提供服务！",
                this)
        dialog.show(fragmentManager,"tag")
    }
}

