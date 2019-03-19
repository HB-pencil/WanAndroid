package com.example.shinelon.wanandroid

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.shinelon.wanandroid.helper.BaseAdapter
import com.example.shinelon.wanandroid.helper.BaseViewHolder
import com.example.shinelon.wanandroid.modle.DataBean
import com.example.shinelon.wanandroid.modle.DatasBean
import com.example.shinelon.wanandroid.presenter.CommonItemActivityPresenter
import com.example.shinelon.wanandroid.viewimp.ICommonItemActivityView
import kotlinx.android.synthetic.main.activity_article_search.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class CommomItemActivityImpl : AppCompatActivity(),ICommonItemActivityView{
    var presenter: CommonItemActivityPresenter? = null
    val TAG = "SearchArticleActivityP"
    val itemList = mutableListOf<Any>()
    private var currentPage = 0
    private var currentIndex = 0
    private var totalPage = 0
    var adapter: BaseAdapter? = null
    var isExecute = false
    var isLoading = false
    var k: String = ""
    var cid: Int = -1
    private var nowClick: Int = -1
    var animate: ObjectAnimator? = null

    val options = RequestOptions()
            .error(R.drawable.error_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            //.placeholder(R.drawable.loading)
            .fitCenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_search)
        setSupportActionBar(toolbar_base)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        k = intent.getStringExtra("search_key")?: ""
        cid = intent.getIntExtra("cid",-1)

        if (presenter == null) setPresenter()

        recycler_view_search.layoutManager = LinearLayoutManager(this)

        itemList.add(Any())
        adapter = object : BaseAdapter(itemList){
            override fun getItemLayoutId(position: Int): Int {
                when(position){
                    itemList.size -1 -> return R.layout.article_item_load_more
                    else -> return R.layout.article_item_layout
                }
            }

            override fun bindData(holder: BaseViewHolder, position: Int) {
                when(position){
                    itemList.size-1 -> Unit
                    else -> {
                        val article = itemList[position] as DatasBean
                        holder.getChildView<TextView>(R.id.article_title_item).text = Html.fromHtml(article.title)
                        holder.getChildView<ImageView>(R.id.article_status).setImageDrawable(if (article.collect) resources.getDrawable(R.drawable.love_red, theme)
                        else resources.getDrawable(R.drawable.love_black, theme))
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
                        val img = holder.getChildView<ImageView>(R.id.imgPic)
                        if (TextUtils.isEmpty(article.envelopePic)) return
                        Glide.with(getActivityContext())
                                .load(article.envelopePic)
                                .apply(options)
                                .into(img)
                    }
                }
            }

            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                nowClick = position
                if (position >= 0 && position < itemList.size-1) {
                    val item = itemList[position] as DatasBean
                    presenter?.loadWeb(item.link,item.collect,item.id)
                }
            }
        }
        recycler_view_search.adapter = adapter
        val resId = R.anim.animation_list
        val animation = AnimationUtils.loadLayoutAnimation(this,resId)
        recycler_view_search.layoutAnimation = animation

        showLoadingView()

        val layoutManager = recycler_view_search.layoutManager as LinearLayoutManager
        recycler_view_search.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //滑到底部加载更多
                var animator: ObjectAnimator? = null
                var loadView: ImageView? = null
                var loadErrView: TextView? = null
                if (dy > 0 && layoutManager.findFirstVisibleItemPosition() > 0 &&
                        layoutManager.findLastVisibleItemPosition() == itemList.size - 1 &&
                        currentPage + 1 < totalPage) {
                    loadView = recyclerView?.getChildAt(recyclerView.childCount - 1)?.findViewById(R.id.article_item_load_more)
                    loadErrView = recyclerView?.getChildAt(recyclerView.childCount - 1)?.findViewById(R.id.article_item_load_more_error)
                    loadErrView?.visibility = View.INVISIBLE

                    if (!isLoading) {
                        animator = ObjectAnimator.ofFloat(loadView!!, "rotation", 0F, 360F)
                        animator.duration = 1000
                        animator.repeatMode = ValueAnimator.RESTART
                        animator.repeatCount = ValueAnimator.INFINITE
                        loadView.visibility = View.VISIBLE
                        animator.start()
                        Handler().postDelayed({
                            currentPage++
                            sendQuestByJudge()
                        }, 500)
                        isLoading = true
                    }
                } else {
                    Log.d(TAG,"加载结束")
                    animator?.end()
                    loadView?.visibility = View.INVISIBLE
                }
            }
        })
        sendQuestByJudge()
    }

    fun sendQuestByJudge() {
        if (!TextUtils.isEmpty(k)) {
            presenter?.getSearchArticle(currentPage,k)
        } else if (cid >= 0) {
            presenter?.getStructProjectItem(currentPage,cid)
        }
    }

    override fun setPresenter() {
        presenter = CommonItemActivityPresenter()
        presenter?.addView(this)
    }

    override fun getActivityContext() = this

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLoadingView() {
        article_search_load.visibility = View.VISIBLE
        animate = ObjectAnimator.ofFloat(article_search_load,"rotation",0F,360F)
        animate?.repeatCount = ValueAnimator.INFINITE
        animate?.repeatMode = ValueAnimator.RESTART
        animate?.start()
    }

    override fun hideLoadingView() {
        animate?.cancel()
        article_search_load.visibility = View.INVISIBLE
    }

    override fun createContentView(dataBean: DataBean?) {
        if (!isExecute) {
            hideLoadingView()
            isExecute = true
        }
        if (dataBean == null) {
            hideLoadingView()
            showErrorView()
            return
        }
        hideErrorView()

        currentPage = dataBean.curPage
        totalPage = dataBean.pageCount
        val articles = mutableListOf<DatasBean>()
        dataBean.datas.forEach {
            articles.add(it)
        }
        if (articles.isEmpty()) return
        //插入数据源
        itemList.addAll(currentIndex, articles)
        adapter!!.notifyItemInserted(currentIndex)
        recycler_view_search.scheduleLayoutAnimation()
        currentIndex += dataBean.datas.size
        Log.i(TAG, "itemList size: ${itemList.size}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            5 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data!!.getBooleanExtra("isCollected", false)
                    if (nowClick >= 0) {
                        changeLoveView(result)
                        Log.i(TAG, "<<<<<<<信息已经得到更新>>>>>>>")
                    }
                }
            }//最新文章页
        }
    }

    override fun changeLoveView(isCollected: Boolean) {
        val item = itemList[nowClick] as DatasBean
        item.collect = isCollected
        adapter?.notifyItemChanged(nowClick)
    }

    override fun showErrorView() {
        article_search_error.visibility = View.VISIBLE
    }

    override fun hideErrorView() {
        article_search_error.visibility = View.INVISIBLE
    }
}
