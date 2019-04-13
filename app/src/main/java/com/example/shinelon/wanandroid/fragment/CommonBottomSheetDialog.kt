package com.example.shinelon.wanandroid.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.shinelon.wanandroid.R
import java.io.Serializable

class CommonBottomSheetDialog: BottomSheetDialogFragment(){

    companion object {
        fun newInstance(title: String, message: String?,listener: CommonDialogListener?): CommonBottomSheetDialog{
            val dialogFragment = CommonBottomSheetDialog()
            val bundle = Bundle()
            bundle.putString("title",title)
            bundle.putString("message",message)
            bundle.putSerializable("listener",listener)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val title = arguments?.getString("title")
        val message = arguments?.getString("message")
        val listener = arguments?.getSerializable("listener") as CommonBottomSheetDialogListener

        val dialog = BottomSheetDialog(context!!)
        val frameLayout = FrameLayout(context)
        LayoutInflater.from(context).inflate(R.layout.dialog_bottom,frameLayout,true)
        dialog.setContentView(frameLayout)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setTitle(title)
        return dialog
    }
}

/**
 * 托管该Fragment的Activity必须实现该接口
 */
interface CommonBottomSheetDialogListener: Serializable{
    val uuid: Long
    fun onPositiveClick()
    fun onNegativeClick()
}