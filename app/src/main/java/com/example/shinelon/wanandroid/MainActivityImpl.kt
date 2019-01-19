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
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.example.shinelon.wanandroid.fragment.CommonDialogFragment
import com.example.shinelon.wanandroid.fragment.CommonDialogListener
import com.example.shinelon.wanandroid.fragment.HotSearchPopupWin
import com.example.shinelon.wanandroid.helper.NavigationViewhelper
import com.example.shinelon.wanandroid.helper.ViewPagerAdapter
import com.example.shinelon.wanandroid.modle.DataBeanBanner
import com.example.shinelon.wanandroid.presenter.MainActivityPresenter
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.header_layout_main.view.*
import kotlinx.android.synthetic.main.view_pager_item.view.*


class MainActivityImpl : AppCompatActivity(), IMainActivityView, NavigationView.OnNavigationItemSelectedListener,
        CommonDialogListener {
    val TAG = "MainActivityImpl"
    var presenter: MainActivityPresenter? = null
    var isOnline = false
    var mWindow: HotSearchPopupWin? = null
    val list = mutableListOf<View>()
    val listBannerUrl = mutableListOf<String>()
    var adapter: ViewPagerAdapter? = null
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

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar_base, R.string.open_navigation, R.string.close_navigation)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        if (presenter == null) setPresenter()
        val permissions = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
        presenter?.checkPermissions(permissions)

        navigation_view.setNavigationItemSelectedListener(this)

        navigation_bottom.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        NavigationViewhelper.disableShiftMode(navigation_bottom)

        setOnlineState(intent.getBooleanExtra("isOnline", false))
        val stateTv = navigation_view.getHeaderView(0).state_tv
        stateTv.setOnClickListener {
            if (!getOnlineState()) {
                presenter?.login()
            } else {
                presenter?.logout()
            }
        }

        //占位
        val view = LayoutInflater.from(this).inflate(R.layout.view_pager_item,null,false)
        view.banner_image_item.setImageDrawable(resources.getDrawable(R.drawable.loading))
        view.banner_image_title.text = ""
        list.add(view)
        listBannerUrl.add("")
        adapter = ViewPagerAdapter(list)
        viewpager_main.adapter = adapter
        viewpager_main.currentItem = Integer.MAX_VALUE/2
        //这里不能用lambdas，我佛了
        adapter?.addItemClickListener(object: ViewPagerAdapter.OnItemClickListener{
            override fun onItemClick(realPosition: Int) {
                presenter?.onPageItemClick(listBannerUrl[realPosition])
            }
        })

        //真正初始化list
        presenter?.getBanner()

        val handler = Handler()
        val runnable = object: Runnable{
            override fun run() {
                viewpager_main.currentItem = ++viewpager_main.currentItem % Integer.MAX_VALUE
                handler.postDelayed(this,4000)
            }
        }
        handler.postDelayed(runnable,4000)
    }

    override fun onResume() {
        super.onResume()
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
        }
        presenter?.checkAutoLogin()
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

    override fun getOnlineState() = isOnline

    override fun setOnlineState(isOnline: Boolean) {
        this.isOnline = isOnline
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
        Log.d(TAG, "name:$name;isOnline:$isOnline")
        if (intent != null) updateHeaderView(isOnline!!, name)
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
                        showWarnDialog()
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    fun showWarnDialog() {
        val dialog = CommonDialogFragment.newInstance("警告", "为了让软件正常工作，请您允许通过申请的权限，否则将无法提供服务！",
                this)
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
    }

    override fun hideHotWords() {
        mWindow?.dismiss()
        mWindow = null
    }

    //TODO 目前加载时间过长，后续优化
    override fun createBannerView(mutableList: MutableList<DataBeanBanner>): MutableList<View> {
        val options = RequestOptions()
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.loading)
                .fitCenter()
        if(!list.isEmpty() && !mutableList.isEmpty()) list.clear()
        mutableList.forEach {
            val view = LayoutInflater.from(this).inflate(R.layout.view_pager_item,null,false)
            view.banner_image_title.text = it.title
            Glide.with(this)
                    .load(it.imagePath)
                    .apply(options)
                    .into(view.banner_image_item)
            list.add(view)
            listBannerUrl.add(it.url)
            Log.w(TAG,"${it.imagePath}")
        }
        adapter?.notifyDataSetChanged()
        return list
    }
}


