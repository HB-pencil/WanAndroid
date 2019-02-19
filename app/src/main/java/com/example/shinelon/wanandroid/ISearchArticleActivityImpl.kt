package com.example.shinelon.wanandroid

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.shinelon.wanandroid.helper.BaseAdapter
import com.example.shinelon.wanandroid.helper.BaseViewHolder
import com.example.shinelon.wanandroid.modle.DatasBean
import com.example.shinelon.wanandroid.presenter.ISearchArticleActivityPresebter
import com.example.shinelon.wanandroid.viewimp.ISearchArticleActivityView
import kotlinx.android.synthetic.main.activity_article_search.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class ISearchArticleActivityImpl : AppCompatActivity(),ISearchArticleActivityView{
    var presenter: ISearchArticleActivityPresebter? = null
    val itemList = mutableListOf<Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_search)
        setSupportActionBar(toolbar_base)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_view_search.layoutManager = LinearLayoutManager(this)

        itemList.add(Any())
        itemList.add(Any())
        recycler_view_search.adapter = object : BaseAdapter(itemList){
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

            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                if (position > 0 && position < itemList.size-1) {
                    val item = itemList[position] as DatasBean
                    presenter?.loadWeb(item.link)
                }
            }
        }
    }

    override fun setPresenter() {
        presenter = ISearchArticleActivityPresebter()
    }

    override fun getActivityContext() = this

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
