package com.example.shinelon.wanandroid.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.shinelon.wanandroid.CommomItemActivityImpl
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.StructPageAdapter
import com.example.shinelon.wanandroid.modle.SuperData
import com.example.shinelon.wanandroid.presenter.StructPresenter
import com.example.shinelon.wanandroid.viewimp.IStructFragmentView
import kotlinx.android.synthetic.main.fragment_struct.*

class IStructFragmentImpl: BaseFragment(),IStructFragmentView,StructPageFragment.ItemClickListener {
    var presenter: StructPresenter? = null
    val TAG = "IStructFragmentImpl"
    val tabList = mutableListOf<String>()
    val pageList = mutableListOf<Fragment>()
    var viewPager: ViewPager? = null

    companion object {
        fun getInstance(bundle: Bundle?): IStructFragmentImpl {
            val fragment = IStructFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId() = R.layout.fragment_struct

    override fun init() {
        viewPager = rootView?.findViewById(R.id.struct_view_pager)
        presenter?.getStructDetails()
    }

    override fun setPresenter() {
        presenter  = StructPresenter()
        presenter?.addView(this)
    }

    override fun showErrorView() {
        rootView!!.findViewById<TextView>(R.id.load_error).visibility = View.VISIBLE
    }

    override fun hideErrorView() {
        rootView!!.findViewById<TextView>(R.id.load_error).visibility = View.INVISIBLE
    }

    override fun initPages(data: MutableList<SuperData>) {
        data.forEach {
            tabList.add(it.name)
            val bundle = Bundle()
            bundle.putSerializable("struct_item",it.children)
            val page = StructPageFragment.getInstance(bundle)
            page.addItemClickListener(this)
            pageList.add(page)
        }
            viewPager?.adapter = StructPageAdapter(pageList,tabList,activity!!.supportFragmentManager)
            struct_tab.setupWithViewPager(struct_view_pager)
    }

    override fun onClick(id: Int) {
        Log.d(TAG,"分级 $id")
        val intent = Intent(activity,CommomItemActivityImpl::class.java)
        intent.putExtra("cid",id)
        presenter?.jumpToTarget(ActionFlag.OTHER,intent)
    }
}