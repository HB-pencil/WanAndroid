package com.example.shinelon.wanandroid

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.example.shinelon.wanandroid.fragment.*
import com.example.shinelon.wanandroid.helper.*
import com.example.shinelon.wanandroid.presenter.MainActivityPresenter
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.header_layout_main.view.*

class MainActivityImpl : AppCompatActivity(), IMainActivityView, NavigationView.OnNavigationItemSelectedListener,
        CommonDialogListener {
    private val TAG = "MainActivityImpl"
    private var presenter: MainActivityPresenter? = null
    private var mWindow: HotSearchPopupWin? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.btn_home -> {
                showCurrentFragment(FragmentTag.HOME.tag)
            }
            R.id.btn_struct -> {
                showCurrentFragment(FragmentTag.STRUCT.tag)
            }
            R.id.btn_project -> {
                showCurrentFragment(FragmentTag.PROJECT.tag)
            }
            R.id.btn_nav -> {
                showCurrentFragment(FragmentTag.NAVIGATE.tag)
            }
        }
        true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.panel_collect_art -> {
                if (!UserInfo.INSTANCE.isOnline){
                    val intent = Intent(this,LoginActivityImpl::class.java)
                    presenter?.jumpToTarget(ActionFlag.LOGIN,intent)
                }else {
                    val intent = Intent(this,CollectedActivityImpl::class.java)
                    presenter?.jumpToTarget(ActionFlag.COLLECT,intent)
                }
            }
            R.id.panel_love_web -> {
                if (!UserInfo.INSTANCE.isOnline){
                    val intent = Intent(this,LoginActivityImpl::class.java)
                    presenter?.jumpToTarget(ActionFlag.LOGIN,intent)
                }else {

                }
            }
            R.id.panel_night_mode -> {
                //TODO
                if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                recreate()
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
        supportActionBar?.title = FragmentTag.HOME.tag

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar_base, R.string.open_navigation, R.string.close_navigation)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        if (presenter == null) setPresenter()
        val permissions = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_NETWORK_STATE")

        presenter?.checkPermissions(permissions)
        presenter?.checkNetworkState()

        setOnlineState(intent.getBooleanExtra("isOnline", false))
        val stateTv = navigation_view.getHeaderView(0).state_tv
        stateTv.setOnClickListener {
            if (!getOnlineState()) {
                presenter?.login()
            } else {
                presenter?.logout()
            }
            Log.d(TAG,"操作${stateTv.text}")
        }
        presenter?.checkAutoLogin()
        navigation_view.setNavigationItemSelectedListener(this)

        navigation_bottom.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        NavigationViewhelper.disableShiftMode(navigation_bottom)

        initFragments() //初始化
    }

    override fun onResume() {
        super.onResume()
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
        }
    }

    fun showCurrentFragment(fragmentTag: String){
        supportActionBar?.title = fragmentTag
        val ft = supportFragmentManager.beginTransaction()

        hideAllFragments(ft)//开启事务
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null){
            fragment = getTargetFragment(fragmentTag)
            ft?.add(R.id.fragment_container,fragment,fragmentTag)
            ft?.show(fragment)
        }else {
            ft?.show(fragment)
            Log.i(TAG,"$fragmentTag fragment已经存在")
        }
        ft?.commit()
    }

    fun initFragments(){
        val map = mutableMapOf<String,Fragment>()
        val fm = supportFragmentManager
        map[FragmentTag.HOME.tag] = fm.findFragmentByTag(FragmentTag.HOME.tag)?:getTargetFragment(FragmentTag.HOME.tag)
        map[FragmentTag.STRUCT.tag] = fm.findFragmentByTag(FragmentTag.STRUCT.tag)?:getTargetFragment(FragmentTag.STRUCT.tag)
        map[FragmentTag.PROJECT.tag] = fm.findFragmentByTag(FragmentTag.PROJECT.tag)?:getTargetFragment(FragmentTag.PROJECT.tag)
        map[FragmentTag.NAVIGATE.tag] = fm.findFragmentByTag(FragmentTag.NAVIGATE.tag)?:getTargetFragment(FragmentTag.NAVIGATE.tag)
        val ft = fm.beginTransaction()
        map.forEach {
            ft.add(R.id.fragment_container,it.value,it.key)
            if (it.value.tag == FragmentTag.HOME.tag) {
                ft.show(it.value)
                supportActionBar?.title = FragmentTag.HOME.tag
            } else {
                ft.hide(it.value)
            }
        }
        ft.commit()
    }

    /**
     * 新建Fragment
     */
    fun getTargetFragment(fragmentTag: String): Fragment{
        when(fragmentTag){
            FragmentTag.STRUCT.tag -> return IStructFragmentImpl.getInstance(null)
            FragmentTag.PROJECT.tag -> return IProjectFragmentImpl.getInstance(null)
            FragmentTag.NAVIGATE.tag -> return INavigateFragmentImpl.getInstance(null)
            else -> return IHomeFragmentImpl.getInstance(null)
        }
    }

    fun hideAllFragments(ft: FragmentTransaction){
        val home = supportFragmentManager.findFragmentByTag(FragmentTag.HOME.tag)
        if (home != null) ft.hide(home)

        val struct = supportFragmentManager.findFragmentByTag(FragmentTag.STRUCT.tag)
        if (struct != null) ft.hide(struct)

        val project = supportFragmentManager.findFragmentByTag(FragmentTag.PROJECT.tag)
        if (project != null) ft.hide(project)

        val navigate = supportFragmentManager.findFragmentByTag(FragmentTag.NAVIGATE.tag)
        if (navigate != null) ft.hide(navigate)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        val menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "搜索"
        searchView.setOnSearchClickListener {
            presenter?.getHotWords()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(this@MainActivityImpl, SearchArticleActivityImpl::class.java)
                intent.putExtra("search_key", query)
                presenter?.jumpToTarget(ActionFlag.SEARCH, intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        searchView.setOnCloseListener {
            hideHotWords()
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun setPresenter() {
        presenter = MainActivityPresenter()
        presenter?.addView(this)
    }

    override fun getOnlineState() = UserInfo.INSTANCE.isOnline

    override fun setOnlineState(isOnline: Boolean) {
        UserInfo.INSTANCE.isOnline = isOnline
    }

    override fun getActivityContext() = this

    override fun updateHeaderView(isOnline: Boolean, name: String?) {
        val header = navigation_view.getHeaderView(0)
        if (isOnline) {
            header.name_tv.text = name
            header.state_tv.text = resources.getString(R.string.user_logout)
        } else {
            header.name_tv.text = resources.getString(R.string.unknown_user)
            header.state_tv.text = resources.getString(R.string.user_login)
        }
    }

    /**
     * 登录 Main -> Login 点击 -> Main 回调此方法，singleTask模式 传入intent
     * 登录 Main -> Login 返回 -> Main 回调此方法，intent == null
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val name = intent?.getStringExtra("name")
        val isOnline = intent?.getBooleanExtra("isOnline", false)
        val reCreate = intent?.getBooleanExtra("recreate",false)
        Log.d(TAG, "name:$name;isOnline:$isOnline")
        if (intent != null) updateHeaderView(isOnline!!, name)
        if (reCreate != null && reCreate){
            val ft = supportFragmentManager.beginTransaction()
            //寻找原先存在的并移除，重新创建
            var home = supportFragmentManager.findFragmentByTag(FragmentTag.HOME.tag)
            if(home != null) {
                ft.remove(home)
                home = getTargetFragment(FragmentTag.HOME.tag)
            }
            ft.add(home,FragmentTag.HOME.tag)
            ft.show(home)
            ft.commit()
        }
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
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", this.packageName, null)
        i.data = uri
        startActivity(i)
    }

    override fun onNegativeClick() {
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && Build.VERSION.SDK_INT >= 23) {
            grantResults.forEach {
                if (it == PackageManager.PERMISSION_DENIED) {
                    if (!shouldShowRequestPermissionRationale(permissions[grantResults.indexOf(it)])) {
                        showWarnDialog(this@MainActivityImpl, "为了软件正常运行，请允许申请的权限！", "警告")
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    override fun showWarnDialog(listener: CommonDialogListener, message: String, title: String) {
        val dialog = CommonDialogFragment.newInstance(title, message, listener)
        dialog.show(fragmentManager, "tag")
    }

    override fun showHotWords(list: MutableList<String>) {
        val hotWindow = HotSearchPopupWin(this)
        if (mWindow == null) mWindow = hotWindow
        hotWindow.width = WindowManager.LayoutParams.MATCH_PARENT
        hotWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        hotWindow.isErrorViewShow(list.isEmpty())
        for (i in 0..(list.size - 1) step 2) {
            hotWindow.addHotWord(list[i], if (i + 1 == list.size) "" else list[i + 1])
        }
        hotWindow.showWindow(toolbar_base)
        val contentView = window.decorView.findViewById<FrameLayout>(android.R.id.content)
        contentView.setBackgroundColor(resources.getColor(R.color.mask))
        hotWindow.setOnDismissListener {
            contentView.setBackgroundColor(Color.WHITE)
        }

        hotWindow.addClickListener(object : HotSearchPopupWin.HotSearchPopupWinListener {
            override fun onClick(hotWord: String) {
                val intent = Intent(this@MainActivityImpl,SearchArticleActivityImpl::class.java)
                intent.putExtra("search_key",hotWord)
                presenter?.jumpToTarget(ActionFlag.SEARCH,intent)
            }
        })
    }

    override fun hideHotWords() {
        mWindow?.dismiss()
        mWindow = null
    }
}


