package com.example.shinelon.wanandroid.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.helper.BaseAdapter
import com.example.shinelon.wanandroid.helper.BaseViewHolder
import com.example.shinelon.wanandroid.helper.ViewPagerAdapter
import com.example.shinelon.wanandroid.modle.DataBean
import com.example.shinelon.wanandroid.modle.DataBeanBanner
import com.example.shinelon.wanandroid.modle.DatasBean
import com.example.shinelon.wanandroid.presenter.IHomeFragmentPresenter
import com.example.shinelon.wanandroid.viewimp.IHomeFragmentView
import kotlinx.android.synthetic.main.article_item_banner.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_pager_item.view.*
import java.util.*

class IHomeFragmentImpl : BaseFragment(), IHomeFragmentView {
    var presenter: IHomeFragmentPresenter? = null
    val TAG = "IHomeFragmentImpl"
    private val viewList = mutableListOf<View>()
    private val itemList = mutableListOf<Any>()
    private val listBannerUrl = mutableListOf<String>()
    private var vpAdapter: ViewPagerAdapter? = null
    private var itemBannerV: View? = null
    private var currentPage = 0
    private var rcyvAdapter: BaseAdapter? = null
    private var isLoading = false
    private var currentIndex = 1
    private var recyclerView: RecyclerView? = null

    companion object {
        fun getInstance(bundle: Bundle?): IHomeFragmentImpl {
            val fragment = IHomeFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(id: Int) = R.layout.fragment_home

    override fun setPresenter() {
        presenter = IHomeFragmentPresenter()
        presenter?.addView(this)
    }

    override fun init() {
        //此处两个为占位，分别是banner和load,load默认不可见
        itemList.add(Any())
        itemList.add(Any())
        recyclerView = rootView!!.findViewById(R.id.recycler_view_home)
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
                if (position > 0 && position < itemList.size - 1) {
                    val item = itemList[position] as DatasBean
                    presenter?.loadWeb(item.link)
                }
            }
        }
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = rcyvAdapter

        Handler().postDelayed({
            presenter?.getBanner()
            presenter?.getArticleList(currentPage)
        }, 500)

        val layoutManager = recyclerView?.layoutManager as LinearLayoutManager


        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

                    if (!isLoading) {
                        animator = ObjectAnimator.ofFloat(loadView!!, "rotation", 0F, 360F)
                        animator.duration = 1000
                        animator.repeatMode = ValueAnimator.RESTART
                        animator.repeatCount = ValueAnimator.INFINITE
                        loadView.visibility = View.VISIBLE
                        animator.start()
                        Handler().postDelayed({
                            presenter?.getArticleList(currentPage++)
                        }, 500)
                        isLoading = true
                    }
                } else {
                    animator?.end()
                    loadView?.visibility = View.INVISIBLE
                }
            }
        })
    }
    override fun createBannerView(mutableList: MutableList<DataBeanBanner>) {
        if (mutableList.isEmpty()) return
        val options = RequestOptions()
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.loading)
                .fitCenter()
        mutableList.forEach {
            val view = LayoutInflater.from(activity).inflate(R.layout.view_pager_item, null, false)
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
        itemBannerV?.viewpager_home?.adapter = vpAdapter
        //此处取值过大的话会导致ANR
        itemBannerV?.viewpager_home?.currentItem = 105
        //这里不能用lambdas，我佛了
        vpAdapter?.addItemClickListener(object : ViewPagerAdapter.OnItemClickListener {
            override fun onItemClick(realPosition: Int) {
                presenter?.onPageItemClick(listBannerUrl[realPosition])
            }
        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    itemBannerV!!.viewpager_home!!.currentItem = ++itemBannerV!!.viewpager_home!!.currentItem
                }
            }
        }, 3000, 3000)
        itemBannerV!!.home_occupy.visibility = View.GONE
    }

    override fun createContentView(data: DataBean?) {
        if (data == null) {
            hideLoadMoreView()
            showLoadMoreErrorView()
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

    override fun showLoadMoreView() {
        val loadView = recyclerView!!.getChildAt(recyclerView!!.childCount - 1).findViewById<ImageView>(R.id.article_item_load_more)
        loadView.visibility = View.VISIBLE
    }

    override fun hideLoadMoreView() {
        val loadView = recyclerView!!.getChildAt(recyclerView!!.childCount - 1).findViewById<ImageView>(R.id.article_item_load_more)
        loadView.visibility = View.INVISIBLE
    }

    override fun showLoadMoreErrorView() {
        val loadErrView = recyclerView!!.getChildAt(recyclerView!!.childCount - 1).findViewById<TextView>(R.id.article_item_load_more_error)
        loadErrView.visibility = View.VISIBLE
    }

    override fun hideLoadMoreErrorView() {
        val loadErrView = recyclerView!!.getChildAt(recyclerView!!.childCount - 1).findViewById<TextView>(R.id.article_item_load_more_error)
        loadErrView.visibility = View.INVISIBLE
    }
}