package com.example.shinelon.wanandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.example.shinelon.wanandroid.fragment.*
import com.example.shinelon.wanandroid.helper.*
import com.example.shinelon.wanandroid.presenter.MainActivityPresenter
import com.example.shinelon.wanandroid.utils.toast
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.header_layout_main.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class MainActivityImpl : AppCompatActivity(), IMainActivityView, NavigationView.OnNavigationItemSelectedListener{
    private val TAG = "MainActivityImpl"
    private var presenter: MainActivityPresenter? = null
    private var mHotWin: HotSearchPopupWin? = null
    private var mHintWin: SearchHintWin? = null
    private var isHotWinShow = false
    private var isHintWinShow = false
    private var networkStateReceiver: NetWorkStateReceiver? = null
    private var cacheList = mutableListOf<String>()

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
            R.id.panel_night_mode -> {
               /** if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //recreate()
                val intent = Intent(this,this@MainActivityImpl::class.java)
                recreate() */
                toast(this,"暂未完善！")
            }
            R.id.panel_setting -> {
                val intent = Intent(this,SettingActivity::class.java)
                presenter?.jumpToTarget(ActionFlag.OTHER,intent)
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

        UserInfo.INSTANCE.isOnline = intent.getBooleanExtra("isOnline", false)
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

        presenter?.checkPermissions(permissions)

        networkStateReceiver = networkStateReceiver?: NetWorkStateReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkStateReceiver,intentFilter)

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
            // 若Fragment已经存在，比如屏幕旋转实例被保存，则不能add
            if (!it.value.isAdded) ft.add(R.id.fragment_container,it.value,it.key)
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
        searchView.queryHint = "输入搜索关键字"
        searchView.setOnSearchClickListener {
            presenter?.getHotWords()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchView.windowToken,0)
            navigation_view.requestFocus() // 为了收起软键盘
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(this@MainActivityImpl, CommomItemActivityImpl::class.java)
                intent.putExtra("search_key", query)
                presenter?.saveOrUpdateKeyWords(query)
                presenter?.jumpToTarget(ActionFlag.SEARCH, intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (TextUtils.isEmpty(newText)){
                    hideHintWin()
                    return true
                }
                launch(UI) {
                    val list = async {
                        // 用set是为了去重
                        val source = hashSetOf<String>()
                        cacheList.forEach { source.add(it) }
                        val res = presenter?.getMatchList(source,newText!!) ?: mutableListOf()
                        res
                    }.await()
                    showHintWin(list)
                }
                return true
            }
        })
        searchView.setOnCloseListener {
            hideHotWords()
            hideHintWin()
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun setPresenter() {
        presenter = MainActivityPresenter()
        presenter?.addView(this)
    }

    override fun getOnlineState() = UserInfo.INSTANCE.isOnline

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
        /**
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
        }*/
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
        } else if (isHotWinShow || isHintWinShow) {
            hideHotWords()
            hideHintWin()
        } else{
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && Build.VERSION.SDK_INT >= 23) {
            grantResults.forEach {
                if (it == PackageManager.PERMISSION_DENIED) {
                    if (!shouldShowRequestPermissionRationale(permissions[grantResults.indexOf(it)])) {
                        showWarnDialog(object: CommonDialogListener{
                            override val uuid = System.currentTimeMillis()
                            override fun onPositiveClick() {
                                val i = Intent()
                                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", packageName, null)
                                i.data = uri
                                startActivity(i)
                            }
                            override fun onNegativeClick() {
                                finish()
                            }
                        }, "为了软件正常运行，请允许申请的权限！", "警告","权限")
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    override fun showWarnDialog(listener: CommonDialogListener, message: String, title: String,tag: String): DialogFragment {
        val dialog = CommonDialogFragment.newInstance(title, message, listener)
        dialog.show(supportFragmentManager, tag)
        return dialog
    }

    override fun showHotWords(list: MutableList<String>) {
        isHotWinShow = true
        val hotWindow = HotSearchPopupWin(this)
        if (mHotWin == null) mHotWin = hotWindow
        cacheList.clear()

        launch(UI) {
            val result = async {
                val res = presenter?.getTopWords() ?: mutableListOf()
                res
            }.await()

            cacheList.addAll(list)
            cacheList.addAll(result)

            hotWindow.isErrorViewShow(list.isEmpty())
            if (!list.isEmpty()) hotWindow.addTitle("搜索热词")
            list.forEach {
                hotWindow.addWord(it)
            }
            hotWindow.addTitle("猜你想搜")
            hotWindow.isErrorViewShow(result.isEmpty())
            result.forEach {
                hotWindow.addWord(it)
            }

            hotWindow.showWindow(toolbar_base)

            generateMask()
            hotWindow.setOnDismissListener {
                removeMask()
            }

            hotWindow.addClickListener(object : HotSearchPopupWin.HotSearchPopupWinListener {
                override fun onClick(hotWord: String) {
                    doSearch(hotWord)
                }
            })
        }
    }

    override fun hideHotWords() {
        isHotWinShow = false
        mHotWin?.dismiss()
        mHotWin = null
    }

    override fun showHintWin(list: MutableList<String>) {
        if (mHintWin == null) {
            mHintWin = SearchHintWin(getActivityContext())
            mHintWin?.registerListener(object: SearchHintWin.SearchHitWinListener{
                override fun onClick(word: String) {
                    doSearch(word)
                }
            })
        }
        generateMask()
        isHintWinShow = true
        mHintWin?.addHintWords(list)
        mHintWin?.showWindow(toolbar_base)
    }

    override fun hideHintWin() {
        removeMask()
        mHintWin?.hideWindow()
        isHintWinShow = false
        mHintWin = null
    }

    fun doSearch(keyWords: String){
        val intent = Intent(this@MainActivityImpl,CommomItemActivityImpl::class.java)
        intent.putExtra("search_key",keyWords)
        presenter?.jumpToTarget(ActionFlag.SEARCH,intent)
    }

    fun generateMask(){
        //蒙层遮罩
        val contentView = window.decorView.findViewById<FrameLayout>(android.R.id.content)
        contentView.setBackgroundColor(resources.getColor(R.color.mask))
    }

    fun removeMask(){
        val contentView = window.decorView.findViewById<FrameLayout>(android.R.id.content)
        contentView.setBackgroundColor(Color.WHITE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkStateReceiver)
    }

    inner class NetWorkStateReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!presenter!!.checkNetworkState()){
                return
            }
            initFragments()
            Log.w("NetWorkState","网络状态改变")
        }
    }
}


