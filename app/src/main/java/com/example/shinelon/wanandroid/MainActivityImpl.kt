package com.example.shinelon.wanandroid

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.request.RequestOptions
import com.example.shinelon.wanandroid.fragment.CommonDialogFragment
import com.example.shinelon.wanandroid.fragment.CommonDialogListener
import com.example.shinelon.wanandroid.fragment.HotSearchPopupWin
import com.example.shinelon.wanandroid.helper.BaseAdapter
import com.example.shinelon.wanandroid.helper.BaseViewHolder
import com.example.shinelon.wanandroid.helper.NavigationViewhelper
import com.example.shinelon.wanandroid.helper.ViewPagerAdapter
import com.example.shinelon.wanandroid.modle.DataBean
import com.example.shinelon.wanandroid.modle.DataBeanBanner
import com.example.shinelon.wanandroid.modle.DatasBean
import com.example.shinelon.wanandroid.presenter.MainActivityPresenter
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.article_item_banner.view.*
import kotlinx.android.synthetic.main.header_layout_main.view.*
import kotlinx.android.synthetic.main.view_pager_item.view.*
import java.util.*

class MainActivityImpl : AppCompatActivity(), IMainActivityView, NavigationView.OnNavigationItemSelectedListener,
        CommonDialogListener {
    private val TAG = "MainActivityImpl"

    private var presenter: MainActivityPresenter? = null
    private var isOnline = false
    private var mWindow: HotSearchPopupWin? = null
    private val viewList = mutableListOf<View>()
    private val itemList = mutableListOf<Any>()
    private val listBannerUrl = mutableListOf<String>()
    private var vpAdapter: ViewPagerAdapter? = null
    private var itemBannerV: View? = null
    private var currentPage = 0
    private var rcyvAdapter: BaseAdapter? = null
    private var isLoading = false
    private var currentIndex = 1

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
                Color.GREEN
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
        //此处两个为占位，分别是banner和load,load默认不可见
        itemList.add(Any())
        itemList.add(Any())

        rcyvAdapter = object : BaseAdapter(itemList) {
            override fun bindData(holder: BaseViewHolder, position: Int) {
                when (position) {
                    0 -> itemBannerV = holder.itemView //这里获得Banner这个item的View方便添加
                    itemList.size - 1 -> Unit
                    else -> {
                        val article = itemList[position] as DatasBean
                        holder.getChildView<TextView>(R.id.article_title_item).text = article.title
                        if (article.tags.size > 0) {
                            val sbf = StringBuffer()
                            article.tags.forEach {
                                sbf.append("${it.name} ")
                            }
                            holder.getChildView<Button>(R.id.article_tags_item).text = sbf.toString()
                            holder.getChildView<Button>(R.id.article_tags_item).visibility = View.VISIBLE
                        } else {
                            holder.getChildView<Button>(R.id.article_tags_item).visibility = View.INVISIBLE
                        }
                        holder.getChildView<TextView>(R.id.author_item).text = article.author
                        holder.getChildView<TextView>(R.id.article_category).text = "${article.superChapterName} ${article.chapterName}"
                        holder.getChildView<TextView>(R.id.article_time).text = article.niceDate
                    }
                }
            }

            override fun getItemLayoutId(position: Int): Int {
                when (position) {
                    0 -> return R.layout.article_item_banner
                    itemList.size - 1 -> return R.layout.article_item_load_more
                    else -> return R.layout.article_item_layout
                }
            }

            override fun onItemClick(position: Int) {
                super.onItemClick(position)
            }
        }
        recycler_view_main.layoutManager = LinearLayoutManager(this)
        recycler_view_main.adapter = rcyvAdapter

        Handler().postDelayed({
            presenter?.getBanner()
            presenter?.getArticleList(currentPage)
        }, 500)

        val layoutManager = recycler_view_main.layoutManager as LinearLayoutManager


        recycler_view_main.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //滑到底部加载更多
                var animator: ObjectAnimator? = null
                var loadView: ImageView? = null
                var loadErrView: TextView? = null
                if (dy > 0 && layoutManager.findFirstVisibleItemPosition() > 0 &&
                        layoutManager.findLastVisibleItemPosition() == itemList.size - 1) {
                    loadView = recyclerView?.getChildAt(recyclerView.childCount - 1)?.findViewById(R.id.article_item_load_more)
                    loadErrView = recyclerView?.getChildAt(recyclerView.childCount - 1)?.findViewById(R.id.article_item_load_more_error)
                    loadErrView?.visibility = View.INVISIBLE

                    animator = ObjectAnimator.ofFloat(loadView!!, "rotation", 0F, 360F)
                    animator.duration = 1000
                    animator.repeatMode = RESTART
                    animator.repeatCount = INFINITE
                    animator.start()
                    loadView.visibility = View.VISIBLE
                    if (!isLoading) {
                        Handler().postDelayed({
                            presenter?.getArticleList(currentPage++)
                        }, 500)
                    }
                } else {
                    animator?.end()
                    loadView?.visibility = View.INVISIBLE
                }
            }
        })

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

    override fun createBannerView(mutableList: MutableList<DataBeanBanner>) {
        if (mutableList.isEmpty()) return
        val options = RequestOptions()
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.loading)
                .fitCenter()
        mutableList.forEach {
            val view = LayoutInflater.from(this).inflate(R.layout.view_pager_item, null, false)
            view.banner_image_title.text = it.title
            Glide.with(this)
                    .load(it.imagePath)
                    .apply(options)
                    .into(view.banner_image_item)
            viewList.add(view)
            listBannerUrl.add(it.url)
        }
        startBanner()
    }

    fun startBanner() {
        if (viewList.isEmpty() || itemBannerV == null) return
        vpAdapter = ViewPagerAdapter(viewList)
        itemBannerV?.viewpager_main?.adapter = vpAdapter
        //此处取值过大的话会导致ANR
        itemBannerV?.viewpager_main?.currentItem = 2100
        //这里不能用lambdas，我佛了
        vpAdapter?.addItemClickListener(object : ViewPagerAdapter.OnItemClickListener {
            override fun onItemClick(realPosition: Int) {
                presenter?.onPageItemClick(listBannerUrl[realPosition])
            }
        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    //TODO 此处不知为什么失效了
                    itemBannerV!!.viewpager_main!!.currentItem = itemBannerV!!.viewpager_main!!.currentItem++
                }
            }
        }, 3000)
        itemBannerV!!.main_occupy.visibility = View.GONE
    }

    override fun createContentView(data: DataBean?) {
        if (data == null) {
            val loadView = recycler_view_main.getChildAt(recycler_view_main.childCount - 1).findViewById<ImageView>(R.id.article_item_load_more)
            loadView.visibility = View.INVISIBLE
            val loadErrView = recycler_view_main.getChildAt(recycler_view_main.childCount - 1).findViewById<TextView>(R.id.article_item_load_more_error)
            loadErrView.visibility = View.VISIBLE
            return
        }
        currentPage = data.curPage
        val articles = mutableListOf<DatasBean>()
        data.datas.forEach {
            articles.add(it)
        }
        //插入数据源
        itemList.addAll(currentIndex, articles)
        rcyvAdapter!!.notifyItemInserted(currentIndex)
        currentIndex += data.datas.size
        Log.i(TAG, "itemList size: ${itemList.size}")
    }
}


