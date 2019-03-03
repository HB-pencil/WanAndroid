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
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.shinelon.wanandroid.helper.BaseAdapter
import com.example.shinelon.wanandroid.helper.BaseViewHolder
import com.example.shinelon.wanandroid.modle.DataBean
import com.example.shinelon.wanandroid.modle.DatasBean
import com.example.shinelon.wanandroid.presenter.CollectedActivityPresenter
import com.example.shinelon.wanandroid.viewimp.ICollectedActivityView
import kotlinx.android.synthetic.main.activity_article_search.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class CollectedActivityImpl: AppCompatActivity(),ICollectedActivityView {
    var presenter: CollectedActivityPresenter? = null
    val TAG = "CollectedActivityImpl"
    val itemList = mutableListOf<Any>()
    private var currentPage = 0
    private var currentIndex = 0
    private var totalPage = 0
    var adapter: BaseAdapter? = null
    var isExecute = false
    var isLoading = false
    private var nowClick: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_search)
        setSupportActionBar(toolbar_base)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                        holder.getChildView<TextView>(R.id.article_title_item).text = article.title
                        holder.getChildView<TextView>(R.id.author_item).text = article.author
                        holder.getChildView<TextView>(R.id.article_category).text = "${article.chapterName}"
                        holder.getChildView<TextView>(R.id.article_time).text = article.niceDate
                        holder.getChildView<TextView>(R.id.time_occupy).text = "收藏时间"
                        holder.getChildView<Button>(R.id.article_tags_item).visibility = View.GONE
                        holder.getChildView<ImageView>(R.id.article_status).visibility = View.GONE
                    }
                }
            }

            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                nowClick = position
                if (position >= 0 && position < itemList.size-1) {
                    val item = itemList[position] as DatasBean
                    presenter?.loadWeb(item.link,item.originId)
                }
            }
        }
        recycler_view_search.adapter = adapter
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
                        currentPage + 1 <= totalPage) {
                    Log.e(TAG,"$currentPage  $totalPage")
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
                            presenter?.getCollectedArticles(currentPage++)
                        }, 500)
                        isLoading = true
                    }
                } else {
                    animator?.end()
                    loadView?.visibility = View.INVISIBLE
                }
            }
        })

        presenter?.getCollectedArticles(currentPage)
    }

    override fun setPresenter() {
        presenter = CollectedActivityPresenter()
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
        article_search_load.animate()
                .rotation(360F)
                .setDuration(60000)
                .start()
    }

    override fun hideLoadingView() {
        article_search_load.animate().cancel()
        article_search_load.visibility = View.INVISIBLE
    }

    override fun createContentView(dataBean: DataBean?) {
        if (!isExecute) {
            hideLoadingView()
            isExecute = true
        }
        if (dataBean == null) {
            return
        }
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
        currentIndex += dataBean.datas.size
        Log.i(TAG, "itemList size: ${itemList.size}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            6 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data!!.getBooleanExtra("isCollected", false)
                    if (!result) {
                        itemList.removeAt(nowClick)
                        adapter!!.notifyItemRemoved(nowClick)
                        Log.i(TAG, "<<<<<<<信息已经得到更新>>>>>>> ")
                    }
                }
            }//最新文章页
        }
    }
}