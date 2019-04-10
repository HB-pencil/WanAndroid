package com.example.shinelon.wanandroid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.modle.ChildData
import java.util.ArrayList

class StructPageFragment: Fragment() {
    var list : ArrayList<ChildData>? = null
    val TAG = "StructPageFragment"
    var listener: ItemClickListener? = null

    companion object {
        fun getInstance(bundle: Bundle?): StructPageFragment {
            val fragment = StructPageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list  = arguments?.getSerializable("struct_item") as ArrayList<ChildData>
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val  view = LayoutInflater.from(activity).inflate(R.layout.fragment_struct_item,container,false)
        val root = view.findViewById<LinearLayout>(R.id.fragment_struct_item_root)

        list?.forEach{
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(10,10,10,10)
            val text = Button(context)
            text.text = it.name
            text.layoutParams = params
            text.gravity = Gravity.CENTER
            text.setPadding(10,10,10,10)
            text.background = root.resources.getDrawable(R.drawable.ripple_struct)
            root.addView(text)
            val id = it.id
            text.setOnClickListener {
                listener?.onClick(id)
            }
            Log.d(TAG,"add a itemView")
        }
        return view
    }

    fun addItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    interface ItemClickListener{
        fun onClick(id: Int)
    }
}